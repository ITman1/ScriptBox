package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectMembers;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

/*
 * Creates scope for java native object.
 */
public class HostedJavaObject extends ScriptableObject implements Wrapper {

	private static final long serialVersionUID = 6761328943903362404L;

	protected Object javaObject;
	protected Class<?> javaObjectType;
	
	protected boolean hasNonObjectGetterGet;
		
	protected ObjectMembers objectMembers;
	
	public HostedJavaObject(Scriptable scope, Object javaObject) {
		this(scope, ObjectMembers.getObjectMembers(javaObject));
	}
	
	public HostedJavaObject(Scriptable scope, ObjectMembers objectMembers) {
		super(scope, null);
		
		this.objectMembers = objectMembers;
		this.javaObject = objectMembers.getObject();
		this.javaObjectType = this.javaObject.getClass();
		
		if (javaObject instanceof ObjectGetter) {
			Class<?>[] getterArgs = ObjectGetter.METHOD_ARG_TYPES;
			String getterName = ObjectGetter.METHOD_NAME;
			for (Method method : javaObjectType.getMethods()) {
				String methodName = method.getName();
				Class<?>[] methodParams = method.getParameterTypes();
				if (methodName.equals(getterName) && !Arrays.equals(methodParams, getterArgs)) {
					hasNonObjectGetterGet = true;
					break;
				}
			}
		}
	}
	
	@Override
	public String getClassName() {
		return "HostedJavaObject";
	}
	
	@Override
	public Object get(int index, Scriptable start) {		
		Object object;
		
		object = super.get(index, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(index) : object;
		
		if (object != Scriptable.NOT_FOUND) {
			object = wrapObject(object);
		}
		
		return object;
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object;
		
		object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? hostGet(name) : object;
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(name) : object;
		
		if (object != Scriptable.NOT_FOUND) {
			object = wrapObject(object);
		}
		
		return object;
	}
		
	@Override
	public void put(String name, Scriptable start, Object value) {
		if (objectMembers.hasMemberWithName(name)) {
			hostPut(name, value);
		} else {
			super.put(name, start, value);
		}
	}
	
	@Override
	public void delete(String name) {
		if (objectMembers.hasMemberWithName(name)) {
			hostDelete(name);
		} else {
			super.delete(name);
		}
	}
	
	@Override
	public boolean has(String name, Scriptable start) {
		boolean hasProperty = super.has(name, start);
				
		if (hasProperty && name.equals(ObjectGetter.METHOD_NAME)) {
			return hasNonObjectGetterGet;
		}
		
		return (hasProperty)? true : objectMembers.hasMemberWithName(name);
	}
	
	@Override
	public Object[] getIds() {
		Object[] superIds = super.getAllIds();
		return getIds(objectMembers, superIds);
	}
	
	@Override
	public Object unwrap() {
		if (javaObject instanceof org.fit.cssbox.scriptbox.script.Wrapper) {
			return ((org.fit.cssbox.scriptbox.script.Wrapper<?>)javaObject).unwrap();
		}
		return javaObject;
	}
	
	@Override
	public String toString() {
		return javaObject.toString();
	}

	protected Object wrapObject(Object object) {		
		return ObjectScriptable.javaToJS(object, this);
	}
	
	protected void hostDelete(String name) {
		// TODO: It can be feature
		throw new FieldException("Deleting host properties is not supported");
	}
	
	protected void hostPut(String name, Object value) {
		if (objectMembers.hasMemberWithName(name)) {
			Set<ClassMember<?>> members = objectMembers.getMembersByName(name);
			
			if (members == null || members.isEmpty()) {
				throw new FieldException("Scope does not contain property with this name!");
			}

			ClassMember<?> firstMember = members.iterator().next();
			if (firstMember instanceof ClassField) {
				hostFieldPut((ClassField)firstMember, value);
				return;
			} else {
				throw new FieldException("Unsupported operation");
			}
		}
		throw new FieldException("Scope does not contain property with this name!");
	}
	
	protected void hostFieldPut(ClassField objectField, Object value) {
		objectField.set(this, value);
	}
	
	protected Object hostGet(String name) {
		Object result = Scriptable.NOT_FOUND;
		
		if (objectMembers.hasMemberWithName(name)) {
			Set<ClassMember<?>> members = objectMembers.getMembersByName(name);
			
			if (members == null || members.isEmpty()) {
				return Scriptable.NOT_FOUND;
			}
			
			result = wrapGet(members);
		}
		
		return result;
	}
	
	protected Object wrapGet(Set<ClassMember<?>> members) {
		Object result = Scriptable.NOT_FOUND;
		ClassMember<?> firstMember = members.iterator().next();
		if (firstMember instanceof ClassField) {
			result = hostFieldGet((ClassField)firstMember);
		} else if (firstMember instanceof ClassFunction) {
			Set<ClassFunction> functions = new HashSet<ClassFunction>();
			boolean failed = false;
			for (ClassMember<?> member : members) {
				if (member instanceof ClassFunction) {
					functions.add((ClassFunction)member);
				} else {
					failed = true;
					break;
				}
			}
			if (!failed) {
				result = hostFunctionGet(functions);
			}
		}
		
		return result;

	}
	
	protected Object hostFieldGet(ClassField objectField) {
		return objectField.get(javaObject);
	}
	
	protected Object hostFunctionGet(Set<ClassFunction> functions) {
		return new HostedJavaMethod(this, javaObject, functions);
	}
	
	protected Object objectGetterGet(Object key) {
		Method method = null;
		Object result = Scriptable.NOT_FOUND;
		
		if (javaObject instanceof ObjectGetter) {
			Object value = ((ObjectGetter)javaObject).get(key);
			
			try {
				method = ClassUtils.getPublicMethod(javaObjectType, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
			} catch (Exception e) {
				throw new InternalException(e);
			}
			
			if (value != ObjectGetter.UNDEFINED_VALUE) {
				result = value;
			}
		}
		
		if (result != Scriptable.NOT_FOUND && method != null) {
			//result = new JavaMethodRedirectedWrapper(result, javaObject, method);
		}
		
		return result;
	}
	
	public static Object[] getIds(ObjectMembers objectMembers, Object[] superIds) {
		Set<String> membersNames = objectMembers.getEnumerableMemberNames();
		
		Object[] returnIds = new Object[membersNames.size() + superIds.length];

		int i = 0;
		for (; i < superIds.length; i++) {
			returnIds[i] = superIds[i];
		}
		
		for (String name : membersNames) {
			returnIds[i] = name;
			i++;
		}
		
		return returnIds;
	}
}

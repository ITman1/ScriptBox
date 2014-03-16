package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectField;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectMember;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectMembers;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

public class HostedJavaObject extends ObjectScriptable implements Wrapper {

	private static final long serialVersionUID = 6761328943903362404L;

	protected Object javaObject;
	protected Class<?> javaObjectType;
	
	protected boolean hasNonObjectGetterGet;
		
	protected ObjectMembers objectMembers;
	
	public HostedJavaObject(Scriptable scope, Object javaObject) {
		this(scope, getObjectMembers(javaObject));
	}
	
	public HostedJavaObject(Scriptable scope, ObjectMembers objectMembers) {
		super(scope, null);
		
		this.objectMembers = objectMembers;
		this.javaObject = objectMembers.getObject();
		this.javaObjectType = objectMembers.getObjectType();
		
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
	public Object get(int index, Scriptable start) {		
		Object object;
		
		object = super.get(index, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(index) : object;
		
		return wrapObject(object);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object;
		
		object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? hostGet(name) : object;
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(name) : object;
		
		return wrapObject(object);
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
		Set<String> membersNames = objectMembers.getEnumerableMemberNames();
		Object[] superIds = super.getAllIds();
		
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
	
	@Override
	public Object unwrap() {
		return javaObject;
	}

	protected Object wrapObject(Object object) {
		if (object instanceof Scriptable) {
			return object;
		}
		
		if (object != Scriptable.NOT_FOUND) {
			object = Context.javaToJS(object, this);
		}
		
		return object;
	}
	
	protected void hostDelete(String name) {
		// TODO: It can be feature
		throw new FieldException("Deleting host properties is not supported");
	}
	
	protected void hostPut(String name, Object value) {
		if (objectMembers.hasMemberWithName(name)) {
			Set<ObjectMember<?>> members = objectMembers.getMembersByName(name);
			
			if (members == null || members.isEmpty()) {
				throw new FieldException("Scope does not contain property with this name!");
			}

			ObjectMember<?> firstMember = members.iterator().next();
			if (firstMember instanceof ObjectField) {
				hostFieldPut((ObjectField)firstMember, value);
				return;
			} else {
				throw new FieldException("Unsupported operation");
			}
		}
		throw new FieldException("Scope does not contain property with this name!");
	}
	
	protected void hostFieldPut(ObjectField objectField, Object value) {
		objectField.setter(this, value);
	}
	
	protected Object hostGet(String name) {
		Object result = Scriptable.NOT_FOUND;
		
		if (objectMembers.hasMemberWithName(name)) {
			Set<ObjectMember<?>> members = objectMembers.getMembersByName(name);
			
			if (members == null || members.isEmpty()) {
				return Scriptable.NOT_FOUND;
			}
			
			result = wrapGet(members);
		}
		
		return result;
	}
	
	protected Object wrapGet(Set<ObjectMember<?>> members) {
		Object result = Scriptable.NOT_FOUND;
		ObjectMember<?> firstMember = members.iterator().next();
		if (firstMember instanceof ObjectField) {
			result = hostFieldGet((ObjectField)firstMember);
		} else if (firstMember instanceof ObjectFunction) {
			Set<ObjectFunction> functions = new HashSet<ObjectFunction>();
			boolean failed = false;
			for (ObjectMember<?> member : members) {
				if (member instanceof ObjectFunction) {
					functions.add((ObjectFunction)member);
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
	
	protected Object hostFieldGet(ObjectField objectField) {
		return objectField.gettter(this);
	}
	
	protected Object hostFunctionGet(Set<ObjectFunction> functions) {
		return new HostedJavaMethod(this, functions);
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
	
	protected static ObjectMembers getObjectMembers(Object object) {
		// TODO: Use cache for object members - reflecting is expensive
		return new ObjectMembers(object);
	}
	
}

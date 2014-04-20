/**
 * HostedJavaObject.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.java.ClassConstructor;
import org.fit.cssbox.scriptbox.script.java.ClassField;
import org.fit.cssbox.scriptbox.script.java.ClassFunction;
import org.fit.cssbox.scriptbox.script.java.ClassMember;
import org.fit.cssbox.scriptbox.script.java.InvocableMember;
import org.fit.cssbox.scriptbox.script.java.MemberConstructor;
import org.fit.cssbox.scriptbox.script.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.java.ObjectMembers;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ObjectException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

/*
 * Creates scope for java native object.
 */
public class HostedJavaObject extends BaseFunction implements Wrapper {

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
	public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
		Scriptable result = createObject(cx, scope);
		
		Set<ClassConstructor> constructors = objectMembers.getConstructors();
		
		if (constructors == null || constructors.isEmpty()) {
			throw new ObjectException("Object does not contain any constructors!");
		}
		
		InvocableMember<?> nearestInvocable = HostedJavaMethod.getNearestObjectFunction(args, constructors);

		if (nearestInvocable == null) {
			throw new FunctionException("Unable to match nearest constructor");
		}
		
		MemberConstructor nearestConstructorMember = (MemberConstructor)nearestInvocable;
		
		Constructor<?> constructor = nearestConstructorMember.getMember();
		
		Class<?> expectedTypes[] = constructor.getParameterTypes();
		Object[] castedArgs = ClassFunction.castArgs(expectedTypes, args);
		
		try {
			Object newInstance = constructor.newInstance(castedArgs);
			newInstance = wrapObject(newInstance);
			
			if (newInstance instanceof Scriptable) {
				result = (Scriptable)newInstance;
			}
			
		} catch (Exception e) {
			throw new UnknownException(e);
		}
		
		
		return result;
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

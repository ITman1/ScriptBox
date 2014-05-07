/**
 * Hostedobject.java
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

package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.exceptions.ObjectException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.reflect.ClassConstructor;
import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.reflect.ConstructorMember;
import org.fit.cssbox.scriptbox.script.reflect.DefaultObjectMembers;
import org.fit.cssbox.scriptbox.script.reflect.InvocableMember;
import org.fit.cssbox.scriptbox.script.reflect.ObjectGetter;
import org.fit.cssbox.scriptbox.script.reflect.ObjectMembers;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.TopLevel.Builtins;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

/**
 * Creates scope for native Java object which contains its class 
 * member properties. In other words, wraps the native Java object
 * and makes it accessible from the JavaScript.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class HostedJavaObject extends ObjectScriptable implements Wrapper, Function {

	private static final long serialVersionUID = 6761328943903362404L;
	
	protected boolean hasNonObjectGetterGet;
		
	protected ObjectMembers objectMembers;
	
	/**
	 * Constructs new scope representing the native Java object.
	 * 
	 * @param scope Scope to become the parent scope of this Java object.
	 * @param object Java object to be wrapped.
	 */
	public HostedJavaObject(Scriptable scope, Object object) {
		this(scope, DefaultObjectMembers.getObjectMembers(object));
	}
	
	/**
	 * Constructs new scope that contains the passed object members.
	 * 
	 * @param scope Scope to become the parent scope of this Java object.
	 * @param objectMembers Object and its members to be put into this new scope.
	 */
	public HostedJavaObject(Scriptable scope, ObjectMembers objectMembers) {
		super(objectMembers.getObject(), scope, null);
		
		this.objectMembers = objectMembers;
		
		TopLevel topLevel = WindowJavaScriptEngine.getObjectTopLevel(scope);
		Scriptable builtinObject = topLevel.getBuiltinCtor(Builtins.Object);
		setPrototype(builtinObject);
		
		if (object instanceof ObjectGetter) {
			Class<?>[] getterArgs = ObjectGetter.METHOD_ARG_TYPES;
			String getterName = ObjectGetter.METHOD_NAME;
			for (Method method : objectClass.getMethods()) {
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
		return "Hostedobject";
	}
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		return Undefined.instance;
	}
	
	@Override
	public Scriptable construct(Context cx, Scriptable scope, Object[] args) {
		Scriptable result = new NativeObject();
		result.setParentScope(getParentScope());
		
		Set<ClassConstructor> constructors = objectMembers.getConstructors();
		
		if (constructors == null || constructors.isEmpty()) {
			throw new ObjectException("Object does not contain any constructors!");
		}
		
		InvocableMember<?> nearestInvocable = HostedJavaMethod.getNearestObjectFunction(args, constructors);

		if (nearestInvocable == null) {
			throw new FunctionException("Unable to match nearest constructor");
		}
		
		ConstructorMember nearestConstructorMember = (ConstructorMember)nearestInvocable;
		
		Constructor<?> constructor = nearestConstructorMember.getMember();
		
		Class<?> expectedTypes[] = constructor.getParameterTypes();
		Object[] castedArgs = HostedJavaMethod.castArgs(expectedTypes, args);
		
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
		if (object instanceof org.fit.cssbox.scriptbox.script.Wrapper) {
			return ((org.fit.cssbox.scriptbox.script.Wrapper<?>)object).unwrap();
		}
		return object;
	}
	
	@Override
	public String toString() {
		return object.toString();
	}

	/**
	 * Wraps the given object.
	 * 
	 * @param object Object to be wrapped.
	 * @return Wrapped passed object.
	 */
	protected Object wrapObject(Object object) {		
		return WindowJavaScriptEngine.javaToJS(object, this);
	}
	
	/**
	 * Removes the property from the Native java object. (not supported)
	 * 
	 * @param name Name of the property to be removed from the Java object.
	 */
	protected void hostDelete(String name) {
		// TODO: It can be feature
		throw new FieldException("Deleting host properties is not supported");
	}
	
	/**
	 * Sets new value to the field of the wrapped object.
	 * 
	 * @param name Name of the field that should be set.
	 * @param value Value to be set into field.
	 */
	protected void hostPut(String name, Object value) {
		if (objectMembers.hasMemberWithName(name)) {
			Set<ClassMember<?>> members = objectMembers.getMembersByName(name);
			
			if (members == null || members.isEmpty()) {
				throw new FieldException("Scope does not contain property with this name!");
			}

			ClassMember<?> firstMember = members.iterator().next();
			if (firstMember instanceof ClassField) {
				hostPut((ClassField)firstMember, object, value);
				return;
			} else {
				throw new FieldException("Unsupported operation");
			}
		}
		throw new FieldException("Scope does not contain property with this name!");
	}
	
	/**
	 * Returns value of the wrapped object field with the given name.
	 * 
	 * @param name Name of the property.
	 * @return Value of the property of the wrapped java object.
	 */
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
	
	/**
	 * Returns the value of the object getter it there is any for the wrapped java object.
	 * 
	 * @param key Key that should be searched using the object getter.
	 * @return Value of the property of the wrapped java object
	 */
	protected Object objectGetterGet(Object key) {
		Method method = null;
		Object result = Scriptable.NOT_FOUND;
		
		if (object instanceof ObjectGetter) {
			Object value = ((ObjectGetter)object).get(key);
			
			try {
				method = ClassUtils.getPublicMethod(objectClass, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
			} catch (Exception e) {
				throw new InternalException(e);
			}
			
			if (value != ObjectGetter.UNDEFINED_VALUE) {
				result = value;
			}
		}
		
		if (result != Scriptable.NOT_FOUND && method != null) {
			//result = new JavaMethodRedirectedWrapper(result, object, method);
		}
		
		return result;
	}
	
	private Object wrapGet(Set<ClassMember<?>> members) {
		Object result = Scriptable.NOT_FOUND;
		ClassMember<?> firstMember = members.iterator().next();
		if (firstMember instanceof ClassField) {
			result = hostGet((ClassField)firstMember, object);
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

	private Object hostFunctionGet(Set<ClassFunction> functions) {
		return new HostedJavaMethod(this, object, functions);
	}
	
	/**
	 * Returns merged enumerable properties from given object members and already known super IDs.
	 * 
	 * @param objectMembers Object members that might have some enumerable properties.
	 * @param superIds IDs that should be included into result array.
	 * @return Array of the enumerable properties.
	 */
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
	
	/**
	 * Implements put operation into native Java object.
	 * 
	 * @param objectField Field into which should we are putting.
	 * @param object Object having the passed field.
	 * @param value Value to be put into passed field.
	 */
	public static void hostPut(ClassField objectField, Object object, Object value) {
		object = WindowJavaScriptEngine.jsToJava(object);
		value = WindowJavaScriptEngine.jsToJava(value);
		Class<?> type = objectField.getFieldType();
		value = ClassField.wrap(type, value);
		objectField.set(object, value);
	}
	
	/**
	 * Implements get operation onto native Java object.
	 * 
	 * @param objectField Field into which should we are putting.
	 * @param object Object having the passed field.
	 * @return Value retrieved from the passed field.
	 */
	public static Object hostGet(ClassField objectField, Object object) {
		object = WindowJavaScriptEngine.jsToJava(object);
		Object value = objectField.get(object);
		return ClassField.unwrap(value);
	}
}

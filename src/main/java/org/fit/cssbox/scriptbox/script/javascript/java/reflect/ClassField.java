/**
 * ClassField.java
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

package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.fit.cssbox.scriptbox.script.javascript.window.FunctionEventHandlerAdapter;
import org.mozilla.javascript.Function;

public class ClassField extends ClassMember<Field> implements MemberField {
	protected Method fieldGetterMethod;
	protected Method fieldSetterMethod;
	protected boolean getOverride;
	protected boolean setOverride;
	
	public ClassField(Class<?> clazz, Field field) {
		this(clazz, null, null, field, false, false, null);
	}
	
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, null, false, false, null);
	}
	
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod, Field field) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, field, false, false, null);
	}
	
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod, Field field, boolean getOverride, boolean setOverride) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, field, getOverride, setOverride, null);
	}
	
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod, Field field, boolean getOverride, boolean setOverride, String[] options) {
		super(clazz, field, options);
		this.fieldGetterMethod = fieldGetterMethod;
		this.fieldSetterMethod = fieldSetterMethod;
		this.getOverride = getOverride;
		this.setOverride = setOverride;
	}
	
	@Override
	public Method getFieldGetterMethod() {
		return fieldGetterMethod;
	}
	
	@Override
	public Method getFieldSetterMethod() {
		return fieldSetterMethod;
	}
	
	@Override
	public boolean hasGetOverride() {
		return getOverride;
	}
	
	@Override
	public boolean hasSetOverride() {
		return setOverride;
	}
	
	public Object get(Object object) {
		object = ObjectScriptable.jsToJava(object);

		if (!clazz.isInstance(object)) {
			throw new FieldException("Passed object is not instance of the class to which field belongs to.");
		}
		
		boolean override = getOverride && fieldGetterMethod != null;
		Object value = null;
		if (member != null && !override) {
			try {
				value = member.get(object);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		} else {
			try {
				value = fieldGetterMethod.invoke(object);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		}
		
		return unwrap(value);
	}
	
	public void set(Object object, Object value) {
		object = ObjectScriptable.jsToJava(object);
		value = ObjectScriptable.jsToJava(value);

		Class<?> type = getFieldType();
		value = wrap(type, value);
		
		if (!clazz.isInstance(object)) {
			throw new FieldException("Passed object is not instance of the class to which field belongs to.");
		}
		
		boolean override = setOverride && fieldSetterMethod != null;
		
		if (member != null && !override) {
			try {
				member.set(object, value);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		} else {
			try {
				fieldSetterMethod.invoke(object, value);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		}
	}	
	
	public static Object wrap(Class<?> type, Object value) {
		if (type.equals(EventHandler.class) && value instanceof Function) {
			value = new FunctionEventHandlerAdapter((Function)value);
		}
		
		return value;
	}
	
	public static Object unwrap(Object value) {
		if (value instanceof FunctionEventHandlerAdapter) {
			value = ((FunctionEventHandlerAdapter)value).getFunction();
		}
		
		return value;
	}
	
	public Class<?> getFieldType() {
		Class<?>[] params = (fieldSetterMethod != null)? fieldSetterMethod.getParameterTypes() : null;
		Class<?> setterType = (params != null && params.length > 0)? params[0] : null;
		Class<?> getterType = (fieldGetterMethod != null)? fieldGetterMethod.getReturnType() : null;
		Class<?> memberType = (member != null)? member.getType() : null;
		
		if (setterType != null) {
			return setterType;
		} else if (getterType != null) {
			return getterType;
		} else {
			return memberType;
		}
	}
	
	public static boolean isGetter(Method method) {
		String getterName =  method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		
		if (getterName.startsWith("get") && getterName.length() > 3) {
			Character fourthCharacter = getterName.charAt(3);
			return parameterTypes.length == 0 && Character.isUpperCase(fourthCharacter) && returnType != Void.TYPE;
		}
				
		return false;
	}
	
	public static boolean isSetter(Method method) {
		String setterName =  method.getName();
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		
		if (setterName.startsWith("set") && setterName.length() > 3) {
			Character fourthCharacter = setterName.charAt(3);
			return parameterTypes.length == 1 && Character.isUpperCase(fourthCharacter) && returnType == Void.TYPE;
		}
				
		return false;
	}
	
	public static boolean isField(Field field) {
		return !Modifier.isStatic(field.getModifiers());
	}
	
	public static String extractFieldNameFromGetter(Method method) {		
		if (isGetter(method)) {
			String getterName =  method.getName();
			Character fourthCharacter = getterName.charAt(3);
			return (fourthCharacter + "").toLowerCase() + getterName.substring(4);
		}
			
		throw new FieldException("Invalid getter name! Getter method should start with 'get' and next character should be upper case!");
	}
	
	public static String extractFieldNameFromSetter(Method method) {
		if (isSetter(method)) {
			String getterName =  method.getName();
			Character fourthCharacter = getterName.charAt(3);
			return (fourthCharacter + "").toLowerCase() + getterName.substring(4);
		}
		
		throw new FieldException("Invalid setter name! Getter method should start with 'set' and next character should be upper case!");
	}
	
	public static String extractFieldName(Field field) {
		return field.getName();
	}
}

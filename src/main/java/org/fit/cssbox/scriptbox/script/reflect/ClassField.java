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

package org.fit.cssbox.scriptbox.script.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.dom.events.OnErrorEventHandler;
import org.fit.cssbox.scriptbox.history.StateObject;
import org.fit.cssbox.scriptbox.script.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.wrap.FunctionEventHandlerAdapter;
import org.fit.cssbox.scriptbox.script.javascript.wrap.FunctionEventListenerAdapter;
import org.fit.cssbox.scriptbox.script.javascript.wrap.FunctionOnErrorEventHandlerAdapter;
import org.mozilla.javascript.Function;
import org.w3c.dom.events.EventListener;

/**
 * Represents the field of the class which might be accessible via field, getter, or setter.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ClassField extends ClassMember<Field> implements FieldMember {
	protected Method fieldGetterMethod;
	protected Method fieldSetterMethod;
	protected boolean getOverride;
	protected boolean setOverride;
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param field Wrapped field.
	 */
	public ClassField(Class<?> clazz, Field field) {
		this(clazz, null, null, field, false, false, null);
	}
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param field Wrapped field.
	 * @param options Array with options.
	 */
	public ClassField(Class<?> clazz, Field field, String ...options) {
		this(clazz, null, null, field, false, false, options);
	}
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param fieldGetterMethod Field getter that mediates the field.
	 * @param fieldSetterMethod Field setter that modifies the field.
	 */
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, null, false, false, null);
	}
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param fieldGetterMethod Field getter that mediates the field.
	 * @param fieldSetterMethod Field setter that modifies the field.
	 * @param field Wrapped field.
	 */
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod, Field field) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, field, false, false, null);
	}
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param fieldGetterMethod Field getter that mediates the field.
	 * @param fieldSetterMethod Field setter that modifies the field.
	 * @param field Wrapped field.
	 * @param getOverride If set, then use field getter instead the direct access to field.
	 * @param setOverride If set, then use field setter instead the direct access to field.
	 */
	public ClassField(Class<?> clazz, Method fieldGetterMethod, Method fieldSetterMethod, Field field, boolean getOverride, boolean setOverride) {
		this(clazz, fieldGetterMethod, fieldSetterMethod, field, getOverride, setOverride, null);
	}
	
	/**
	 * Constructs class field wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped field.
	 * @param fieldGetterMethod Field getter that mediates the field.
	 * @param fieldSetterMethod Field setter that modifies the field.
	 * @param field Wrapped field.
	 * @param getOverride If set, then use field getter instead the direct access to field.
	 * @param setOverride If set, then use field setter instead the direct access to field.
	 * @param options Options associated with this class field.
	 */
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
	
	@Override
	public String getName() {
		String fieldName = null;
		
		if (member != null) {
			fieldName = extractFieldName(member);
		}
		
		if (fieldName != null) {
			return fieldName;
		} else if (fieldGetterMethod != null) {
			fieldName = extractFieldNameFromGetter(fieldGetterMethod);
		}
		
		if (fieldName != null) {
			return fieldName;
		} else if (fieldSetterMethod != null) {
			fieldName = extractFieldNameFromSetter(fieldSetterMethod);
		}
		
		if (fieldName != null) {
			return fieldName;
		} else {
			return "";
		}
	}
	
	/**
	 * Returns value of the field of the passed object.
	 * 
	 * @param object Object which contains the field.
	 * @return Field value.
	 */
	public Object get(Object object) {

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
		
		return value;
	}
	
	/**
	 * Sets new value of the field of the passed object.
	 * 
	 * @param object Object which contains the field.
	 * @param value New field value.
	 */
	public void set(Object object, Object value) {		
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
			if (fieldSetterMethod == null) {
				throw new FieldException("Object field does not have setter!");
			}
			
			try {
				fieldSetterMethod.invoke(object, value);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		}
	}	
	
	// FIXME: It could be here some adapter/registry mechanism here
	// FIXME: Here should not be referenced mozilla javascript packages, this should be generalized in future!
	public static Object wrap(Class<?> type, Object value) {
		if (type.equals(EventListener.class) && value instanceof Function) {
			value = new FunctionEventListenerAdapter((Function)value);
		} else if (type.equals(EventHandler.class) && value instanceof Function) {
			value = new FunctionEventHandlerAdapter((Function)value);
		} else if (type.equals(OnErrorEventHandler.class) && value instanceof Function) {
			value = new FunctionOnErrorEventHandlerAdapter((Function)value);
		} else if (type.equals(StateObject.class) && value instanceof Object) {
			value = new StateObject(value);
		}
		
		return value;
	}
	
	// FIXME: It could be here some adapter/registry mechanism here, not hard coded...
	public static Object unwrap(Object value) {
		if (value instanceof FunctionEventListenerAdapter) {
			value = ((FunctionEventListenerAdapter)value).getFunction();
		} else if (value instanceof FunctionEventHandlerAdapter) {
			value = ((FunctionEventHandlerAdapter)value).getFunction();
		} else if (value instanceof StateObject) {
			value = ((StateObject)value).getObject();
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
	
	/**
	 * Tests whether the passed method is the getter method for some field.
	 * 
	 * @param method Method to be tested.
	 * @return True if is passed method the getter method, otherwise false.
	 */
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
	
	/**
	 * Tests whether the passed method is the setter method for some field.
	 * 
	 * @param method Method to be tested.
	 * @return True if is passed method the setter method, otherwise false.
	 */
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
	
	/**
	 * Tests whether the passed field is the field.
	 * 
	 * @param field Field to be tested.
	 * @return True if passed field is the field, otherwise false.
	 */
	public static boolean isField(Field field) {
		return !Modifier.isStatic(field.getModifiers());
	}
	
	/**
	 * Extracts the name for field given by a getter method.
	 * 
	 * @param method Field getter method from which to extract the name.
	 * @return Name of the field.
	 */
	public static String extractFieldNameFromGetter(Method method) {		
		if (isGetter(method)) {
			String getterName =  method.getName();
			Character fourthCharacter = getterName.charAt(3);
			return (fourthCharacter + "").toLowerCase() + getterName.substring(4);
		}
			
		throw new FieldException("Invalid getter name! Getter method should start with 'get' and next character should be upper case!");
	}
	
	/**
	 * Extracts the name for field given by a setter method.
	 * 
	 * @param method Field setter method from which to extract the name.
	 * @return Name of the field.
	 */
	public static String extractFieldNameFromSetter(Method method) {
		if (isSetter(method)) {
			String getterName =  method.getName();
			Character fourthCharacter = getterName.charAt(3);
			return (fourthCharacter + "").toLowerCase() + getterName.substring(4);
		}
		
		throw new FieldException("Invalid setter name! Getter method should start with 'set' and next character should be upper case!");
	}
	
	/**
	 * Extracts the name for passed field.
	 * 
	 * @param field Field.
	 * @return Name of the field.
	 */
	public static String extractFieldName(Field field) {
		return field.getName();
	}
}

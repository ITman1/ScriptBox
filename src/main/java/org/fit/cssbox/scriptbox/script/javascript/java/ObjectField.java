package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.mozilla.javascript.Scriptable;

public class ObjectField {
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("gettter", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("setter", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}

	protected Object object;
	protected Method fieldGetterMethod;
	protected Method fieldSetterMethod;
	protected Field field;
	
	public ObjectField(Object object, Field field) {
		this.object = object;
		this.field = field;
	}
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod) {
		this.object = object;
		this.fieldGetterMethod = fieldGetterMethod;
		this.fieldSetterMethod = fieldSetterMethod;
	}
	
	public Object getObject() {
		return object;
	}

	public Method getFieldGetterMethod() {
		return fieldGetterMethod;
	}
	
	public Method getFieldSetterMethod() {
		return fieldSetterMethod;
	}
	
	public Field getField() {
		return field;
	}
	
	public Object gettter(Scriptable obj) {
		if (field != null) {
			try {
				return field.get(object);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		} else {
			try {
				return fieldGetterMethod.invoke(object);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		}
	}
	
	public void setter(Scriptable obj, Object value) {
		value = ObjectScriptable.jsToJava(value);
		if (field != null) {
			try {
				field.set(obj, value);
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
			return parameterTypes.length == 1 && Character.isUpperCase(fourthCharacter) && returnType != Void.TYPE;
		}
				
		return false;
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
}

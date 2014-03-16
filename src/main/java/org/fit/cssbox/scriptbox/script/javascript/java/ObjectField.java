package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.mozilla.javascript.Scriptable;

public class ObjectField extends ObjectMember<Field> {
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

	protected Method fieldGetterMethod;
	protected Method fieldSetterMethod;
	protected boolean getOverride;
	protected boolean setOverride;
	
	public ObjectField(Object object, Field field) {
		this(object, null, null, field, false, false, null);
	}
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod) {
		this(object, fieldGetterMethod, fieldSetterMethod, null, false, false, null);
	}
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod, Field field) {
		this(object, fieldGetterMethod, fieldSetterMethod, field, false, false, null);
	}
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod, Field field, boolean getOverride, boolean setOverride) {
		this(object, fieldGetterMethod, fieldSetterMethod, field, getOverride, setOverride, null);
	}
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod, Field field, boolean getOverride, boolean setOverride, String[] options) {
		super(object, field, options);
		this.fieldGetterMethod = fieldGetterMethod;
		this.fieldSetterMethod = fieldSetterMethod;
		this.getOverride = getOverride;
		this.setOverride = setOverride;
	}
	
	public Method getFieldGetterMethod() {
		return fieldGetterMethod;
	}
	
	public Method getFieldSetterMethod() {
		return fieldSetterMethod;
	}
	
	public Object gettter(Scriptable obj) {
		boolean override = getOverride && fieldGetterMethod != null;
		if (member != null && !override) {
			try {
				return member.get(object);
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
		boolean override = setOverride && fieldSetterMethod != null;
		value = ObjectScriptable.jsToJava(value);
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

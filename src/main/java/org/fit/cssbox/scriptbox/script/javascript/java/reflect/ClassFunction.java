package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;

public class ClassFunction extends ClassMember<Method> implements MemberFunction {	
	
	public ClassFunction(Class<?> clazz, Method functionMethod) {
		this(clazz, functionMethod, null);
	}
	
	public ClassFunction(Class<?> clazz, Method functionMethod, String[] options) {
		super(clazz, functionMethod, options);
	}
	
	public Object invoke(Object object, Object ...args) {
		if (!clazz.isInstance(object)) {
			throw new FunctionException("Passed object is not instance of the class to which function belongs to.");
		}
		
		try {
			return member.invoke(object, args);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	public static Object[] castArgs(Class<?>[] expectedTypes, Object... args) {
		if (expectedTypes != null && args != null && expectedTypes.length == args.length) {
			Object[] castedArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				Class<?> expectedType = expectedTypes[i];
				if (arg == null) {
					castedArgs[i] = null;
				} else if (arg instanceof Double && (expectedType.equals(Integer.class) || expectedType.equals(int.class))) {
					castedArgs[i] = ((Double)arg).intValue();
				} else {
					castedArgs[i] = ObjectScriptable.jsToJava(arg);;
				}
			}
			
			return castedArgs;
		}
		
		return null;
	}
	
	public static boolean isAssignableTypes(Object[] args, Class<?>... types) {
		if (args.length != types.length) {
			return false;
		}
		
		for (int i = 0; i < args.length; i++) {
			Class<?> argClass = (args[i] == null)? null : args[i].getClass();
			Class<?> paramClass = types[i];
			
			if (paramClass.isPrimitive() && argClass == null) {
				return false;
			} else if (!paramClass.isPrimitive() && argClass == null) {
				continue; 
			} else if (!ClassUtils.isAssignable(argClass, paramClass, true)) {
				return false;
			}
		}
		
		return true;
	}
	
	public static Method getObjectGetterMetod(Class<?> clazz) {
		try {
			return ClassUtils.getPublicMethod(clazz, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
		} catch (Exception e) {
			throw new InternalException(e);
		}
	}
	
	public static boolean isObjectGetterMethod(Class<?> clazz, Method method) {
		if (ObjectGetter.class.isAssignableFrom(clazz)) {
			Method getterMethod = ClassFunction.getObjectGetterMetod(clazz);	
			return method.equals(getterMethod);
		}
		return false;
	}
	
	public static boolean isFunction(Class<?> clazz, Method method) {
		return !isObjectGetterMethod(clazz, method);
	}
	
	public static String extractFunctionName(Method method) {
		return method.getName();
	}
}

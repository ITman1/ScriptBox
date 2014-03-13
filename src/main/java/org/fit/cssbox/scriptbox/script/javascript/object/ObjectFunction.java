package org.fit.cssbox.scriptbox.script.javascript.object;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class ObjectFunction {	
	public final static Method FUNCTION_METHOD;
	
	static {
		Method functionMethod = null;
		try {
			functionMethod = ObjectFunction.class.getDeclaredMethod("function", Context.class, Scriptable.class, Object[].class, Function.class);
        } catch (NoSuchMethodException e) {
        }
		
		FUNCTION_METHOD = functionMethod;
	}
	
	private Object object;
	private Method functionMethod;
	
	public ObjectFunction(Object object, Method functionMethod) {
		this.object = object;
		this.functionMethod = functionMethod;
	}
	
	public Object getObject() {
		return object;
	}

	public Method getFunctionMethod() {
		return functionMethod;
	}
	
	public static Object function(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (funObj instanceof OverloadableFunctionObject) {
			
			OverloadableFunctionObject wrappedFunctionObject = (OverloadableFunctionObject)funObj;
			ObjectFunction nearestFunctionObject = wrappedFunctionObject.getNearestObjectFunction(args);

			if (nearestFunctionObject == null) {
				throw new FunctionException("Unable to match nearest function");
			}
			
			Method functionMethod = nearestFunctionObject.functionMethod;
			Object object = nearestFunctionObject.object;
			
			Class<?> expectedTypes[] = functionMethod.getParameterTypes();
			Object[] castedArgs = castArgs(expectedTypes, args);
			
			try {
				 return functionMethod.invoke(object, castedArgs);
			} catch (Exception e) {
				throw new UnknownException(e);
			}
		}

		throw new FunctionException("Function object must be of class WrappedFunctionObject");
		//return Context.getUndefinedValue();
	}
	
	public static Object[] castArgs(Class<?>[] expectedTypes, Object... args) {
		if (expectedTypes != null && args != null && expectedTypes.length == args.length && args.length > 0) {
			Object[] castedArgs = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				Object arg = args[i];
				Class<?> expectedType = expectedTypes[i];
				if (arg instanceof Double && (expectedType.equals(Integer.class) || expectedType.equals(int.class))) {
					castedArgs[i] = ((Double)arg).intValue();
				} else {
					castedArgs[i] = ObjectScriptable.jsToJava(arg);;
				}
			}
			
			return castedArgs;
		}
		
		return new Object[0];
	}
	
	public static boolean isAssignableTypes(Object[] args, Class<?>... types) {
		if (args.length != types.length) {
			return false;
		}
		
		for (int i = 0; i < args.length; i++) {
			Class<?> argClass = args[i].getClass();
			Class<?> paramClass = types[i];
			
			if (!ClassUtils.isAssignable(argClass, paramClass, true)) {
				return false;
			}
		}
		
		return true;
	}
}

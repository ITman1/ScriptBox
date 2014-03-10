package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.script.ScriptException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

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
	
	public static Object function(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ScriptException {
		if (funObj instanceof WrappedFunctionObject) {
			WrappedFunctionObject wrappedFunctionObject = (WrappedFunctionObject)funObj;
			ObjectFunction wrapper = wrappedFunctionObject.getObjectFunctionWrapper();
			Method functionMethod = wrapper.functionMethod;
			Object object = wrapper.object;
			
			Class<?> expectedTypes[] = functionMethod.getParameterTypes();
			Object[] castedArgs = castArgs(expectedTypes, args);
			
			try {
				 return functionMethod.invoke(object, castedArgs);
			} catch (Exception e) {
				throw new ScriptException(e);
			}
		}

		throw new ScriptException("Function object must be of class WrappedFunctionObject");
		//return Context.getUndefinedValue();
	}
	
	private static Object[] castArgs(Class<?>[] expectedTypes, Object[] args) {
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
}

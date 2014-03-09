package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import javax.script.ScriptException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ObjectFunctionWrapper {
	private static class WrappedFunctionObject extends FunctionObject {
		private static final long serialVersionUID = -5644060115581311028L;

		private ObjectFunctionWrapper wrapper;
		
		public WrappedFunctionObject(ObjectFunctionWrapper wrapper, String name, Member methodOrConstructor, Scriptable scope) {
			super(name, methodOrConstructor, scope);
			
			this.wrapper = wrapper;
		}
		
		public ObjectFunctionWrapper getObjectFunctionWrapper() {
			return wrapper;
		}
	}
	
	public final static Method FUNCTION_METHOD;
	
	static {
		Method functionMethod = null;
		try {
			functionMethod = ObjectFunctionWrapper.class.getDeclaredMethod("function", Context.class, Scriptable.class, Object[].class, Function.class);
        } catch (NoSuchMethodException e) {
        }
		
		FUNCTION_METHOD = functionMethod;
	}
	
	private Object object;
	private Method functionMethod;
	
	public ObjectFunctionWrapper(Object object, Method functionMethod) {
		this.object = object;
		this.functionMethod = functionMethod;
	}
	
	public static Object function(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws ScriptException {
		if (funObj instanceof WrappedFunctionObject) {
			WrappedFunctionObject wrappedFunctionObject = (WrappedFunctionObject)funObj;
			ObjectFunctionWrapper wrapper = wrappedFunctionObject.getObjectFunctionWrapper();
			
			try {
				 return wrapper.functionMethod.invoke(wrapper.object, args);
			} catch (Exception e) {
				throw new ScriptException(e);
			}
		}

		throw new ScriptException("Function object must be of class WrappedFunctionObject");
		//return Context.getUndefinedValue();
	}
	
	public static ObjectFunctionWrapper defineWrappedObjectFunction(ScriptableObject functionScopeObject, String functionName, Object object, Method functionMethod) {
		ObjectFunctionWrapper functionWrapper = new ObjectFunctionWrapper(object, functionMethod);
		
        FunctionObject f = new WrappedFunctionObject(functionWrapper, functionName, FUNCTION_METHOD, functionScopeObject);
        functionScopeObject.defineProperty(functionName, f, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
        
        return functionWrapper;
	}
}

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class HostedJavaMethod extends FunctionObject {
	private static final long serialVersionUID = -5644060115581311028L;

	private Set<ObjectFunction> objectFunctions;
	
	public HostedJavaMethod(Scriptable scope, Set<ObjectFunction> objectFunctions) {
		super(objectFunctions.iterator().next().getMember().getName(), ObjectFunction.FUNCTION_METHOD, scope);
		
		this.objectFunctions = objectFunctions;
	}
	
	public HostedJavaMethod(Scriptable scope, final ObjectFunction objectFunction) {
		this(scope, 
			new HashSet<ObjectFunction>() {
				private static final long serialVersionUID = -7257365623995694177L;
				{
					add(objectFunction);
				}
			}
		);
	}
	
	public Set<ObjectFunction> getAttachedObjectFunctions() {
		return objectFunctions;
	}
	
	public void attachObjectFunction(ObjectFunction objectFunction) {
		this.objectFunctions.add(objectFunction);
	}
	
	public ObjectFunction getNearestObjectFunction(Object[] args) {
		Class<?> argsTypes[] = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			argsTypes[i] = args[i].getClass();
		}
		
		for (ObjectFunction objectFunction : objectFunctions) {
			Method method = objectFunction.getMember();
			Class<?> methodTypes[] = method.getParameterTypes();
			
			Object[] castedArgs = ObjectFunction.castArgs(methodTypes, args);
			if (castedArgs == null) {
				continue;
			}
			
			boolean isAssignable = ObjectFunction.isAssignableTypes(castedArgs, methodTypes);
			if (isAssignable) {
				return objectFunction;
			}
		}
		
		return null;
	}
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		ObjectFunction nearestFunctionObject = getNearestObjectFunction(args);

		if (nearestFunctionObject == null) {
			throw new FunctionException("Unable to match nearest function");
		}
		
		Method functionMethod = nearestFunctionObject.getMember();
		Object object = nearestFunctionObject.getObject();
		
		Class<?> expectedTypes[] = functionMethod.getParameterTypes();
		Object[] castedArgs = ObjectFunction.castArgs(expectedTypes, args);
		
		try {
			 return functionMethod.invoke(object, castedArgs);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
}

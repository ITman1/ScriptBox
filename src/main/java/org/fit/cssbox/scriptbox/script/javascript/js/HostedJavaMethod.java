package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.MemberFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class HostedJavaMethod extends FunctionObject {
	public final static Method FUNCTION_METHOD;
	
	static {
		Method functionMethod = null;
		try {
			functionMethod = HostedJavaMethod.class.getDeclaredMethod("invoke", Context.class, Scriptable.class, Object[].class, Function.class);
        } catch (NoSuchMethodException e) {
        }
		
		FUNCTION_METHOD = functionMethod;
	}
	
	private static final long serialVersionUID = -5644060115581311028L;

	private Object object;
	private Set<MemberFunction> objectFunctions;
	
	public HostedJavaMethod(Scriptable scope, Object object, Set<? extends MemberFunction> objectFunctions) {
		super(objectFunctions.iterator().next().getMember().getName(), FUNCTION_METHOD, scope);
		
		this.object = object;
		this.objectFunctions = new HashSet<MemberFunction>();
		this.objectFunctions.addAll(objectFunctions);
	}
	
	public HostedJavaMethod(Scriptable scope, Object object, final MemberFunction objectFunction) {
		this(scope, object,
			new HashSet<MemberFunction>() {
				private static final long serialVersionUID = -7257365623995694177L;
				{
					add(objectFunction);
				}
			}
		);
	}
	
	public static Object invoke(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (funObj instanceof HostedJavaMethod) {
			((HostedJavaMethod)funObj).call(cx, thisObj, thisObj, args);
		}

		throw new FunctionException("Function object must be of class HostedJavaMethod");
	}
	
	public Set<? extends MemberFunction> getAttachedObjectFunctions() {
		return objectFunctions;
	}
	
	public void attachObjectFunction(MemberFunction objectFunction) {
		this.objectFunctions.add(objectFunction);
	}
	
	public MemberFunction getNearestObjectFunction(Object[] args) {
		Class<?> argsTypes[] = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			argsTypes[i] = args[i].getClass();
		}
		
		for (MemberFunction objectFunction : objectFunctions) {
			Method method = objectFunction.getMember();
			Class<?> methodTypes[] = method.getParameterTypes();
			
			Object[] castedArgs = ClassFunction.castArgs(methodTypes, args);
			if (castedArgs == null) {
				continue;
			}
			
			boolean isAssignable = ClassFunction.isAssignableTypes(castedArgs, methodTypes);
			if (isAssignable) {
				return objectFunction;
			}
		}
		
		return null;
	}
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		MemberFunction nearestFunctionObject = getNearestObjectFunction(args);

		if (nearestFunctionObject == null) {
			throw new FunctionException("Unable to match nearest function");
		}
		
		Method functionMethod = nearestFunctionObject.getMember();
		
		Class<?> expectedTypes[] = functionMethod.getParameterTypes();
		Object[] castedArgs = ClassFunction.castArgs(expectedTypes, args);
		
		try {
			 return functionMethod.invoke(object, castedArgs);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
}

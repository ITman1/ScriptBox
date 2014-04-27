/**
 * HostedJavaMethod.java
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

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.reflect.FunctionMember;
import org.fit.cssbox.scriptbox.script.reflect.InvocableMember;
import org.mozilla.javascript.ConsString;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

/**
 * Creates function scope for native Java method - wraps the native Java method
 * and makes it callable from the JavaScript.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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
	private Set<FunctionMember> objectFunctions;
	
	/**
	 * Constructs new hosted Java method for given parent scope, of the passed object and with the passed overloaded function members.
	 * 
	 * @param scope Scope to become the parent scope of this function.
	 * @param object Object with the wrapped method.
	 * @param objectFunctions Overloaded function members which should have the same name.
	 */
	public HostedJavaMethod(Scriptable scope, Object object, Set<? extends FunctionMember> objectFunctions) {
		super(objectFunctions.iterator().next().getMember().getName(), FUNCTION_METHOD, scope);
		
		this.object = object;
		this.objectFunctions = new HashSet<FunctionMember>();
		this.objectFunctions.addAll(objectFunctions);
	}
	
	/**
	 * Constructs new hosted Java method for given parent scope, of the passed object and with the passed function member.
	 * 
	 * @param scope Scope to become the parent scope of this function.
	 * @param object Object with the wrapped method.
	 * @param objectFunction Function member to be wrapped.
	 */
	public HostedJavaMethod(Scriptable scope, Object object, final FunctionMember objectFunction) {
		this(scope, object,
			new HashSet<FunctionMember>() {
				private static final long serialVersionUID = -7257365623995694177L;
				{
					add(objectFunction);
				}
			}
		);
	}
	
	/**
	 * Returns attached object overloaded function members.
	 * 
	 * @return All associated function members.
	 */
	public Set<? extends FunctionMember> getAttachedObjectFunctions() {
		return objectFunctions;
	}
	
	/**
	 * Adds new overloaded member into this wrapped function object.
	 * 
	 * @param objectFunction New function member to be attached.
	 */
	public void attachObjectFunction(FunctionMember objectFunction) {
		this.objectFunctions.add(objectFunction);
	}
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		InvocableMember<?> nearestInvocable = getNearestObjectFunction(args, objectFunctions);

		if (nearestInvocable == null) {
			throw new FunctionException("Unable to match nearest function");
		}
		
		FunctionMember nearestFunctionObject = (FunctionMember)nearestInvocable;
		
		Method functionMethod = nearestFunctionObject.getMember();
		Class<?> returnType = functionMethod.getReturnType();
		
		Class<?> expectedTypes[] = functionMethod.getParameterTypes();
		Object[] castedArgs = castArgs(expectedTypes, args);
		
		try {
			 Object returned = functionMethod.invoke(object, castedArgs);
			 return (returnType == Void.class)? Undefined.instance : returned;
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	/**
	 * Invokes given function object.
	 * 
	 * @param funObj Function object to be invoked.
	 * @see Function#call(Context, Scriptable, Scriptable, Object[])
	 */
	public static Object invoke(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		if (funObj instanceof HostedJavaMethod) {
			((HostedJavaMethod)funObj).call(cx, thisObj, thisObj, args);
		}

		throw new FunctionException("Function object must be of class HostedJavaMethod");
	}
	
	/**
	 * Returns the nearest function - searches inside set of functions and
	 * tries to match the appropriate function according to given arguments.
	 * 
	 * @param args Arguments that are passed in the function call.
	 * @param invocableMembers Set of the functions.
	 * @return Function member if there was any that matched the passed arguments.
	 */
	public static InvocableMember<?> getNearestObjectFunction(Object[] args, Set<? extends InvocableMember<?>> invocableMembers) {
		Class<?> argsTypes[] = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			argsTypes[i] = (args[i] != null)? args[i].getClass() : null;
		}
		
		for (InvocableMember<?> invocableMember : invocableMembers) {
			Class<?> methodTypes[] = invocableMember.getParameterTypes();
			
			Object[] castedArgs = castArgs(methodTypes, args);
			if (castedArgs == null) {
				continue;
			}
			
			boolean isAssignable = ClassFunction.isAssignableTypes(castedArgs, methodTypes);
			if (isAssignable) {
				return invocableMember;
			}
		}
		
		return null;
	}
	
	/**
	 * Casts the given arguments into given expected types.
	 * 
	 * @param expectedTypes Types into which should be casted the given arguments.
	 * @param args Arguments to be casted.
	 * @return Array of the casted arguments if casting was successful, otherwise null.
	 */
	public static Object[] castArgs(Class<?>[] expectedTypes, Object... args) {
		if (expectedTypes != null && args != null) {

			if (expectedTypes.length <= args.length + 1 && expectedTypes.length > 0) {
				Class<?> lastType = expectedTypes[expectedTypes.length - 1];
				if (lastType.isArray()) {
					Class<?> arrayType = lastType.getComponentType();
					
					boolean maybeVarargs = true;
					if (expectedTypes.length == args.length) {
						Class<?> lastArg = args[args.length - 1].getClass();
						maybeVarargs = !ClassUtils.isAssignable(lastArg, lastType);
					}
					
					if (maybeVarargs) {
						for (int i = expectedTypes.length - 1; i < args.length; i++) {
							if (args[i] == null) {
								continue;
							}
							Class<?> argType = args[i].getClass();
							
							if (!ClassUtils.isAssignable(argType, arrayType)) {
								maybeVarargs = false;
								break;
							}
						}
						
						if (maybeVarargs) {
							Object[] oldArgs = args;
							args = new Object[expectedTypes.length];
							
							for (int i = 0; i < expectedTypes.length - 1; i++) {
								args[i] = oldArgs[i];
							}
							
							Object[] varargs = new Object[oldArgs.length - expectedTypes.length + 1];
							
							for (int i = expectedTypes.length - 1; i < oldArgs.length; i++) {
								varargs[i - expectedTypes.length + 1] = oldArgs[i];
							}
							
							args[expectedTypes.length - 1] = varargs;
						}
					}
				}
			}
			
			if (expectedTypes.length == args.length) {
				Object[] castedArgs = new Object[args.length];
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					Class<?> expectedType = expectedTypes[i];
					if (arg == null) {
						castedArgs[i] = null;
					} else if (arg == Undefined.instance) {
						castedArgs[i] = null;
					} else if (arg instanceof ConsString) {
						castedArgs[i] = ((ConsString)arg).toString();
					} else if (arg instanceof Double && (expectedType.equals(Integer.class) || expectedType.equals(int.class))) {
						castedArgs[i] = ((Double)arg).intValue();
					} else {
						castedArgs[i] = WindowJavaScriptEngine.jsToJava(arg);
						//castedArgs[i] = Context.jsToJava(castedArgs[i], expectedType);
					}
					
					castedArgs[i] = ClassField.wrap(expectedTypes[i], castedArgs[i]);
				}
				
				return castedArgs;
			}
		}
		
		return null;
	}
}

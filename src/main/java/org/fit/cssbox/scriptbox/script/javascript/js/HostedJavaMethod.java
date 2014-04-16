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

import org.fit.cssbox.scriptbox.script.java.ClassFunction;
import org.fit.cssbox.scriptbox.script.java.InvocableMember;
import org.fit.cssbox.scriptbox.script.java.MemberFunction;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

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
	
	@Override
	public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		InvocableMember<?> nearestInvocable = getNearestObjectFunction(args, objectFunctions);

		if (nearestInvocable == null) {
			throw new FunctionException("Unable to match nearest function");
		}
		
		MemberFunction nearestFunctionObject = (MemberFunction)nearestInvocable;
		
		Method functionMethod = nearestFunctionObject.getMember();
		Class<?> returnType = functionMethod.getReturnType();
		
		Class<?> expectedTypes[] = functionMethod.getParameterTypes();
		Object[] castedArgs = ClassFunction.castArgs(expectedTypes, args);
		
		try {
			 Object returned = functionMethod.invoke(object, castedArgs);
			 return (returnType == Void.class)? Undefined.instance : returned;
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	public static InvocableMember<?> getNearestObjectFunction(Object[] args, Set<? extends InvocableMember<?>> invocableMembers) {
		Class<?> argsTypes[] = new Class<?>[args.length];
		
		for (int i = 0; i < args.length; i++) {
			argsTypes[i] = (args[i] != null)? args[i].getClass() : null;
		}
		
		for (InvocableMember<?> invocableMember : invocableMembers) {
			Class<?> methodTypes[] = invocableMember.getParameterTypes();
			
			Object[] castedArgs = ClassFunction.castArgs(methodTypes, args);
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
}

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.javascript.java.ObjectFunction;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class OverloadableFunctionObject extends FunctionObject {
	private static final long serialVersionUID = -5644060115581311028L;

	private Set<ObjectFunction> objectFunctions;
	
	public OverloadableFunctionObject(ObjectFunction objectFunction, String name, Member methodOrConstructor, Scriptable scope) {
		super(name, methodOrConstructor, scope);
		
		this.objectFunctions = new HashSet<ObjectFunction>();
		this.objectFunctions.add(objectFunction);
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
			Method method = objectFunction.getFunctionMethod();
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
}

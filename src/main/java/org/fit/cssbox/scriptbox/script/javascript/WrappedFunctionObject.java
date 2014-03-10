package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Member;

import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;

public class WrappedFunctionObject extends FunctionObject {
	private static final long serialVersionUID = -5644060115581311028L;

	private ObjectFunction wrapper;
	
	public WrappedFunctionObject(ObjectFunction wrapper, String name, Member methodOrConstructor, Scriptable scope) {
		super(name, methodOrConstructor, scope);
		
		this.wrapper = wrapper;
	}
	
	public ObjectFunction getObjectFunctionWrapper() {
		return wrapper;
	}
}

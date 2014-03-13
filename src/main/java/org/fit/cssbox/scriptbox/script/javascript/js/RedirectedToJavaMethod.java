package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;

import org.mozilla.javascript.Wrapper;

public class RedirectedToJavaMethod implements Wrapper {

	protected Object wrappedObject;
	protected Object instance;
	protected Method method;
	
	public RedirectedToJavaMethod(Object wrappedObject, Object instance, Method method) {		
		this.wrappedObject = wrappedObject;
		this.instance = instance;
		this.method = method;
	}


	public Method getOriginMethod() {
		return method;
	}
	
	public Object getOriginInstance() {
		return instance;
	}

	@Override
	public Object unwrap() {
		return wrappedObject;
	}
	
}

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;

import org.mozilla.javascript.Wrapper;

public class JavaMethodRedirectedWrapper implements Wrapper {

	protected Object wrappedObject;
	protected Object instance;
	protected Method method;
	
	public JavaMethodRedirectedWrapper(Object wrappedObject, Object instance, Method method) {		
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
		if (wrappedObject instanceof Wrapper) {
			return ((Wrapper) wrappedObject).unwrap();
		}
		
		return wrappedObject;
	}
	
}

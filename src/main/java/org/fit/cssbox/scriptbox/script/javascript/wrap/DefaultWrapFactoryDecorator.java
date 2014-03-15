package org.fit.cssbox.scriptbox.script.javascript.wrap;

import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class DefaultWrapFactoryDecorator extends WrapFactoryDecorator {
	protected class DecoratedWrapFactory extends WrapFactory {
		@Override
		public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
			return topWrap(cx, scope, obj, staticType);
		}
		
		@Override
		public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
			return topWrapAsJavaObject(cx, scope, javaObject, staticType);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
			return topWrapJavaClass(cx, scope, javaClass);
		}
		
		@Override
		public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
			return topWrapNewObject(cx, scope, obj);
		}
		
		public Object decoratorWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
			return super.wrap(cx, scope, obj, staticType);
		}
		
		public Scriptable decoratorWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
			return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
		}
		
		public Scriptable decoratorWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
			return super.wrapJavaClass(cx, scope, javaClass);
		}
		
		public Scriptable decoratorWrapNewObject(Context cx, Scriptable scope, Object obj) {
			return super.wrapNewObject(cx, scope, obj);
		}
	}
	
	protected DecoratedWrapFactory factory;
	
	public DefaultWrapFactoryDecorator() {
		this(null);
	}
	
	public DefaultWrapFactoryDecorator(WrapFactoryDecorator decorator) {
		super(decorator);
		
		factory = new DecoratedWrapFactory();
	}
	
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return factory.decoratorWrap(cx, scope, obj, staticType);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return new HostedJavaObject(scope, javaObject, staticType);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
		return factory.decoratorWrapJavaClass(cx, scope, javaClass);
	}
	
	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		return factory.decoratorWrapNewObject(cx, scope, obj);
	}
}

package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.fit.cssbox.scriptbox.script.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectImplementor;

public class ScriptAnnotationImplementor extends ObjectImplementor {
		
	public ScriptAnnotationImplementor(Object implementedObject, BrowserScriptEngine browserScriptEngine) {
		super(implementedObject, browserScriptEngine);
	}

	@Override
	protected boolean isGetter(Method method) {
		if (method.isAnnotationPresent(ScriptGetter.class)) {
			boolean isSupported = ScriptAnnotation.isEngineSupported(implementedObjectType, method, scriptEngine);
			boolean isGetter = super.isGetter(method);
			
			return isSupported && isGetter;
		}
		return false;
	}
	
	@Override
	protected boolean isSetter(Method method) {
		if (method.isAnnotationPresent(ScriptSetter.class)) {
			boolean isSupported = ScriptAnnotation.isEngineSupported(implementedObjectType, method, scriptEngine);
			boolean isSetter = super.isGetter(method);
			
			return isSupported && isSetter;
		}
		return false;
	}
	
	@Override
	protected boolean isFunction(Method method) {
		if (method.isAnnotationPresent(ScriptFunction.class)) {
			boolean isSupported = ScriptAnnotation.isEngineSupported(implementedObjectType, method, scriptEngine);
			boolean isFunction = super.isFunction(method);
			
			return isSupported && isFunction;
		}
		return false;
	}	
}

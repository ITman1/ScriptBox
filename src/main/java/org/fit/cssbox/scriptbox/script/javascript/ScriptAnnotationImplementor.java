package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptClass;
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
		boolean isGetter = super.isGetter(method);
		boolean isSupportedAndValid = ScriptAnnotation.isSupportedAndValid(ScriptGetter.class, ScriptClass.ALL_FIELDS, implementedObjectType, method, scriptEngine);

		return isGetter && isSupportedAndValid;
	}
	
	@Override
	protected boolean isSetter(Method method) {
		boolean isSetter = super.isSetter(method);
		boolean isSupportedAndValid = ScriptAnnotation.isSupportedAndValid(ScriptSetter.class, ScriptClass.ALL_FIELDS, implementedObjectType, method, scriptEngine);
				
		return isSetter && isSupportedAndValid;
	}
	
	@Override
	protected boolean isFunction(Method method) {
		boolean isFunction = super.isFunction(method);
		boolean isSupportedAndValid = ScriptAnnotation.isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, implementedObjectType, method, scriptEngine);
				
		return isFunction && isSupportedAndValid;
	}	
}

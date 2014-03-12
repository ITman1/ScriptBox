package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ScriptAnnotationException;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.Shutter;


public class ScriptAnnotationShutter implements Shutter {

	protected BrowserScriptEngine scriptEngine;
	
	public ScriptAnnotationShutter(BrowserScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
	
	@Override
	public boolean isClassVisible(Class<?> type) {
		return true;
	}

	@Override
	public boolean isFieldVisible(Object instance, String fieldName) {
		if (fieldName.length() > 0) {
			char firstCharacter = fieldName.charAt(0);
			String getterMethodName = "get" + Character.toUpperCase(firstCharacter) + fieldName.substring(1);
			Class<?> instanceType = instance.getClass();
			
			Method method = null;
			
			try {
				method = instanceType.getMethod(getterMethodName);
			} catch (Exception e) {
				return false;
			}
			
			Annotation annotation = ScriptAnnotation.getScriptAnnotation(method);
			
			if (annotation != null && annotation instanceof ScriptGetter) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(annotation, scriptEngine);
				return isSupported;
			}
		}

		return false;
	}

	@Override
	public boolean isMethodVisible(Object instance, Method method) {
		Annotation annotation = ScriptAnnotation.getScriptAnnotation(method);
		
		if (annotation != null && annotation instanceof ScriptFunction) {
			boolean isSupported = ScriptAnnotation.isEngineSupported(annotation, scriptEngine);
			return isSupported;
		}
		
		return false;
	}

	@Override
	public boolean isStaticFieldVisible(Class<?> type, String fieldName) {
		return true;
	}

	@Override
	public boolean isStaticMethodVisible(Class<?> type, Method method) {
		return true;
	}

}

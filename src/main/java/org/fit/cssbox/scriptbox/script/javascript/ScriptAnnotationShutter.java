package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptClass;
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
			Class<?> instanceType = instance.getClass();		
			char firstCharacter = fieldName.charAt(0);
			String getterMethodName = "get" + Character.toUpperCase(firstCharacter) + fieldName.substring(1);
			
			Method method = null;
			
			try {
				method = instanceType.getMethod(getterMethodName);
			} catch (Exception e) {
				return false;
			}
			
			Annotation classAnnotation = instanceType.getAnnotation(ScriptClass.class);
			boolean allFields = ScriptAnnotation.containsOption(classAnnotation, ScriptClass.ALL_FIELDS);
			
			if (allFields) {
				return true;
			}
			
			if (method.isAnnotationPresent(ScriptGetter.class)) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(instanceType, method, scriptEngine);
				return isSupported;
			}
		}

		return false;
	}

	@Override
	public boolean isMethodVisible(Object instance, Method method) {
		Class<?> instanceType = instance.getClass();	
		Annotation classAnnotation = instanceType.getAnnotation(ScriptClass.class);
		boolean allMethods = ScriptAnnotation.containsOption(classAnnotation, ScriptClass.ALL_METHODS);
		
		if (allMethods) {
			return true;
		}

		if (method.isAnnotationPresent(ScriptFunction.class)) {
			boolean isSupported = ScriptAnnotation.isEngineSupported(instanceType, method, scriptEngine);
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

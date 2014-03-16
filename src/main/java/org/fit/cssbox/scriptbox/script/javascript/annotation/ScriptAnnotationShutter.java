package org.fit.cssbox.scriptbox.script.javascript.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotation;
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
	public boolean isFieldVisible(Object instance, Field field) {
		/*if (fieldName.length() > 0) {
			Class<?> instanceType = instance.getClass();		
			char firstCharacter = fieldName.charAt(0);
			String getterMethodName = "get" + Character.toUpperCase(firstCharacter) + fieldName.substring(1);
			
			Method method = null;
			
			try {
				method = instanceType.getMethod(getterMethodName);
			} catch (Exception e) {
				return false;
			}

			return ScriptAnnotation.testForScriptGetter(instanceType, method, scriptEngine);
		}*/

		return false;
	}

	@Override
	public boolean isMethodVisible(Object instance, Method method) {
		Class<?> instanceType = instance.getClass();
		return ScriptAnnotation.testForScriptFunction(instanceType, method, scriptEngine);
	}

	@Override
	public boolean isStaticFieldVisible(Class<?> type, Field field) {
		return true;
	}

	@Override
	public boolean isStaticMethodVisible(Class<?> type, Method method) {
		return true;
	}

}

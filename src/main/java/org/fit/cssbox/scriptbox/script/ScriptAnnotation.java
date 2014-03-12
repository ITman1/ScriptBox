package org.fit.cssbox.scriptbox.script;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ScriptAnnotationException;

public class ScriptAnnotation {
	public static boolean isScriptAnnotation(Annotation annotation) {		
		return 	annotation instanceof ScriptGetter || 
				annotation instanceof ScriptSetter || 
				annotation instanceof ScriptFunction;
	}
	
	public static boolean containsOption(Annotation classAnnotation, String option) {
		if (classAnnotation != null) {
			ScriptClass scriptClassAnnotation = (ScriptClass)classAnnotation;
			String options[] = scriptClassAnnotation.options();
			
			for (String currOption : options) {
				if (currOption.equals(option)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static boolean isEngineSupported(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		Annotation methodAnnotation = getMethodScriptAnnotation(method);
		Annotation clazzAnnotation = getClassScriptAnnotation(clazz);

		boolean engineSupported = true;
		if (clazzAnnotation != null) {
			engineSupported = engineSupported && isEngineSupported(clazzAnnotation, scriptEngine);
		}
		engineSupported = engineSupported && isEngineSupported(methodAnnotation, scriptEngine);
		
		return engineSupported;
	}
	
	public static boolean isEngineSupported(Annotation annotation, BrowserScriptEngine scriptEngine) {
		String engineName = scriptEngine.getBrowserFactory().getEngineShortName();
		String engines[] = null;		
		if (annotation instanceof ScriptGetter) {
			ScriptGetter scriptGetter = (ScriptGetter)annotation;
			engines = scriptGetter.engines();
		} else if (annotation instanceof ScriptSetter) {
			ScriptSetter scriptSetter = (ScriptSetter)annotation;
			engines = scriptSetter.engines();
		} else if (annotation instanceof ScriptFunction) {
			ScriptFunction scriptFunction = (ScriptFunction)annotation;
			engines = scriptFunction.engines();
		} else if (annotation instanceof ScriptClass) {
			ScriptClass scriptClass = (ScriptClass)annotation;
			engines = scriptClass.engines();
		} else {
			throw new ScriptAnnotationException("Passed annotation is not script annotation!");
		}
		
		if (engines == null || engines.length == 0) {
			return true;
		}
		
		return isEngineSuported(engines, engineName);
	}
	
	public static boolean isEngineSuported(String engines[], String engineName) {
		for (String engine : engines) {
			if (engine.equals(engineName)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Annotation getClassScriptAnnotation(Class<?> clazz) {
		Annotation clazzAnnotation = clazz.getAnnotation(ScriptClass.class);
				
		return clazzAnnotation;
	}
	
	public static Annotation getMethodScriptAnnotation(Method method) {
		Annotation scriptAnnotation = null;
		Annotation returnAnnotation = null;
		int annotationsCounter = 0;
		
		if ((scriptAnnotation = method.getAnnotation(ScriptGetter.class)) != null) {
			annotationsCounter++;
			returnAnnotation = scriptAnnotation;
		}
		
		if ((scriptAnnotation = method.getAnnotation(ScriptSetter.class)) != null) {
			annotationsCounter++;
			returnAnnotation = scriptAnnotation;
		}
		
		if ((scriptAnnotation = method.getAnnotation(ScriptFunction.class)) != null) {
			annotationsCounter++;
			returnAnnotation = scriptAnnotation;
		}
		
		if (annotationsCounter > 1) {
			throw new ScriptAnnotationException("Method has multiple script annotations!");
		}
		
		return returnAnnotation;
	}
}

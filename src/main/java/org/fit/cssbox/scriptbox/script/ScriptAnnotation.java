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
		} else {
			throw new ScriptAnnotationException("Passed annotation is not script annotation!");
		}
		
		if (engines == null || engines.length == 0) {
			return true;
		}
		
		List<String> enginesList = Arrays.asList(engines);
		
		return engineName != null && !engineName.isEmpty() && enginesList.contains(engineName);
	}
	
	public static Annotation getScriptAnnotation(Method method) {
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

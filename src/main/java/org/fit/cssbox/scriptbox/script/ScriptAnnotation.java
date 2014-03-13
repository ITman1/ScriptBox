package org.fit.cssbox.scriptbox.script;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ScriptAnnotationException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.mozilla.javascript.Scriptable;

public class ScriptAnnotation {
	public static boolean isScriptAnnotation(Annotation annotation) {		
		return 	annotation instanceof ScriptGetter || 
				annotation instanceof ScriptSetter || 
				annotation instanceof ScriptFunction;
	}
	
	public static boolean containsOption(Annotation classAnnotation, String option) {
		if (classAnnotation != null && classAnnotation instanceof ScriptClass) {
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
		boolean classSupported = false;
		boolean methodSupported = false;
		
		if (clazzAnnotation != null) {
			classSupported = isEngineSupported(clazzAnnotation, scriptEngine);
		}
		
		if (methodAnnotation != null) {
			methodSupported = isEngineSupported(methodAnnotation, scriptEngine);
		}
		
		boolean engineSupported = false;
		if (clazzAnnotation != null) {
			engineSupported = classSupported;
			if (methodAnnotation != null) {
				engineSupported = engineSupported && methodSupported;
			}
		} else if (methodAnnotation != null) {
			engineSupported = methodSupported;
		}
		
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
	
	public static Annotation getClassScriptAnnotation(Method method) {
		Class<?> clazz = method.getClass();
		return getClassScriptAnnotation(clazz);
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
	
	public static boolean isSupportedAndValid(Class<? extends Annotation> annotationType, String classOption, Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		boolean engineSupported = ScriptAnnotation.isEngineSupported(clazz, method, scriptEngine);
		boolean hasValidAnnotation = false;
		
		Annotation classAnnotation = ScriptAnnotation.getClassScriptAnnotation(method);
		Annotation methodAnnotation = ScriptAnnotation.getMethodScriptAnnotation(method);
		if (ScriptAnnotation.containsOption(classAnnotation, classOption)) {
			hasValidAnnotation = true;
		} else if (methodAnnotation != null) {
			hasValidAnnotation = annotationType.isAssignableFrom(methodAnnotation.getClass());
		}
		
		return engineSupported && hasValidAnnotation;
	}
	
	public static boolean hasSupportedAndValidGetter(Class<?> clazz, BrowserScriptEngine scriptEngine) {
		if (ObjectGetter.class.isAssignableFrom(clazz)) {
			Method method = ObjectFunction.getObjectGetterMetod(clazz);			
			return isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, clazz, method, scriptEngine);
		}
		
		return false;
	}
}

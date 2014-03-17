package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ScriptAnnotationException;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectGetter;

public class ScriptAnnotation {
	public static boolean isScriptAnnotation(Annotation annotation) {		
		return 	annotation instanceof ScriptGetter || 
				annotation instanceof ScriptSetter || 
				annotation instanceof ScriptFunction;
	}
	
	public static boolean containsMemberOption(Member member, String option) {
		Annotation annotation = getMemberScriptAnnotation(member);
		return containsOption(annotation, option);
	}
	
	public static boolean containsOption(Annotation classAnnotation, String option) {
		String options[] = null;
		
		if (classAnnotation == null) {
			return false;
		} else if (classAnnotation instanceof ScriptClass) {
			options = ((ScriptClass)classAnnotation).options();
		} else if (classAnnotation instanceof ScriptGetter) {
			options = ((ScriptGetter)classAnnotation).options();
		} else if (classAnnotation instanceof ScriptSetter) {
			options = ((ScriptSetter)classAnnotation).options();
		} else if (classAnnotation instanceof ScriptField) {
			options = ((ScriptField)classAnnotation).options();
		} else if (classAnnotation instanceof ScriptFunction) {
			options = ((ScriptFunction)classAnnotation).options();
		} else {
			return false;
		}
		
		for (String currOption : options) {
			if (currOption.equals(option)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isEngineSupported(Class<?> clazz, Member member, BrowserScriptEngine scriptEngine) {
		Annotation methodAnnotation = getMemberScriptAnnotation(member);
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
	
	public static Annotation getClassScriptAnnotation(Member member) {
		Class<?> clazz = member.getClass();
		return getClassScriptAnnotation(clazz);
	}
	
	public static Annotation getClassScriptAnnotation(Class<?> clazz) {
		Annotation clazzAnnotation = clazz.getAnnotation(ScriptClass.class);
		return clazzAnnotation;
	}
	
	public static Annotation getMemberScriptAnnotation(Member member) {
		Annotation scriptAnnotation = null;
		Annotation returnAnnotation = null;
		int annotationsCounter = 0;
		
		if (member instanceof Method) {
			Method method = (Method)member;
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
		} else if (member instanceof Field) {
			Field field = (Field) member;
			if ((scriptAnnotation = field.getAnnotation(ScriptField.class)) != null) {
				annotationsCounter++;
				returnAnnotation = scriptAnnotation;
			}
		}
				
		if (annotationsCounter > 1) {
			throw new ScriptAnnotationException("Method has multiple script annotations!");
		}
		
		return returnAnnotation;
	}
	
	public static boolean testForScriptGetter(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		Class<?> returnType = method.getReturnType();
		
		boolean isSupportedAndValid = isSupportedAndValid(ScriptGetter.class, ScriptClass.ALL_FIELDS, clazz, method, scriptEngine);
		boolean isSignatureValid = parameterTypes.length == 0 && returnType != Void.TYPE;;
		
		return isSupportedAndValid && isSignatureValid;
	}
	
	public static boolean testForScriptSetter(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		boolean isSupportedAndValid = isSupportedAndValid(ScriptSetter.class, ScriptClass.ALL_FIELDS, clazz, method, scriptEngine);
		boolean isSignatureValid = parameterTypes.length == 1;
		
		return isSupportedAndValid && isSignatureValid;
	}
	
	public static boolean testForScriptFunction(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, clazz, method, scriptEngine);
		return isSupportedAndValid;
	}
	
	public static boolean testForScriptField(Class<?> clazz, Field field, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptField.class, ScriptClass.ALL_FIELDS, clazz, field, scriptEngine);
		return isSupportedAndValid;
	}
	
	public static boolean testForObjectGetter(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		if (ClassFunction.isObjectGetterMethod(clazz, method)) {	
			return isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, clazz, method, scriptEngine);
		}
		
		return false;
	}

	public static String extractFieldNameFromGetter(Method method) {
		String fieldName = null;
		Annotation annotation = getMemberScriptAnnotation(method);
		
		if (annotation instanceof ScriptGetter) {
			ScriptGetter getterAnnotation = (ScriptGetter) annotation;
			fieldName = getterAnnotation.field();
			if (fieldName.equals(ScriptGetter.EMPTY)) {
				fieldName = ClassField.extractFieldNameFromGetter(method);
			}
		} else {
			throw new ScriptAnnotationException("Passed method is not script getter!");
		}
		
		return fieldName;
	}

	public static String extractFieldNameFromSetter(Method method) {
		String fieldName = null;
		Annotation annotation = getMemberScriptAnnotation(method);
		
		if (annotation instanceof ScriptSetter) {
			ScriptSetter getterAnnotation = (ScriptSetter) annotation;
			fieldName = getterAnnotation.field();
			if (fieldName.equals(ScriptGetter.EMPTY)) {
				fieldName = ClassField.extractFieldNameFromSetter(method);
			}
		} else {
			throw new ScriptAnnotationException("Passed method is not script getter!");
		}
		
		return fieldName;
	}

	public static String extractFieldName(Field field) {
		return ClassField.extractFieldName(field);
	}

	public static String extractFunctionName(Method method) {
		return ClassFunction.extractFunctionName(method);
	}
	
	protected static boolean isSupportedAndValid(Class<? extends Annotation> annotationType, String classOption, Class<?> clazz, Member member, BrowserScriptEngine scriptEngine) {
		boolean engineSupported = ScriptAnnotation.isEngineSupported(clazz, member, scriptEngine);
		boolean hasValidAnnotation = false;
		
		Annotation classAnnotation = ScriptAnnotation.getClassScriptAnnotation(member);
		Annotation methodAnnotation = ScriptAnnotation.getMemberScriptAnnotation(member);
		if (ScriptAnnotation.containsOption(classAnnotation, classOption)) {
			hasValidAnnotation = true;
		} else if (methodAnnotation != null) {
			hasValidAnnotation = annotationType.isAssignableFrom(methodAnnotation.getClass());
		}
		
		return engineSupported && hasValidAnnotation;
	}
}

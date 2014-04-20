/**
 * ScriptAnnotation.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.java.ClassField;
import org.fit.cssbox.scriptbox.script.java.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.ScriptAnnotationException;

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
		
		return containsOption(option, options);
	}
	
	public static boolean containsOption(String option, String[] options) {
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
		} else if (annotation instanceof ScriptConstructor) {
			ScriptConstructor scriptConstructor = (ScriptConstructor)annotation;
			engines = scriptConstructor.engines();
		} else if (annotation instanceof ScriptClass) {
			ScriptClass scriptClass = (ScriptClass)annotation;
			engines = scriptClass.engines();
		} else if (annotation instanceof ScriptField) {
			ScriptField scriptClass = (ScriptField)annotation;
			engines = scriptClass.engines();
		} else if (annotation instanceof InvisibleField) {
			return false;
		} else if (annotation instanceof InvisibleFunction) {
			return false;
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
			
			if ((scriptAnnotation = method.getAnnotation(InvisibleFunction.class)) != null) {
				annotationsCounter++;
				returnAnnotation = scriptAnnotation;
			}
		} else if (member instanceof Field) {
			Field field = (Field) member;
			if ((scriptAnnotation = field.getAnnotation(ScriptField.class)) != null) {
				annotationsCounter++;
				returnAnnotation = scriptAnnotation;
			}
			
			if ((scriptAnnotation = field.getAnnotation(InvisibleField.class)) != null) {
				annotationsCounter++;
				returnAnnotation = scriptAnnotation;
			}
		} else if (member instanceof Constructor<?>) {
			Constructor<?> constructor = (Constructor<?>) member;
			if ((scriptAnnotation = constructor.getAnnotation(ScriptConstructor.class)) != null) {
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
		
		Annotation annotation = getMemberScriptAnnotation(method);
		if (annotation == null) {
			isSignatureValid = ClassField.isGetter(method);
		}
		
		return isSupportedAndValid && isSignatureValid;
	}
	
	public static boolean testForScriptSetter(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		
		boolean isSupportedAndValid = isSupportedAndValid(ScriptSetter.class, ScriptClass.ALL_FIELDS, clazz, method, scriptEngine);
		boolean isSignatureValid = parameterTypes.length == 1;
		
		Annotation annotation = getMemberScriptAnnotation(method);
		if (annotation == null) {
			isSignatureValid = ClassField.isSetter(method);
		}
		
		return isSupportedAndValid && isSignatureValid;
	}
	
	public static boolean testForScriptConstructor(Class<?> clazz, Constructor<?> constructor, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptConstructor.class, ScriptClass.ALL_CONSTRUCTORS, clazz, constructor, scriptEngine);
		return isSupportedAndValid;
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
			if (!fieldName.equals(ScriptGetter.EMPTY)) {
				return fieldName;
			}
		}
		
		return ClassField.extractFieldNameFromGetter(method);
	}

	public static String extractFieldNameFromSetter(Method method) {
		String fieldName = null;
		Annotation annotation = getMemberScriptAnnotation(method);
		
		if (annotation instanceof ScriptSetter) {
			ScriptSetter getterAnnotation = (ScriptSetter) annotation;
			fieldName = getterAnnotation.field();
			if (!fieldName.equals(ScriptGetter.EMPTY)) {
				return fieldName;
			}
		}
		
		return ClassField.extractFieldNameFromSetter(method);
	}

	public static String extractFieldName(Field field) {
		return ClassField.extractFieldName(field);
	}

	public static String extractFunctionName(Method method) {
		return ClassFunction.extractFunctionName(method);
	}
	
	public static boolean isFieldEnumerable(Method objectFieldGetter, Method objectFieldSetter, Field field) {
		boolean isEnumerable = (field == null)? 
				containsOption(ScriptField.ENUMERABLE, ScriptField.DEFAULT_OPTIONS) : 
				containsMemberOption(field, ScriptField.ENUMERABLE);
		isEnumerable = isEnumerable || containsMemberOption(objectFieldGetter, ScriptGetter.ENUMERABLE_FIELD);
		isEnumerable = isEnumerable || containsMemberOption(objectFieldSetter, ScriptSetter.ENUMERABLE_FIELD);
		
		return isEnumerable;
	}
	
	public static boolean isFunctionEnumerable(Method function) {
		return containsMemberOption(function, ScriptFunction.ENUMERABLE)
			|| containsMemberOption(function, ScriptGetter.CALLABLE_ENUMERABLE_GETTER)
			|| containsMemberOption(function, ScriptSetter.CALLABLE_ENUMERABLE_SETTER);
	}
	
	public static boolean isCallable(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		boolean isPureFunction = 
				ScriptAnnotation.testForScriptFunction(clazz, method, scriptEngine);
		boolean isGetterFunction = 
				ScriptAnnotation.testForScriptGetter(clazz, method, scriptEngine) &&
					(ScriptAnnotation.containsMemberOption(method, ScriptGetter.CALLABLE_GETTER) || 
					ScriptAnnotation.containsMemberOption(method, ScriptGetter.CALLABLE_ENUMERABLE_GETTER));
		boolean isSetterFunction =
				ScriptAnnotation.testForScriptSetter(clazz, method, scriptEngine) &&
					(ScriptAnnotation.containsMemberOption(method, ScriptSetter.CALLABLE_SETTER) ||
					ScriptAnnotation.containsMemberOption(method, ScriptSetter.CALLABLE_ENUMERABLE_SETTER));
		boolean objectGetterFunction = 
				ScriptAnnotation.testForObjectGetter(clazz, method, scriptEngine);
		
		return (isPureFunction || isGetterFunction || isSetterFunction) && !objectGetterFunction;
	}
	
	protected static boolean isSupportedAndValid(Class<? extends Annotation> annotationType, String classOption, Class<?> clazz, Member member, BrowserScriptEngine scriptEngine) {
		boolean engineSupported = ScriptAnnotation.isEngineSupported(clazz, member, scriptEngine);
		boolean hasValidAnnotation = false;
		
		Annotation classAnnotation = ScriptAnnotation.getClassScriptAnnotation(clazz);
		Annotation memberAnnotation = ScriptAnnotation.getMemberScriptAnnotation(member);
		if (memberAnnotation != null) {
			hasValidAnnotation = annotationType.isAssignableFrom(memberAnnotation.getClass());
		} else if (ScriptAnnotation.containsOption(classAnnotation, classOption)) {
			hasValidAnnotation = true;
		}
		
		return engineSupported && hasValidAnnotation;
	}
}

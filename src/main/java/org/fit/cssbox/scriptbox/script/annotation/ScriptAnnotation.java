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
import org.fit.cssbox.scriptbox.script.exceptions.ScriptAnnotationException;
import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ClassFunction;

public class ScriptAnnotation {
	
	/**
	 * Tests whether is passed annotation the script annotation.
	 * 
	 * @param annotation Annotation to be tested.
	 * @return True if is given annotation the script annotation.
	 */
	public static boolean isScriptAnnotation(Annotation annotation) {		
		return 	annotation instanceof ScriptGetter || 
				annotation instanceof ScriptSetter || 
				annotation instanceof ScriptFunction || 
				annotation instanceof ScriptClass || 
				annotation instanceof ScriptConstructor || 
				annotation instanceof InvisibleField || 
				annotation instanceof InvisibleFunction;
	}
	
	/**
	 * Tests whether given member contains script annotation with the given option.
	 * 
	 * @param member Member to be tested for the option.
	 * @param option Option to be located in the member annotation.
	 * @return True if given member contains given option.
	 */
	public static boolean containsMemberOption(Member member, String option) {
		Annotation annotation = getMemberScriptAnnotation(member);
		return containsOption(annotation, option);
	}
	
	/**
	 * Tests whether given class annotation contains the given option.
	 * 
	 * @param classAnnotation Class annotation to be tested for the option.
	 * @param option Option to be located in the class annotation.
	 * @return True if given class annotation contains given option.
	 */
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
	
	/**
	 * Tests whether is given option inside arrays of options.
	 * 
	 * @param option Option to be located in the option array.
	 * @param options Array with the options.
	 * @return True if is given option inside array of given options.
	 */
	public static boolean containsOption(String option, String[] options) {
		for (String currOption : options) {
			if (currOption.equals(option)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Tests whether is passed script engine supported by a member of some class.
	 * 
	 * @param clazz Class containing the script member.
	 * @param member Class member.
	 * @param scriptEngine Script engine to be tested whether is supported by a member.
	 * @return True if is passed script engine supported by a member of some class.
	 */
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
	
	/**
	 * Tests whether is passed script engine supported by a member script annotation.
	 * 
	 * @param annotation Script annotation.
	 * @param scriptEngine Script engine to be tested whether is supported by a member script annotation.
	 * @return True if is passed script engine supported by a member script annotation.
	 */
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
			InvisibleField scriptClass = (InvisibleField)annotation;
			engines = scriptClass.engines();
		} else if (annotation instanceof InvisibleFunction) {
			InvisibleFunction scriptClass = (InvisibleFunction)annotation;
			engines = scriptClass.engines();
		} else {
			throw new ScriptAnnotationException("Passed annotation is not script annotation!");
		}
		
		if (engines == null || engines.length == 0) {
			return true;
		}
		
		return isEngineSuported(engines, engineName);
	}
	
	/**
	 * Tests whether is given engine name inside arrays of engine names.
	 * 
	 * @param engines Array with the engine names.
	 * @param engineName Option to be located in the engine names array.
	 * @return True if is given option inside array of given engine names.
	 */
	public static boolean isEngineSuported(String engines[], String engineName) {
		for (String engine : engines) {
			if (engine.equals(engineName)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Returns class script annotation if there is any.
	 * 
	 * @param member Member that may contain the class script annotation.
	 * @return Class script annotation if there is any, otherwise null.
	 */
	public static Annotation getClassScriptAnnotation(Member member) {
		Class<?> clazz = member.getClass();
		return getClassScriptAnnotation(clazz);
	}
	
	/**
	 * Returns class script annotation if there is any.
	 * 
	 * @param clazz Class that may contain the class script annotation.
	 * @return Class script annotation if there is any, otherwise null.
	 */
	public static Annotation getClassScriptAnnotation(Class<?> clazz) {
		Annotation clazzAnnotation = clazz.getAnnotation(ScriptClass.class);
		return clazzAnnotation;
	}
	
	/**
	 * Returns member script annotation if there is any.
	 * 
	 * @param member Member that may contain the member script annotation.
	 * @return Member script annotation if there is any, otherwise null.
	 */
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
	
	/**
	 * Tests whether is given method of passed class the supported script getter of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param method Method to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a method.
	 * @return True if passed method is supported script getter.
	 */
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
	
	/**
	 * Tests whether is given method of passed class the supported script setter of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param method Method to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a method.
	 * @return True if passed method is supported script setter.
	 */
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
	
	/**
	 * Tests whether is given constructor of passed class the supported script constructor of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param constructor Constructor to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a constructor.
	 * @return True if passed method is supported script constructor.
	 */
	public static boolean testForScriptConstructor(Class<?> clazz, Constructor<?> constructor, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptConstructor.class, ScriptClass.ALL_CONSTRUCTORS, clazz, constructor, scriptEngine);
		return isSupportedAndValid;
	}
	
	/**
	 * Tests whether is given method of passed class the supported script function of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param method Method to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a method.
	 * @return True if passed method is supported script function.
	 */
	public static boolean testForScriptFunction(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, clazz, method, scriptEngine);
		return isSupportedAndValid;
	}
	
	/**
	 * Tests whether is given field of passed class the supported script field of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param field Field to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a field.
	 * @return True if passed method is supported script field.
	 */
	public static boolean testForScriptField(Class<?> clazz, Field field, BrowserScriptEngine scriptEngine) {
		boolean isSupportedAndValid = isSupportedAndValid(ScriptField.class, ScriptClass.ALL_FIELDS, clazz, field, scriptEngine);
		return isSupportedAndValid;
	}
	
	/**
	 * Tests whether is given method of passed class the supported object getter of passed script engine.
	 * 
	 * @param clazz Class of the method.
	 * @param method Method to be tested.
	 * @param scriptEngine Script engine to be tested whether is supported by a object getter.
	 * @return True if passed method is supported object getter.
	 */
	public static boolean testForObjectGetter(Class<?> clazz, Method method, BrowserScriptEngine scriptEngine) {
		if (ClassFunction.isObjectGetterMethod(clazz, method)) {	
			return isSupportedAndValid(ScriptFunction.class, ScriptClass.ALL_METHODS, clazz, method, scriptEngine);
		}
		
		return false;
	}

	/**
	 * Extracts the name of the field from the field getter.
	 * 
	 * @param method Script getter method.
	 * @return Name of the script field.
	 */
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

	/**
	 * Extracts the name of the field from the field setter.
	 * 
	 * @param method Script setter method.
	 * @return Name of the script field.
	 */
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

	/**
	 * Extracts the name of the field from the field.
	 * 
	 * @param field Field.
	 * @return Name of the script field.
	 */
	public static String extractFieldName(Field field) {
		String fieldName = null;
		Annotation annotation = getMemberScriptAnnotation(field);
		
		if (annotation instanceof ScriptField) {
			ScriptField fieldAnnotation = (ScriptField) annotation;
			fieldName = fieldAnnotation.field();
			if (!fieldName.equals(ScriptField.EMPTY)) {
				return fieldName;
			}
		}
		
		return ClassField.extractFieldName(field);
	}

	/**
	 * Extracts the name of the function from the method.
	 * 
	 * @param method Method.
	 * @return Name of the script function.
	 */
	public static String extractFunctionName(Method method) {
		return ClassFunction.extractFunctionName(method);
	}
	
	/**
	 * Extracts the name of the class from the class instance.
	 * 
	 * @param clazz Class.
	 * @return Name of the class.
	 */
	public static String extractClassName(Class<?> clazz) {
		String className = null;
		Annotation annotation = getClassScriptAnnotation(clazz);
		
		if (annotation instanceof ScriptClass) {
			ScriptClass classAnnotation = (ScriptClass) annotation;
			className = classAnnotation.name();
			if (!className.equals(ScriptClass.EMPTY)) {
				return className;
			}
		}
		
		return clazz.getSimpleName();
	}
	
	/**
	 * Tests whether is given field enumerable.
	 * 
	 * @param objectFieldGetter Field getter of the script field to be tested.
	 * @param objectFieldSetter Field setter of the script field to be tested.
	 * @param field Field representing script field to be tested.
	 * @return True if is given field enumerable.
	 */
	public static boolean isFieldEnumerable(Method objectFieldGetter, Method objectFieldSetter, Field field) {
		boolean isEnumerable = (field == null)? 
				containsOption(ScriptField.ENUMERABLE, ScriptField.DEFAULT_OPTIONS) : 
				containsMemberOption(field, ScriptField.ENUMERABLE);
		isEnumerable = isEnumerable || containsMemberOption(objectFieldGetter, ScriptGetter.ENUMERABLE_FIELD);
		isEnumerable = isEnumerable || containsMemberOption(objectFieldSetter, ScriptSetter.ENUMERABLE_FIELD);
		
		return isEnumerable;
	}
	
	/**
	 * Tests whether is given function enumerable.
	 * 
	 * @param function Method representing script function to be tested.
	 * @return True if is given method enumerable.
	 */
	public static boolean isFunctionEnumerable(Method function) {
		return containsMemberOption(function, ScriptFunction.ENUMERABLE)
			|| containsMemberOption(function, ScriptGetter.CALLABLE_ENUMERABLE_GETTER)
			|| containsMemberOption(function, ScriptSetter.CALLABLE_ENUMERABLE_SETTER);
	}
	
	/**
	 * Tests whether is given method of passed class callable by a script engine.
	 * 
	 * @param clazz Class containing the method.
	 * @param method Method to be tested.
	 * @param scriptEngine Script engine to be tested whether can call passed a method.
	 * @return True if is given method callable from the passed script engine, otherwise false.
	 */
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
	
	private static boolean isSupportedAndValid(Class<? extends Annotation> annotationType, String classOption, Class<?> clazz, Member member, BrowserScriptEngine scriptEngine) {
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

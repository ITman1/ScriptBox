package org.fit.cssbox.scriptbox.script.javascript.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectField;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectMembersResolver;

public class ScriptAnnotationObjectMembersResolver extends ObjectMembersResolver {
	protected BrowserScriptEngine scriptEngine;
	
	public ScriptAnnotationObjectMembersResolver(Object object, BrowserScriptEngine browserScriptEngine) {
		super(object);
		this.scriptEngine = browserScriptEngine;
	}

	@Override
	public boolean isGetter(Method method) {
		return ScriptAnnotation.testForScriptGetter(objectType, method, scriptEngine);
	}
	
	@Override
	public boolean isSetter(Method method) {
		return ScriptAnnotation.testForScriptSetter(objectType, method, scriptEngine);
	}
	
	@Override
	public boolean isFunction(Method method) {
		boolean isPureFunction = ScriptAnnotation.testForScriptFunction(objectType, method, scriptEngine);
		boolean isGetterFunction = ScriptAnnotation.containsMemberOption(method, ScriptGetter.CALLABLE);
		boolean isSetterFunction = ScriptAnnotation.containsMemberOption(method, ScriptSetter.CALLABLE);
		boolean objectGetterFunction = ObjectFunction.isObjectGetterMethod(objectType, method);
		
		return (isPureFunction || isGetterFunction || isSetterFunction) && !objectGetterFunction;
	}	
	
	@Override
	public boolean isField(Field field) {
		return ScriptAnnotation.testForScriptField(objectType, field, scriptEngine);
	}
	
	@Override
	public String extractFieldNameFromGetter(Method method) {
		return ScriptAnnotation.extractFieldNameFromGetter(method);
	}
	
	@Override
	public String extractFieldNameFromSetter(Method method) {
		return ScriptAnnotation.extractFieldNameFromSetter(method);
	}
	
	@Override
	public String extractFieldName(Field field) {
		return ScriptAnnotation.extractFieldName(field);
	}

	@Override
	public String extractFunctionName(Method method) {
		return ScriptAnnotation.extractFunctionName(method);
	}

	@Override
	public ObjectField constructObjectField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
		boolean getOverride = ScriptAnnotation.containsMemberOption(objectFieldGetter, ScriptGetter.FIELD_GET_OVERRIDE);
		boolean setOverride = ScriptAnnotation.containsMemberOption(objectFieldGetter, ScriptSetter.FIELD_SET_OVERRIDE);
		return new ObjectField(object, objectFieldGetter, objectFieldSetter, field, getOverride, setOverride);
	}

	@Override
	public ObjectFunction constructObjectFunction(Method method) {
		return new ObjectFunction(object, method);
	}
}

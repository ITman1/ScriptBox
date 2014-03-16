package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultObjectMembersResolver extends ObjectMembersResolver {
	
	public DefaultObjectMembersResolver(Object object) {
		super(object);
	}

	@Override
	public boolean isGetter(Method method) {
		return ObjectField.isGetter(method);
	}
	
	@Override
	public boolean isSetter(Method method) {
		return ObjectField.isSetter(method);
	}
	
	@Override
	public boolean isFunction(Method method) {
		return ObjectFunction.isFunction(objectType, method);
	}
	
	@Override
	public boolean isField(Field field) {
		return ObjectField.isField(field);
	}
	
	@Override
	public String extractFieldNameFromGetter(Method method) {
		return ObjectField.extractFieldNameFromGetter(method);
	}
	
	@Override
	public String extractFieldNameFromSetter(Method method) {
		return ObjectField.extractFieldNameFromSetter(method);
	}
	
	@Override
	public String extractFieldName(Field field) {
		return ObjectField.extractFieldName(field);
	}
	
	@Override
	public String extractFunctionName(Method method) {
		return ObjectFunction.extractFunctionName(method);
	}
	
	@Override
	public ObjectField constructObjectField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
		return new ObjectField(object, objectFieldGetter, objectFieldSetter, field);
	}
	
	@Override
	public ObjectFunction constructObjectFunction(Method method) {
		return new ObjectFunction(object, method);
	}
}

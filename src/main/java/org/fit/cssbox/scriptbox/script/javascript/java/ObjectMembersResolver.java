package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ObjectMembersResolver {
	protected Object object;
	protected Class<?> objectType;
	
	public ObjectMembersResolver(Object object) {
		this.object = object;
		this.objectType = object.getClass();
	}
	
	public Object getObject() {
		return object;
	}
	
	public Class<?> getObjectType() {
		return objectType;
	}
	
	public abstract boolean isGetter(Method method);
	public abstract boolean isSetter(Method method);
	public abstract boolean isFunction(Method method);
	public abstract boolean isField(Field field);
	public abstract String extractFieldNameFromGetter(Method method);
	public abstract String extractFieldNameFromSetter(Method method);
	public abstract String extractFieldName(Field field);
	public abstract String extractFunctionName(Method method);
	public abstract ObjectField constructObjectField(Method objectFieldGetter, Method objectFieldSetter, Field field);
	public abstract ObjectFunction constructObjectFunction(Method method);
}

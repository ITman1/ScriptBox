package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ClassMembersResolver {
	protected Class<?> clazz;
	
	public ClassMembersResolver(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public abstract boolean isObjectGetter(Method method);
	public abstract boolean isGetter(Method method);
	public abstract boolean isSetter(Method method);
	public abstract boolean isFunction(Method method);
	public abstract boolean isField(Field field);
	public abstract String extractFieldNameFromGetter(Method method);
	public abstract String extractFieldNameFromSetter(Method method);
	public abstract String extractFieldName(Field field);
	public abstract String extractFunctionName(Method method);
	public abstract ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field);
	public abstract ClassFunction constructClassFunction(Method method);
}

package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DefaultClassMembersResolverFactory implements ClassMembersResolverFactory {
	class DefaultClassMembersResolver extends ClassMembersResolver {
		
		public DefaultClassMembersResolver(Class<?> clazz) {
			super(clazz);
		}
		
		@Override
		public boolean isObjectGetter(Method method) {
			return ClassFunction.isObjectGetterMethod(clazz, method);
		}
	
		@Override
		public boolean isGetter(Method method) {
			return ClassField.isGetter(method);
		}
		
		@Override
		public boolean isSetter(Method method) {
			return ClassField.isSetter(method);
		}
		
		@Override
		public boolean isFunction(Method method) {
			return ClassFunction.isFunction(clazz, method);
		}
		
		@Override
		public boolean isField(Field field) {
			return ClassField.isField(field);
		}
		
		@Override
		public String extractFieldNameFromGetter(Method method) {
			return ClassField.extractFieldNameFromGetter(method);
		}
		
		@Override
		public String extractFieldNameFromSetter(Method method) {
			return ClassField.extractFieldNameFromSetter(method);
		}
		
		@Override
		public String extractFieldName(Field field) {
			return ClassField.extractFieldName(field);
		}
		
		@Override
		public String extractFunctionName(Method method) {
			return ClassFunction.extractFunctionName(method);
		}
		
		@Override
		public ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
			return new ClassField(clazz, objectFieldGetter, objectFieldSetter, field);
		}
		
		@Override
		public ClassFunction constructClassFunction(Method method) {
			return new ClassFunction(clazz, method);
		}
	}

	@Override
	public ClassMembersResolver create(Class<?> clazz) {
		return new DefaultClassMembersResolver(clazz);
	}
	
	
}

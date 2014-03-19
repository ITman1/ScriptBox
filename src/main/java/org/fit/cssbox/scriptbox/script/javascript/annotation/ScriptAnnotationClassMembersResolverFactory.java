package org.fit.cssbox.scriptbox.script.javascript.annotation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolver;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;

public class ScriptAnnotationClassMembersResolverFactory implements ClassMembersResolverFactory {
	class ScriptAnnotationClassMembersResolver extends ClassMembersResolver {
		final String pernament_options[] = {ClassMember.PERNAMENT};
		final String pernament_enumerable_options[] = {ClassMember.PERNAMENT, ClassMember.ENUMERABLE};
		
		protected BrowserScriptEngine scriptEngine;
		
		public ScriptAnnotationClassMembersResolver(Class<?> clazz, BrowserScriptEngine browserScriptEngine) {
			super(clazz);
			this.scriptEngine = browserScriptEngine;
		}
	
		@Override
		public boolean isObjectGetter(Method method) {
			return ScriptAnnotation.testForObjectGetter(clazz, method, browserScriptEngine);
		}
		
		@Override
		public boolean isGetter(Method method) {
			return ScriptAnnotation.testForScriptGetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isSetter(Method method) {
			return ScriptAnnotation.testForScriptSetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isFunction(Method method) {
			return ScriptAnnotation.isCallable(clazz, method, scriptEngine);
		}	
		
		@Override
		public boolean isField(Field field) {
			return ScriptAnnotation.testForScriptField(clazz, field, scriptEngine);
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
		public ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
			boolean getOverride = ScriptAnnotation.containsMemberOption(objectFieldGetter, ScriptGetter.FIELD_GET_OVERRIDE);
			boolean setOverride = ScriptAnnotation.containsMemberOption(objectFieldSetter, ScriptSetter.FIELD_SET_OVERRIDE);
			boolean enumerable = ScriptAnnotation.isFieldEnumerable(objectFieldGetter, objectFieldSetter, field);
			String options[] = (enumerable)? pernament_enumerable_options : pernament_options;
			return new ClassField(clazz, objectFieldGetter, objectFieldSetter, field, getOverride, setOverride, options);
		}
	
		@Override
		public ClassFunction constructClassFunction(Method method) {
			boolean enumerable = ScriptAnnotation.isFunctionEnumerable(method);
			String options[] = (enumerable)? pernament_enumerable_options : pernament_options;
			return new ClassFunction(clazz, method, options);
		}
	}
	
	protected BrowserScriptEngine browserScriptEngine;

	public ScriptAnnotationClassMembersResolverFactory(BrowserScriptEngine browserScriptEngine) {
		this.browserScriptEngine = browserScriptEngine;
	}
	
	@Override
	public ClassMembersResolver create(Class<?> clazz) {
		return new ScriptAnnotationClassMembersResolver(clazz, browserScriptEngine);
	}
}

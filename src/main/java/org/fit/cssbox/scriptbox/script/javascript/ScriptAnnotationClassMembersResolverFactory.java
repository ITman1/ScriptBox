package org.fit.cssbox.scriptbox.script.javascript;

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
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.DefaultShutter;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.Shutter;

public class ScriptAnnotationClassMembersResolverFactory implements ClassMembersResolverFactory {
	class ScriptAnnotationClassMembersResolver extends ClassMembersResolver {
		final String pernament_options[] = {ClassMember.PERNAMENT};
		final String pernament_enumerable_options[] = {ClassMember.PERNAMENT, ClassMember.ENUMERABLE};
		
		protected BrowserScriptEngine scriptEngine;
		protected Shutter explicitGrantShutter;
		
		public ScriptAnnotationClassMembersResolver(Class<?> clazz, BrowserScriptEngine browserScriptEngine, Shutter explicitGrantShutter) {
			super(clazz);
			this.scriptEngine = browserScriptEngine;
			this.explicitGrantShutter = explicitGrantShutter;
		}
		
		@Override
		public boolean isObjectGetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassFunction.isObjectGetterMethod(clazz, method)) {
				return true;
			}
			return ScriptAnnotation.testForObjectGetter(clazz, method, browserScriptEngine);
		}
		
		@Override
		public boolean isGetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassField.isGetter(method)) {
				return true;
			}
			return ScriptAnnotation.testForScriptGetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isSetter(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassField.isSetter(method)) {
				return true;
			}
			return ScriptAnnotation.testForScriptSetter(clazz, method, scriptEngine);
		}
		
		@Override
		public boolean isFunction(Method method) {
			if (explicitGrantShutter.isMethodVisible(clazz, method) && ClassFunction.isFunction(clazz, method)) {
				return true;
			}
			return ScriptAnnotation.isCallable(clazz, method, scriptEngine);
		}	
		
		@Override
		public boolean isField(Field field) {
			if (explicitGrantShutter.isFieldVisible(clazz, field) && ClassField.isField(field)) {
				return true;
			}
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
	protected Shutter explicitGrantShutter;
	
	public ScriptAnnotationClassMembersResolverFactory(BrowserScriptEngine browserScriptEngine) {
		this(browserScriptEngine, new DefaultShutter());
	}
	
	public ScriptAnnotationClassMembersResolverFactory(BrowserScriptEngine browserScriptEngine, Shutter explicitGrantShutter) {
		this.browserScriptEngine = browserScriptEngine;
		this.explicitGrantShutter = explicitGrantShutter;
	}
	
	@Override
	public ClassMembersResolver create(Class<?> clazz) {
		return new ScriptAnnotationClassMembersResolver(clazz, browserScriptEngine, explicitGrantShutter);
	}
}

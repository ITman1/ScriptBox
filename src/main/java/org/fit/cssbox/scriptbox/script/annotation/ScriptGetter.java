package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface ScriptGetter {
	public static final String ENUMERABLE_FIELD = "enumerable";
	public static final String CALLABLE_GETTER = "callable";
	public static final String CALLABLE_ENUMERABLE_GETTER = "enum_callable";
	public static final String FIELD_GET_OVERRIDE = "field_override";
	public static final String EMPTY = "empty";
	public static final String[] DEFAULT_OPTIONS = {ENUMERABLE_FIELD, FIELD_GET_OVERRIDE};
	
	String[] engines() default {};
	String[] options() default {ENUMERABLE_FIELD, FIELD_GET_OVERRIDE};
	String field() default EMPTY;
}

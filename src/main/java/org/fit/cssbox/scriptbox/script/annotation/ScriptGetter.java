package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScriptGetter {
	public static final String ENUMERABLE = "enumerable";
	public static final String CALLABLE = "callable";
	public static final String ENUM_CALLABLE = "enum_callable";
	public static final String FIELD_GET_OVERRIDE = "field_override";
	public static final String EMPTY = "empty";
	
	String[] engines() default {};
	String[] options() default {ENUMERABLE, FIELD_GET_OVERRIDE};
	String field() default EMPTY;
}

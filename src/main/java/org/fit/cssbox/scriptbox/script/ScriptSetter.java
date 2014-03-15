package org.fit.cssbox.scriptbox.script;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScriptSetter  {
	public static final String ENUMERABLE = "enumerable";
	public static final String CALLABLE = "callable";
	public static final String ENUM_CALLABLE = "enum_callable";
	
	String[] engines() default {};
	String[] options() default {ENUMERABLE};
}

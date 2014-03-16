package org.fit.cssbox.scriptbox.script.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScriptField {
	public static final String ENUMERABLE = "enumerable";
	public static final String EMPTY = "empty";
	
	String[] engines() default {};
	String[] options() default {ENUMERABLE};
}

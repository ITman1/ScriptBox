package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ScriptFunction  {
	public static final String ENUMERABLE = "enumerable";
	
	String[] engines() default {};
	String[] options() default {};
}

package org.fit.cssbox.scriptbox.script.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface ScriptClass  {
	public final String ALL_METHODS = "all_methods";
	public final String ALL_FIELDS = "all_fields";
	public final String ALL_SATIC_METHODS = "all_static_methods";
	public final String ALL_STATIC_FIELDS = "all_static_fields";
	
	String[] engines() default {};
	String[] options() default {};
}

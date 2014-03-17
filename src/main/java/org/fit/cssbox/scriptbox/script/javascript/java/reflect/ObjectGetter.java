package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

public interface ObjectGetter {
	public static final Object UNDEFINED_VALUE = new String("undefined");
	public static final String METHOD_NAME = "get";
	public static final Class<?>[] METHOD_ARG_TYPES = {Object.class};
	
	public Object get(Object arg);
}

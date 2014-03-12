package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.lang.reflect.Method;

public interface Shutter {
	   public boolean isClassVisible(Class<?> type);
	   public boolean isFieldVisible(Object instance, String fieldName);
	   public boolean isMethodVisible(Object instance, Method method);
	   public boolean isStaticFieldVisible(Class<?> type, String fieldName);
	   public boolean isStaticMethodVisible(Class<?> type, Method method);
}

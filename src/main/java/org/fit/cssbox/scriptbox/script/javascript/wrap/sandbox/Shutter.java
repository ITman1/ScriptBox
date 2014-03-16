package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Shutter {
	   public boolean isClassVisible(Class<?> type);
	   public boolean isFieldVisible(Object instance, Field field);
	   public boolean isMethodVisible(Object instance, Method method);
	   public boolean isStaticFieldVisible(Class<?> type, Field fieldName);
	   public boolean isStaticMethodVisible(Class<?> type, Method method);
}

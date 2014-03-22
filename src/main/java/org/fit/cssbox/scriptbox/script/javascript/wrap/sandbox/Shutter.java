package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Shutter {
	   public boolean isClassVisible(Class<?> type);
	   public boolean isFieldVisible(Class<?> type, Field fieldName);
	   public boolean isMethodVisible(Class<?> type, Method method);
}

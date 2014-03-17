package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface MemberField extends MemberClass<Field> {
	public Method getFieldGetterMethod();
	public Method getFieldSetterMethod();
	public boolean hasGetOverride();
	public boolean hasSetOverride();
}

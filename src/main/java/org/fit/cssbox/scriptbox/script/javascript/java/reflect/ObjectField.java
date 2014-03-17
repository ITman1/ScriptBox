package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.mozilla.javascript.Scriptable;

public class ObjectField extends ObjectMember<ClassField, Field> implements MemberField {
	
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("getJS", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("setJS", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}
	
	public ObjectField(Object object, ClassField classField) {
		super(object, classField);
	}
	
	public ObjectField(Object object, Field member) {
		this(object, new ClassField(object.getClass(), member));
	}

	public Object get() {
		return classMember.get(object);
	}
	
	public void set(Object value) {
		classMember.set(object, value);
	}	
	
	public Object getJS(Scriptable obj) {
		return get();
	}
	
	public void setJS(Scriptable obj, Object value) {
		set(value);
	}

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public Method getFieldGetterMethod() {
		return classMember.getFieldGetterMethod();
	}

	@Override
	public Method getFieldSetterMethod() {
		return classMember.getFieldSetterMethod();
	}

	@Override
	public boolean hasGetOverride() {
		return classMember.hasGetOverride();
	}

	@Override
	public boolean hasSetOverride() {
		return classMember.hasSetOverride();
	}	
}

package org.fit.cssbox.scriptbox.script.javascript.object;

import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.mozilla.javascript.Scriptable;

public class ObjectField {
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("gettter", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = ObjectField.class.getMethod("setter", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}

	Object object;
	Method fieldGetterMethod;
	Method fieldSetterMethod;
	
	public ObjectField(Object object, Method fieldGetterMethod, Method fieldSetterMethod) {
		this.object = object;
		this.fieldGetterMethod = fieldGetterMethod;
		this.fieldSetterMethod = fieldSetterMethod;
	}
	
	public Object getObject() {
		return object;
	}

	public Method getFieldGetterMethod() {
		return fieldGetterMethod;
	}
	
	public Method getFieldSetterMethod() {
		return fieldSetterMethod;
	}
	
	public Object gettter(Scriptable obj) {		
		try {
			return fieldGetterMethod.invoke(object);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	public void setter(Scriptable obj, Object value) {
		try {
			value = ObjectScriptable.jsToJava(value);
			fieldSetterMethod.invoke(object, value);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}	
}

package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.java.ClassField;
import org.fit.cssbox.scriptbox.script.java.ObjectField;
import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaObject;
import org.mozilla.javascript.Scriptable;

public class ObjectJSField extends ObjectField {
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = ObjectJSField.class.getMethod("getJS", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = ObjectJSField.class.getMethod("setJS", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}
	
	public ObjectJSField(Object object, ClassField classField) {
		super(object, classField);
	}
	
	public ObjectJSField(Object object, Field member) {
		this(object, new ClassField(object.getClass(), member));
	}
	
	public Object getJS(Scriptable obj) {
		return HostedJavaObject.hostGet(classMember, object);
	}
	
	public void setJS(Scriptable obj, Object value) {
		HostedJavaObject.hostPut(classMember, object, value);
	}
}

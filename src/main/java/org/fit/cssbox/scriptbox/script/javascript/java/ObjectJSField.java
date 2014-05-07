package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ObjectField;
import org.mozilla.javascript.Scriptable;

/**
 * Class that serves as an interface for setting and getting 
 * the fields defined using via object implementor.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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
	
	/**
	 * Entry point for callback that returns values of the object. 
	 * 
	 * @param obj Scriptable object.
	 * @return Value retrieved from the wrapped field of the object.
	 */
	public Object getJS(Scriptable obj) {
		return HostedJavaObject.hostGet(classMember, object);
	}
	
	/**
	 * Entry point for callback that sets values of the object. 
	 * 
	 * @param obj Scriptable object.
	 * @param value Value to be set to the wrapped field of the object.
	 */
	public void setJS(Scriptable obj, Object value) {
		HostedJavaObject.hostPut(classMember, object, value);
	}
}

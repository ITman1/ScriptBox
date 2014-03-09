package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Method;

import javax.script.ScriptException;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ObjectFieldGetterSetterWrapper {
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = ObjectFieldGetterSetterWrapper.class.getMethod("gettter", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = ObjectFieldGetterSetterWrapper.class.getMethod("setter", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}
	
	Object object;
	Method fieldGetterMethod;
	Method fieldSetterMethod;
	
	public ObjectFieldGetterSetterWrapper(Object object, Method fieldGetterMethod, Method fieldSetterMethod) {
		this.object = object;
		this.fieldGetterMethod = fieldGetterMethod;
		this.fieldSetterMethod = fieldSetterMethod;
	}
	
	public Object gettter(Scriptable obj) throws ScriptException {		
		try {
			return fieldGetterMethod.invoke(object);
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public void setter(Scriptable obj, Object value) throws ScriptException {
		try {
			fieldSetterMethod.invoke(object, value);
		} catch (Exception e) {
			throw new ScriptException(e);
		}
	}
	
	public static ObjectFieldGetterSetterWrapper defineWrappedObjectFieldGetterSetter(ScriptableObject fieldScopeObject, String fieldName, Object object, Method fieldGetterMethod, Method fieldSetterMethod) {
		ObjectFieldGetterSetterWrapper delegateObject = new ObjectFieldGetterSetterWrapper(object, fieldGetterMethod, fieldSetterMethod);
		Method wrappedFieldGetterMethod = (fieldGetterMethod == null)? null : ObjectFieldGetterSetterWrapper.GETTER_METHOD;
		Method wrappedFieldSetterMethod = (fieldSetterMethod == null)? null : ObjectFieldGetterSetterWrapper.SETTER_METHOD;
		
		int attributes = ScriptableObject.DONTENUM;
		attributes = (fieldSetterMethod == null)? attributes | ScriptableObject.READONLY : attributes;
		
		fieldScopeObject.defineProperty(fieldName, delegateObject, wrappedFieldGetterMethod, wrappedFieldSetterMethod, attributes);
		
		return delegateObject;
	}
	
}

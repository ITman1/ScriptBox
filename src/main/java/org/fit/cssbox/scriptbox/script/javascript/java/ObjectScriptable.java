package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.js.OverloadableFunctionObject;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

public class ObjectScriptable extends ScriptableObject  {
	private static final long serialVersionUID = 1531587729453175461L;

	public ObjectScriptable() {
	}
	
	public ObjectScriptable(Scriptable scope, Scriptable prototype) {
		super(scope, prototype);
	}
	
	@Override
	public String getClassName() {
		return "ObjectScriptable";
	}

	public void defineObjectField(String fieldName, ObjectField objectField) {
		defineObjectField(this, fieldName, objectField);
	}
	
	public void defineObjectFunction(String functionName, ObjectFunction objectFunction) {		
		defineObjectFunction(this, functionName, objectFunction);
	}
	
	public static void defineObjectField(ScriptableObject fieldScopeObject, String fieldName, ObjectField objectField) {
		Method fieldGetterMethod = objectField.getFieldGetterMethod();
		Method fieldSetterMethod = objectField.getFieldSetterMethod();
		Field field = objectField.getField();
		Method wrappedFieldGetterMethod = (fieldGetterMethod == null && field == null)? null : ObjectField.GETTER_METHOD;
		Method wrappedFieldSetterMethod = (fieldSetterMethod == null && field == null)? null : ObjectField.SETTER_METHOD;
		
		int attributes = ScriptableObject.DONTENUM;
		attributes = (fieldSetterMethod == null)? attributes | ScriptableObject.READONLY : attributes;
		
		fieldScopeObject.defineProperty(fieldName, objectField, wrappedFieldGetterMethod, wrappedFieldSetterMethod, attributes);
	}
	
	public static void defineObjectFunction(ScriptableObject functionScopeObject, String functionName, ObjectFunction objectFunction) {		
		Object function = ScriptableObject.getProperty(functionScopeObject, functionName);
		
		if (function != Scriptable.NOT_FOUND && !(function instanceof OverloadableFunctionObject)) {
			throw new FunctionException("Function already exists and cannot be overloaded because function object does not extends OverloadableFunctionObject");
		}
		
		if (function == Scriptable.NOT_FOUND) {
			function = new OverloadableFunctionObject(objectFunction, functionName, ObjectFunction.FUNCTION_METHOD, functionScopeObject);
			functionScopeObject.defineProperty(functionName, function, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
		} else {
			((OverloadableFunctionObject)function).attachObjectFunction(objectFunction);
		}
	}
	
	public static Object jsToJava(Object jsObj) {
		if (jsObj instanceof Wrapper) {
			Wrapper njb = (Wrapper) jsObj;

			if (njb instanceof NativeJavaClass) {
				return njb;
			}

			Object obj = njb.unwrap();
			if (obj instanceof Number || obj instanceof String ||
				obj instanceof Boolean || obj instanceof Character) {
				return njb;
			} else {
				return obj;
			}
		} else {
			return jsObj;
		}
	}
}

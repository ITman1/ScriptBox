package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.reflect.Method;

import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.Wrapper;

public class ObjectScriptable extends ScriptableObject  {
	private static final long serialVersionUID = 1531587729453175461L;

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
		Method wrappedFieldGetterMethod = (fieldGetterMethod == null)? null : ObjectField.GETTER_METHOD;
		Method wrappedFieldSetterMethod = (fieldSetterMethod == null)? null : ObjectField.SETTER_METHOD;
		
		int attributes = ScriptableObject.DONTENUM;
		attributes = (fieldSetterMethod == null)? attributes | ScriptableObject.READONLY : attributes;
		
		fieldScopeObject.defineProperty(fieldName, objectField, wrappedFieldGetterMethod, wrappedFieldSetterMethod, attributes);
	}
	
	public static void defineObjectFunction(ScriptableObject functionScopeObject, String functionName, ObjectFunction objectFunction) {		
		FunctionObject f = new WrappedFunctionObject(objectFunction, functionName, ObjectFunction.FUNCTION_METHOD, functionScopeObject);
		functionScopeObject.defineProperty(functionName, f, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
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

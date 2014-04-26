/**
 * ObjectScriptable.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaMethod;
import org.fit.cssbox.scriptbox.script.reflect.ObjectField;
import org.fit.cssbox.scriptbox.script.reflect.ObjectFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ObjectScriptable extends ScriptableObject  {
	private static final long serialVersionUID = 1531587729453175461L;
	
	protected Object object;
	
	public ObjectScriptable(Object object) {
		this.object = object;
	}
	
	public ObjectScriptable(Object object, Scriptable scope, Scriptable prototype) {
		super(scope, prototype);
		
		this.object = object;
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
		Field field = objectField.getMember();
		Method wrappedFieldGetterMethod = (fieldGetterMethod == null && field == null)? null : ObjectJSField.GETTER_METHOD;
		Method wrappedFieldSetterMethod = (fieldSetterMethod == null && field == null)? null : ObjectJSField.SETTER_METHOD;
		
		int attributes = ScriptableObject.DONTENUM;
		attributes = (fieldSetterMethod == null && field == null)? attributes | ScriptableObject.READONLY : attributes;
		
		fieldScopeObject.defineProperty(fieldName, objectField, wrappedFieldGetterMethod, wrappedFieldSetterMethod, attributes);
	}
	
	public static void defineObjectFunction(ScriptableObject functionScopeObject, String functionName, ObjectFunction objectFunction) {		
		Object function = ScriptableObject.getProperty(functionScopeObject, functionName);
		
		if (function != Scriptable.NOT_FOUND && !(function instanceof HostedJavaMethod)) {
			deleteProperty(functionScopeObject, functionName);
			function = Scriptable.NOT_FOUND;
			//throw new FunctionException("Function already exists and cannot be overloaded because function object does not extends OverloadableFunctionObject");
		}
		
		if (function == Scriptable.NOT_FOUND) {
			Object object = objectFunction.getObject();
			function = new HostedJavaMethod(functionScopeObject, object, objectFunction);
			functionScopeObject.defineProperty(functionName, function, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
		} else {
			((HostedJavaMethod)function).attachObjectFunction(objectFunction);
		}
	}
	

	
	
}

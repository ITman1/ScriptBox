/**
 * HostedJavaField.java
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
public class HostedJavaField extends ObjectField {
	public final static Method GETTER_METHOD;
	public final static Method SETTER_METHOD;
	
	static {
		Method classMethod = null;
		try {
			classMethod = HostedJavaField.class.getMethod("getJS", Scriptable.class);
		} catch (Exception e) {
		}
		GETTER_METHOD = classMethod;
		
		classMethod = null;
		try {
			classMethod = HostedJavaField.class.getMethod("setJS", Scriptable.class, Object.class);
		} catch (Exception e) {
		}
		SETTER_METHOD = classMethod;
	}
	
	public HostedJavaField(Object object, ClassField classField) {
		super(object, classField);
	}
	
	public HostedJavaField(Object object, Field member) {
		this(object, new ClassField(object.getClass(), member));
	}
	
	public HostedJavaField(ObjectField objectField) {
		this(objectField.getObject(), objectField.getClassMember());
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

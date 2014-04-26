/**
 * ScriptAnnotationShutter.java
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

package org.fit.cssbox.scriptbox.deprecied;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.reflect.Shutter;

/** 
 * @deprecated
 */
public class ScriptAnnotationShutter implements Shutter {

	protected BrowserScriptEngine scriptEngine;
	
	public ScriptAnnotationShutter(BrowserScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
	}
	
	@Override
	public boolean isClassVisible(Class<?> type) {
		return true;
	}

	@Override
	public boolean isFieldVisible(Class<?> type, Field field) {
		/*if (fieldName.length() > 0) {
			Class<?> instanceType = instance.getClass();		
			char firstCharacter = fieldName.charAt(0);
			String getterMethodName = "get" + Character.toUpperCase(firstCharacter) + fieldName.substring(1);
			
			Method method = null;
			
			try {
				method = instanceType.getMethod(getterMethodName);
			} catch (Exception e) {
				return false;
			}

			return ScriptAnnotation.testForScriptGetter(instanceType, method, scriptEngine);
		}*/

		return false;
	}

	@Override
	public boolean isMethodVisible(Class<?> type, Method method) {
		/*Class<?> instanceType = instance.getClass();
		return ScriptAnnotation.testForScriptFunction(instanceType, method, scriptEngine);*/
		return false;
	}

	@Override
	public boolean isConstructorVisible(Class<?> type,
			Constructor<?> constructor) {
		// TODO Auto-generated method stub
		return false;
	}

}

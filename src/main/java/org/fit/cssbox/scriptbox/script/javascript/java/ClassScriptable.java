/**
 * ClassScriptable.java
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

import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotation;
import org.mozilla.javascript.Scriptable;

/**
 * Class that wraps class object into Scriptable interface.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ClassScriptable extends ObjectScriptable {

	private static final long serialVersionUID = 8548972021876328006L;

	public ClassScriptable(Class<?> clazz) {
		super(clazz);
	}
	
	@Override
	public String toString() {
		String name = ScriptAnnotation.extractClassName((Class<?>)object);
		return "[object " + name + "]";
	}
	
	@Override
	public boolean hasInstance(Scriptable instance) {
		boolean result = false;
		
		if (instance instanceof ObjectScriptable) {
			ObjectScriptable objectScriptable = (ObjectScriptable)instance;
			Class<?> instanceClass = objectScriptable.getObjectClass();
			
			result = ((Class<?>)object).isAssignableFrom(instanceClass);
		}
		
		return result || super.hasInstance(instance);
	}
}

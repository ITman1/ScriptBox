/**
 * Shutter.java
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

package org.fit.cssbox.scriptbox.script.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Interface for optional determining if is given class, class member visible, or not.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface Shutter {
	
	/**
	 * Tests whether is the given class visible.
	 * 
	 * @param type Class which should be tested.
	 * @return True if is class visible, otherwise false.
	 */
	public boolean isClassVisible(Class<?> type);
	
	/**
	 * Tests whether is the given field visible.
	 * 
	 * @param type Field which should be tested.
	 * @return True if is field visible, otherwise false.
	 */
	public boolean isFieldVisible(Class<?> type, Field fieldName);
	
	/**
	 * Tests whether is the given constructor visible.
	 * 
	 * @param type Constructor which should be tested.
	 * @return True if is constructor visible, otherwise false.
	 */
	public boolean isConstructorVisible(Class<?> type, Constructor<?> constructor);
	
	/**
	 * Tests whether is the given method visible.
	 * 
	 * @param type Method which should be tested.
	 * @return True if is method visible, otherwise false.
	 */
	public boolean isMethodVisible(Class<?> type, Method method);
}

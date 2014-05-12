/**
 * ObjectGetter.java
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

import java.util.Collection;

/**
 * Interface for object getters. This is used for objects that acts 
 * as associative arrays and enables returning the objects for given key
 * - e.g. Document has getter for getting the nested proxied windows.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface ObjectGetter {
	public static final Object UNDEFINED_VALUE = new String("undefined");
	public static final String METHOD_NAME = "get";
	public static final Class<?>[] METHOD_ARG_TYPES = {Object.class};
	
	/**
	 * Returns value for given key.
	 * 
	 * @param arg Key for which should be returned the value.
	 * @return Value for given key.
	 */
	public Object get(Object arg);
	
	/**
	 * Returns all explicitly known keys.
	 * 
	 * @return All explicitly known keys.
	 */
	public Collection<Object> getKeys();
}

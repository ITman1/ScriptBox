/**
 * AdapterRegistry.java
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

package org.fit.cssbox.scriptbox.script.adapter;

import java.util.HashMap;
import java.util.Map;

/**
 * The registry that collects registered adapters and enables to search them.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see Adapter
 */
final public class AdapterRegistry {
	private Map<Class<?>, Class<?> > adapterMap = new HashMap<Class<?>, Class<?> >();

	/**
	 * Registers new adapter.
	 * 
	 * @param adapter Adapter to be registered inside this registry.
	 * @return True if adapter is successfully registered.
	 */
	public boolean registerAdapter(Class<?> adapter) {
		try {
			Object objAdapter = adapter.newInstance();
			
			if (objAdapter instanceof Adapter) {
				Adapter adapterInstance = (Adapter)objAdapter;
				
				// Create the search key
				Class<?> fromToClass = adapterInstance.getAdapteeClass();
				
				// Insert only when the search key is not already present in the registry
				if (!adapterMap.containsKey(fromToClass)) {
					adapterMap.put(fromToClass, adapter);
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Returns adapter for a given adaptee class or its ancestor base classes.
	 * 
	 * @param adapteeClass Start class that should be adapted.
	 * @return If searching was successful then returns corresponding adapter, otherwise null.
	 */
	public Adapter getAdapter(Class<?> adapteeClass) {
		Adapter adapter = null;
		
		while (adapteeClass != null && (adapter = getSpecificAdapter(adapteeClass)) == null) {
			adapteeClass = adapteeClass.getSuperclass();
			
			break; // FIXME?: This solves that all classes which did not needed the adapter, 
			// were adapted instead. eg. TrustedEvent, PopStateEvent, HashChangeEvent, so this is why is here break...
		}
		
		return adapter;
	}
	
	/**
	 * Returns adapter for a given adaptee class only and without nesting to its ancestor base classes.
	 * 
	 * @param adapteeClass Class that should be adapted.
	 * @return If searching was successful then returns corresponding adapter, otherwise null.
	 */
	public Adapter getSpecificAdapter(Class<?> adapteeClass) {
		try {
			Class<?> adapterClass = adapterMap.get(adapteeClass);
			return (adapterClass != null)? (Adapter)adapterClass.newInstance() : null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
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

package org.fit.cssbox.scriptbox.script.javascript.wrap.adapter;

import java.util.HashMap;
import java.util.Map;

final public class AdapterRegistry {
	private Map<Class<?>, Class<?> > adapterMap = new HashMap<Class<?>, Class<?> >();

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

	public Adapter getAdapter(Class<?> adapteeClass) {
		Adapter adapter = null;
		
		while (adapteeClass != null && (adapter = getSpecificAdapter(adapteeClass)) == null) {
			adapteeClass = adapteeClass.getSuperclass();
			
			break; // FIXME?: This solves that all classes which did not needed the adapter, 
			// were adapted instead. eg. TrustedEvent, PopStateEvent, HashChangeEvent, so this is why is here break...
		}
		
		return adapter;
	}
	
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

// End of the file

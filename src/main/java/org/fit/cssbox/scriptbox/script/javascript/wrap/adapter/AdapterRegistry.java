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
		
		while ((adapter = getSpecificAdapter(adapteeClass)) == null) {
			adapteeClass = adapteeClass.getSuperclass();
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

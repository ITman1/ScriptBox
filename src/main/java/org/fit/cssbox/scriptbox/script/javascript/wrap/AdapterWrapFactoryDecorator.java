package org.fit.cssbox.scriptbox.script.javascript.wrap;

import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.Adapter;
import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.AdapterRegistry;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class AdapterWrapFactoryDecorator extends WrapFactoryDecorator {

	protected AdapterRegistry adapterRegistry;
				
	public AdapterWrapFactoryDecorator(AdapterRegistry adapterRegistry, WrapFactoryDecorator decorator) {
		super(decorator);
			
		this.adapterRegistry = adapterRegistry;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		Class<?> adapteeClass = javaObject.getClass();
		Adapter adapter = adapterRegistry.getAdapter(adapteeClass);
		
		if (adapter != null) {
			javaObject = adapter.getProvider(javaObject);
		}
			
		return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
	}

}

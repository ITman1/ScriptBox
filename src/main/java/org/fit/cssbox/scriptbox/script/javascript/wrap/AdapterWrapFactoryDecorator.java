/**
 * AdapterWrapFactoryDecorator.java
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

package org.fit.cssbox.scriptbox.script.javascript.wrap;

import org.fit.cssbox.scriptbox.script.adapter.Adapter;
import org.fit.cssbox.scriptbox.script.adapter.AdapterRegistry;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * Wrap factory decorator that adapts the Java objects if there 
 * exists related adapter for them inside adapter registry.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class AdapterWrapFactoryDecorator extends WrapFactoryDecorator {

	protected AdapterRegistry adapterRegistry;
		
	/**
	 * Constructs leaf adapter wrap factory decorator. 
	 * 
	 * @param adapterRegistry Adapter registry that is used for lookup of the adapters.
	 */
	public AdapterWrapFactoryDecorator(AdapterRegistry adapterRegistry) {
		this(adapterRegistry, null);
	}
	
	/**
	 * Constructs new wrap factory decorator.
	 * 
	 * @param adapterRegistry Adapter registry that is used for lookup of the adapters.
	 * @param decorator Decorator the be added as a child decorator and chained.
	 */
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

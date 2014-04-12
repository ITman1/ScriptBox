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

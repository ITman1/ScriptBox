/**
 * XMLHttpRequestInjector.java
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

package org.fit.cssbox.scriptbox.script.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.ScriptContextInjector;

/**
 * Implementation of the injector which adds the XMLHttpRequest object into the script context.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class XMLHttpRequestInjector extends ScriptContextInjector {

	/*
	 * TODO: Implement XMLHttpRequest
	 */
	public static class XMLHttpRequest {
		
	}
	
	public XMLHttpRequestInjector() {
		super(ALL_SCRIPT_ENGINE_FACTORIES);
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		XMLHttpRequest xmlHttpRequest = new XMLHttpRequest();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("XMLHttpRequest", xmlHttpRequest);
		
		return true;
	}

}

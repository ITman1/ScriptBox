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

package org.fit.cssbox.scriptbox.script.javascript.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.javascript.JavaScriptInjector;

public class XMLHttpRequestInjector extends JavaScriptInjector {

	/*
	 * TODO: Implement XMLHttpRequest
	 */
	public static class XMLHttpRequest {
		
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		XMLHttpRequest xmlHttpRequest = new XMLHttpRequest();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("XMLHttpRequest", xmlHttpRequest);
		
		return true;
	}

}

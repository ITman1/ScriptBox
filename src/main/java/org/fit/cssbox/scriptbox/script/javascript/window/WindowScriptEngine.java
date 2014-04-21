/**
 * WindowScriptEngine.java
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

package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.window.Window;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;


public class WindowScriptEngine extends GlobalObjectJavaScriptEngine {
	private Window window;
	
	public WindowScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
		
		window = scriptSettings.getGlobalObject();
	}
	
	public Window getWindow() {
		return window;
	}
}

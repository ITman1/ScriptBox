/**
 * WindowScriptEngineFactory.java
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

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngineFactory;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;

public class WindowScriptEngineFactory extends GlobalObjectJavaScriptEngineFactory {
	@Override
	protected BrowserScriptEngine getBrowserScriptEngineProtected(ScriptSettings<?> scriptSettings) {
		if (scriptSettings instanceof WindowScriptSettings) {
			BrowserScriptEngine engine = new WindowScriptEngine(this, (WindowScriptSettings)scriptSettings);
			return engine;
		}
		return null;
	}
}

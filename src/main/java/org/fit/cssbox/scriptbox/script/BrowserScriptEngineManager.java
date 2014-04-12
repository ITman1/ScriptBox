/**
 * BrowserScriptEngineManager.java
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

package org.fit.cssbox.scriptbox.script;

import org.fit.cssbox.scriptbox.misc.MimeContentRegistryBase;
import org.fit.cssbox.scriptbox.script.javascript.window.WindowScriptEngineFactory;

public class BrowserScriptEngineManager extends MimeContentRegistryBase<BrowserScriptEngineFactory, BrowserScriptEngine> {
	static private BrowserScriptEngineManager instance;
	
	private BrowserScriptEngineManager() {
		registerMimeContentFactory(WindowScriptEngineFactory.class);
	}
	
	public static synchronized BrowserScriptEngineManager getInstance() {
		if (instance == null) {
			instance = new BrowserScriptEngineManager();
		}
		
		return instance;
	}
	
	public synchronized BrowserScriptEngine getBrowserScriptEngine(String mimeType, ScriptSettings<?> settings) {
		return getContent(mimeType, settings);
	}
}

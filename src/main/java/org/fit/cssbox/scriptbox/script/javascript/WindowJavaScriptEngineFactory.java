/**
 * WindowJavaScriptEngineFactory.java
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

package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.injectors.ClassObjectsInjector;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;

/**
 * JavaScript engine factory for creating the {@link WindowJavaScriptEngine} instances.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class WindowJavaScriptEngineFactory extends BrowserScriptEngineFactory {

	private static List<String> mimeTypes;
	
	static {	   
		/* http://tools.ietf.org/html/rfc4329#page-9 */
		mimeTypes = new ArrayList<String>(4);
		mimeTypes.add("application/javascript");
		mimeTypes.add("application/ecmascript");
		mimeTypes.add("text/javascript");
		mimeTypes.add("text/ecmascript");
		mimeTypes = Collections.unmodifiableList(mimeTypes);
	}
	
	private static final String ENGINE_SHORTNAME = "javascript";
	private static final String ENGINE_NAME = "Window Rhino Engine";
	private static final String ENGINE_VERSION = "0.9";
	private static final String LANGUAGE_NAME = "ECMAScript";
	private static final String LANGUAGE_VERSION = "1.8";
		
	public WindowJavaScriptEngineFactory() {
		registerScriptContextsInject(new ClassObjectsInjector());
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}

	@Override
	protected BrowserScriptEngine getBrowserScriptEngineProtected(ScriptSettings<?> scriptSettings) {
		if (scriptSettings instanceof WindowScriptSettings) {
			BrowserScriptEngine engine = new WindowJavaScriptEngine(this, (WindowScriptSettings)scriptSettings);
			return engine;
		}
		return null;
	}

	@Override
	public String getEngineName() {
		return ENGINE_NAME;
	}

	@Override
	public String getEngineVersion() {
		return ENGINE_VERSION;
	}

	@Override
	public String getLanguageName() {
		return LANGUAGE_NAME;
	}

	@Override
	public String getLanguageVersion() {
		return LANGUAGE_VERSION;
	}

	@Override
	public String getEngineShortName() {
		return ENGINE_SHORTNAME;
	}
}

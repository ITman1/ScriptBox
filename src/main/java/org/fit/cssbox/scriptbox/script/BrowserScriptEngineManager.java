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
import org.fit.cssbox.scriptbox.script.injectors.URLInjector;
import org.fit.cssbox.scriptbox.script.injectors.XMLHttpRequestInjector;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngineFactory;

public class BrowserScriptEngineManager extends MimeContentRegistryBase<BrowserScriptEngineFactory, BrowserScriptEngine> {
	static private BrowserScriptEngineManager instance = null;
	static private boolean initialized = false;
	
	public static synchronized BrowserScriptEngineManager getInstance() {
		if (instance == null) {
			instance = new BrowserScriptEngineManager();
			
			instance.registerScriptEngineFactories();
		}
		
		return instance;
	}
	
	/**
	 * Constructs new script engine for given MIME type with given script settings.
	 * 
	 * @param mimeType MIME type of which script engine should be returned.
	 * @param settings Settings that should be passed into constructed script engine.
	 * @return New script engine factory if there is any for given MIME type, otherwse null.
	 */
	public synchronized BrowserScriptEngine getBrowserScriptEngine(String mimeType, ScriptSettings<?> settings) {
		return getContent(mimeType, settings);
	}
	
	@Override
	public synchronized BrowserScriptEngine getContent(String mimeType, Object... args) {
		/*
		 * We are accessing the first script engine, so this manager should be already fully instatized.
		 * We can lazily register injectors now.
		 */
		if (!initialized) {
			registerScriptContextInjectors();
		}
		
		return super.getContent(mimeType, args);
	}
	
	private void registerScriptEngineFactories() {
		registerMimeContentFactory(WindowJavaScriptEngineFactory.class);
	}
	
	private void registerScriptContextInjectors() {
		registerScriptContextInjector(new URLInjector());
		registerScriptContextInjector(new XMLHttpRequestInjector());
	}
	
	private void registerScriptContextInjector(ScriptContextInjector injector) {
		injector.registerScriptContextInject();
	}
}

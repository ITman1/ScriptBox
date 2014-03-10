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

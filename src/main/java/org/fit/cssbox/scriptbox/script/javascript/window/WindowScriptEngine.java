package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;


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

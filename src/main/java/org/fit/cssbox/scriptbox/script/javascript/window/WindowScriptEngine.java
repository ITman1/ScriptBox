package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.ScriptAnnotationImplementor;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectTopLevel;
import org.mozilla.javascript.TopLevel;


public class WindowScriptEngine extends JavaScriptEngine {
	private Window window;
	
	public WindowScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
		
		window = scriptSettings.getGlobalObject();
	}
	
	public Window getWindow() {
		return window;
	}
	
	@Override
	protected TopLevel initializeTopLevel() {
		window = ((WindowScriptSettings)scriptSettings).getGlobalObject();
		return new ObjectTopLevel(window, this, new ScriptAnnotationImplementor(window, WindowScriptEngine.this));
	}
}

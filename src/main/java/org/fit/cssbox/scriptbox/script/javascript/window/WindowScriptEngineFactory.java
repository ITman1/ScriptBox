package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngineFactory;

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

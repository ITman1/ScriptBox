package org.fit.cssbox.scriptbox.script.javascript;

import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectTopLevel;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

public class GlobalObjectJavaScriptEngine extends JavaScriptEngine {

	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings, contextFactory);
	}
	
	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
	}

	@Override
	protected TopLevel initializeTopLevel() {
		Object object = ((WindowScriptSettings)scriptSettings).getGlobalObject();
		return new ObjectTopLevel(object, this, new ScriptAnnotationImplementor(object, GlobalObjectJavaScriptEngine.this));
	}
}
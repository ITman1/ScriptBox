package org.fit.cssbox.scriptbox.script;

import javax.script.AbstractScriptEngine;
import javax.script.ScriptEngineFactory;

public abstract class BrowserScriptEngine extends AbstractScriptEngine {
	protected ScriptSettings<?> scriptSettings;
	protected BrowserScriptEngineFactory factory;
	
	protected BrowserScriptEngine(BrowserScriptEngineFactory factory, ScriptSettings<?> scriptSettings) {
		this.scriptSettings = scriptSettings;
		this.factory = factory;
	}
	
	public ScriptSettings<?> getScriptSettings() {
		return scriptSettings;
	}
	
	@Override
    public ScriptEngineFactory getFactory() {
		return factory;
	}
}

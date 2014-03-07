package org.fit.cssbox.scriptbox.script.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptSettings;

public class SecureRhinoScriptEngine extends BrowserScriptEngine {

	public SecureRhinoScriptEngine(ScriptSettings scriptSettings) {
		super(scriptSettings);
	}

	@Override
	protected ScriptEngine getEngine() {
		ScriptEngineManager factory = new ScriptEngineManager();
        return factory.getEngineByName("JavaScript");
	}

}

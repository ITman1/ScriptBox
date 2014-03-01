package org.fit.cssbox.scriptbox.script.javascript;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngine;

public class SecureRhinoScriptEngine extends DocumentScriptEngine {

	protected SecureRhinoScriptEngine(Html5DocumentImpl document) {
		super(document);
	}

	@Override
	protected ScriptEngine getEngine() {
		// TODO: Connect to our script engine
		return new ScriptEngineManager().getEngineByName("js");
	}

}

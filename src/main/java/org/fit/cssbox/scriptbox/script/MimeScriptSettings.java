package org.fit.cssbox.scriptbox.script;

import java.util.Collection;

import javax.script.ScriptEngine;

public abstract class MimeScriptSettings extends ScriptSettings {
	@Override
	public Collection<BrowserScriptEngineFactory> getExecutionEnviroments() {
		BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
		
		return scriptManager.getAllMimeContentFactories();
	}
	
	@Override
	public ScriptEngine getExecutionEnviroment(Script script) {
		if (script instanceof MimeScript) {
			MimeScript windowScript = (MimeScript)script;
			String mimeType = windowScript.getMimeType();
			BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
			
			return scriptManager.getContent(mimeType, this);
		} else {
			return null;
		}
	}
}

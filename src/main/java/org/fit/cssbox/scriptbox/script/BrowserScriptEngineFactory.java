package org.fit.cssbox.scriptbox.script;

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;

public abstract class BrowserScriptEngineFactory extends MimeContentFactoryBase<BrowserScriptEngine> {
	public abstract BrowserScriptEngine getBrowserScriptEngine(ScriptSettings scriptSettings);
	
	@Override
	public BrowserScriptEngine getContent(Object... args) {
		if (args.length == 1 && args[0] instanceof ScriptSettings) {
			return getBrowserScriptEngine((ScriptSettings)args[0]);
			}

			return null;
	}
}

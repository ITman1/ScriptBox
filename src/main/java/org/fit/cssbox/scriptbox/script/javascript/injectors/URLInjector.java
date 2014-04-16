package org.fit.cssbox.scriptbox.script.javascript.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.javascript.JavaScriptInjector;
import org.fit.cssbox.scriptbox.url.URL;

public class URLInjector extends JavaScriptInjector {
	@Override
	public boolean inject(ScriptContext context) {
		URL url = new URL();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("URL", url);
		
		return true;
	}
}

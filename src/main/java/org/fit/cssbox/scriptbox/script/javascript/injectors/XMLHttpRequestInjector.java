package org.fit.cssbox.scriptbox.script.javascript.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.javascript.JavaScriptInjector;

public class XMLHttpRequestInjector extends JavaScriptInjector {

	public static class XMLHttpRequest {
		
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		XMLHttpRequest xmlHttpRequest = new XMLHttpRequest();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("XMLHttpRequest", xmlHttpRequest);
		
		return true;
	}

}

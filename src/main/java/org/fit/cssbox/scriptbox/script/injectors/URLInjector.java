package org.fit.cssbox.scriptbox.script.injectors;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.ScriptContextInjector;
import org.fit.cssbox.scriptbox.url.URL;

/**
 * Implementation of the injector which adds the URL object into the script context.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class URLInjector extends ScriptContextInjector {
	public URLInjector() {
		super(ALL_SCRIPT_ENGINE_FACTORIES);
	}
	
	@Override
	public boolean inject(ScriptContext context) {
		URL url = new URL();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("URL", url);
		
		return true;
	}
}

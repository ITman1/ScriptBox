package org.fit.cssbox.scriptbox.script.javascript;

import org.fit.cssbox.scriptbox.script.ScriptContextInjector;

public abstract class JavaScriptInjector extends ScriptContextInjector {

	public final static String JAVASCRIPT_ENGINE_NAME = "text/javascript";
	
	public JavaScriptInjector() {
		super(JAVASCRIPT_ENGINE_NAME);
	}

}

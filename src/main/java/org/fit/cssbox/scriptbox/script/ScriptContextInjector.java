package org.fit.cssbox.scriptbox.script;

import java.util.Set;

public abstract class ScriptContextInjector extends ScriptContextInject {
	protected final static BrowserScriptEngineManager manager = BrowserScriptEngineManager.getInstance();
	
	protected String scriptEngineName;
	protected boolean isRegistered;
	
	public ScriptContextInjector(String scriptEngineName) {
		this.scriptEngineName = scriptEngineName;
	}
	
	public String getScriptEngineName() {
		return scriptEngineName;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	
	public void registerScriptContextInject() {
		Set<BrowserScriptEngineFactory> factories = manager.getMimeContentFactories(scriptEngineName); 
		
		for (BrowserScriptEngineFactory factory : factories) {
			factory.registerScriptContextsInject(this);
		}
		
		isRegistered = true;
	}
	
	public void unregisterScriptContextInject() {
		Set<BrowserScriptEngineFactory> factories = manager.getMimeContentFactories(scriptEngineName); 
		
		for (BrowserScriptEngineFactory factory : factories) {
			factory.unregisterScriptContextsInject(this);
		}
		
		isRegistered = false;
	}
}

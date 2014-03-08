package org.fit.cssbox.scriptbox.script;

import java.net.URL;
import java.util.Collection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.security.origins.Origin;

public abstract class ScriptSettings<GlobalObject> {
	public Collection<BrowserScriptEngineFactory> getExecutionEnviroments() {
		BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
		
		return scriptManager.getAllMimeContentFactories();
	}
	
	public BrowserScriptEngine getExecutionEnviroment(Script<?, ?> script) {
		String language = script.getLanguage();
		BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
			
		return scriptManager.getContent(language, this);
	}
	
	public abstract GlobalObject getGlobalObject();
	public abstract BrowsingContext getResposibleBrowsingContext();
	public abstract Html5DocumentImpl getResponsibleDocument();
	public abstract EventLoop getResposibleEventLoop();
	public abstract Object getReferrerSource();
	public abstract String getUrlCharacterEncoding();
	public abstract URL getBaseUrl();
	public abstract Origin<?> getOrigin();
	public abstract Origin<?> getEffectiveScriptOrigin();
}

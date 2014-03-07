package org.fit.cssbox.scriptbox.script;

import java.net.URL;
import java.util.Collection;

import javax.script.ScriptEngine;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.security.origins.Origin;

public abstract class ScriptSettings {
	public abstract Collection<BrowserScriptEngineFactory> getExecutionEnviroments();
	public abstract ScriptEngine getExecutionEnviroment(Script script);
	public abstract Object getGlobalObject();
	public abstract BrowsingContext getResposibleBrowsingContext();
	public abstract Html5DocumentImpl getResponsibleDocument();
	public abstract EventLoop getResposibleEventLoop();
	public abstract Object getReferrerSource();
	public abstract String getUrlCharacterEncoding();
	public abstract URL getBaseUrl();
	public abstract Origin<?> getOrigin();
	public abstract Origin<?> getEffectiveScriptOrigin();
}

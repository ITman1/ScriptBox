package org.fit.cssbox.scriptbox.browser;

import java.net.URL;
import java.util.Map;

import javax.script.ScriptEngine;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.script.MimeScriptSettings;
import org.fit.cssbox.scriptbox.security.origins.Origin;

public class WindowScriptSettings extends MimeScriptSettings {

	private Window _window;
	protected Map<Class<? extends ScriptEngine>, ScriptEngine> scriptEngines;
	public void addDocumentScriptEngine(ScriptEngine scriptEngine) {
		scriptEngines.put(scriptEngine.getClass(), scriptEngine);
	}
	
	public void getDocumentScriptEngine(Class<? extends ScriptEngine> engineClass) {
		scriptEngines.get(engineClass);
	}
	public WindowScriptSettings(Window window) {
		_window = window;
	}
	
	public Window getWindow() {
		return _window;
	}

	@Override
	public Object getGlobalObject() {
		return _window;
	}

	@Override
	public BrowsingContext getResposibleBrowsingContext() {
		return _window.getDocumentImpl().getBrowsingContext();
	}

	@Override
	public Html5DocumentImpl getResponsibleDocument() {
		return _window.getDocumentImpl();
	}

	@Override
	public EventLoop getResposibleEventLoop() {
		return getResposibleBrowsingContext().getEventLoop();
	}

	@Override
	public Object getReferrerSource() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getUrlCharacterEncoding() {
		return null;
	}

	@Override
	public URL getBaseUrl() {
		return getResposibleBrowsingContext().getBaseURL();
	}

	@Override
	public Origin<?> getOrigin() {
		return getResponsibleDocument().getOriginContainer().getOrigin();
	}

	@Override
	public Origin<?> getEffectiveScriptOrigin() {
		return getResponsibleDocument().getOriginContainer().getEffectiveScriptOrigin();
	}
}

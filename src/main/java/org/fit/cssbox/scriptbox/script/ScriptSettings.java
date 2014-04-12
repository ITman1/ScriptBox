/**
 * ScriptSettings.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script;

import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.security.origins.Origin;

public abstract class ScriptSettings<GlobalObject> {
	protected Map<String, BrowserScriptEngine> scriptEngines;
	
	public ScriptSettings() {
		scriptEngines = new HashMap<String, BrowserScriptEngine>();
	}
	
	public Collection<BrowserScriptEngineFactory> getExecutionEnviroments() {
		BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
		
		return scriptManager.getAllMimeContentFactories();
	}
	
	public BrowserScriptEngine getExecutionEnviroment(Script<?, ?> script) {
		String language = script.getLanguage();
		
		return getExecutionEnviroment(language);
	}
	
	public BrowserScriptEngine getExecutionEnviroment(String language) {		
		BrowserScriptEngine scriptEngine = scriptEngines.get(language);
		
		if (scriptEngine == null) {
			BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
			scriptEngine = scriptManager.getContent(language, this);
			scriptEngines.put(language, scriptEngine);
		}

		return scriptEngine;
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

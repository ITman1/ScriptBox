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

/**
 * Represents class for creating settings which is passed into scripts and script engines.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#script-settings-object">Script settings object</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ScriptSettings<GlobalObject> {
	protected Map<String, BrowserScriptEngine> scriptEngines;
	
	public ScriptSettings() {
		scriptEngines = new HashMap<String, BrowserScriptEngine>();
	}
	
	/**
	 * Returns all supported execution environments.
	 * 
	 * @return All supported execution environments.
	 */
	public Collection<BrowserScriptEngineFactory> getExecutionEnviroments() {
		BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
		
		return scriptManager.getAllMimeContentFactories();
	}
	
	/**
	 * Returns supported execution environment for the passed script.
	 * 
	 * @param script Script
	 * @return Execution environment for the passed script
	 */
	public BrowserScriptEngine getExecutionEnviroment(Script<?, ?, ?> script) {
		String language = script.getLanguage();
		
		return getExecutionEnviroment(language);
	}
	
	/**
	 * Returns supported execution environment for given language.
	 * 
	 * @param language Script language
	 * @return Execution environment for given language
	 */
	public BrowserScriptEngine getExecutionEnviroment(String language) {		
		BrowserScriptEngine scriptEngine = scriptEngines.get(language);
		
		if (scriptEngine == null) {
			BrowserScriptEngineManager scriptManager = BrowserScriptEngineManager.getInstance();
			scriptEngine = scriptManager.getContent(language, this);
			scriptEngines.put(language, scriptEngine);
		}

		return scriptEngine;
	}
	
	/**
	 * Returns an object that provides the APIs that can be called by the code in scripts.
	 * 
	 * @return An object that provides the APIs that can be called by the code in scripts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#global-object">Global object</a>
	 */
	public abstract GlobalObject getGlobalObject();
	
	/**
	 * Returns a browsing context that is assigned responsibility for actions taken by the scripts.
	 * 
	 * @return A browsing context that is assigned responsibility for actions taken by the scripts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#responsible-browsing-context">A responsible browsing context</a>
	 */
	public abstract BrowsingContext getResposibleBrowsingContext();
	
	/**
	 * Returns a Document that is assigned responsibility for actions taken by the scripts.
	 * 
	 * @return A Document that is assigned responsibility for actions taken by the scripts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#responsible-document">A responsible document</a>
	 */
	public abstract Html5DocumentImpl getResponsibleDocument();
	
	/**
	 * Returns an event loop that is used when it would not be immediately clear what event loop to use.
	 * 
	 * @return An event loop that is used when it would not be immediately clear what event loop to use.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#responsible-event-loop">A responsible event loop</a>
	 */
	public abstract EventLoop getResposibleEventLoop();
	
	/**
	 * Returns Either a Document (specifically, the responsible document), or a URL, which is used by some APIs to determine 
	 * what value to use for the Referer (sic) header in calls to the fetching algorithm.
	 * 
	 * @return Either a Document (specifically, the responsible document), or a URL, which is used by some APIs to determine 
	 * what value to use for the Referer (sic) header in calls to the fetching algorithm.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#api-referrer-source">An API referrer source</a>
	 */
	public abstract Object getReferrerSource();
	
	/**
	 * Returns a character encoding used to encode URLs by APIs called by scripts.
	 * 
	 * @return A character encoding used to encode URLs by APIs called by scripts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#api-url-character-encoding">An API URL character encoding</a>
	 */
	public abstract String getUrlCharacterEncoding();
	
	/**
	 * Returns an absolute URL used by APIs called by scripts.
	 * 
	 * @return An absolute URL used by APIs called by scripts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#api-base-url">An API base URL</a>
	 */
	public abstract URL getBaseUrl();
	
	/**
	 * Returns an instrument used in security checks.
	 * 
	 * @return An instrument used in security checks.
	 */
	public abstract Origin<?> getOrigin();
	
	/**
	 * Returns an instrument used in security checks.
	 * 
	 * @return An instrument used in security checks.
	 */
	public abstract Origin<?> getEffectiveScriptOrigin();
}

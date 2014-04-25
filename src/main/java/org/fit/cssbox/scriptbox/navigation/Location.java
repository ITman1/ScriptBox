/**
 * MimeContentRegistryBase.java
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

package org.fit.cssbox.scriptbox.navigation;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.url.ParserURL;
import org.fit.cssbox.scriptbox.url.URLSearchParams;
import org.fit.cssbox.scriptbox.url.URLUtils;
import org.fit.cssbox.scriptbox.url.WrappedURL;
import org.fit.cssbox.scriptbox.window.Window;

/**
 * Class implementing Location interface which allows navigating of the new
 * documents/resources inside browsing contexts.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#location">Location interface</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class Location extends URLUtils {
	private Html5DocumentImpl document;
	private NavigationController controller;
	private BrowsingContext context;
	
	/**
	 * Constructs location for given document.
	 * 
	 * @param document Document that owns this location object.
	 */
	public Location(Html5DocumentImpl document) {
		this.document = document;
		this.context = document.getBrowsingContext();
		this.controller = context.getNavigationController();
		
		onAddressChanged();
	}
	
	/**
	 * Navigates to the given page without replacement enabled if there is not reason.
	 * 
	 * @param url New URL where to navigate current browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-location-assign">Location assign()</a>
	 */
	@ScriptFunction
	public void assign(String url) {
		securityTest();
		
		boolean replacementEnabled = false;
		
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		
		if (currentDocument == null) {
			return;
		}
		
		if (sessionHistory.getLength() == 1 && currentDocument != null && currentDocument.hasDefaultAddress() && currentEntry.isDefaultEntry()) {
			replacementEnabled = true;
		}
		
		/*
		 * FIXME?: Every browser replaces history if page redirects during loading, but specification tells nothing about that.
		 */
		if (currentDocument.getDocumentReadiness() == DocumentReadiness.LOADING) {
			replacementEnabled = true;
		}
		
		navigate(url, replacementEnabled);
	}
	
	/**
	 * Removes the current page from the session history and navigates to the given page.
	 * 
	 * @param url New URL where to navigate current browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-location-replace">Location replace()</a>
	 */
	@ScriptFunction
	public void replace(String url) {
		if (!isFamiliarResponsibleBrowsingContext()) {
			securityTest();
		}
		
		navigate(url, true);
	}
	
	/**
	 * Reloads the current page.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-location-reload">Location reload()</a>
	 */
	@ScriptFunction
	public void reload() {
		securityTest();
		
		// TODO: If the currently executing task is the dispatch of a resize event in response to the user resizing the browsing context
		// TODO: If the browsing context's active document is an iframe srcdoc document
		// TODO: If the browsing context's active document has its reload override flag set
	
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (activeDocument != null) {
			URL address = activeDocument.getAddress();
			
			navigate(context, address, true);
		}
	}
	
	@ScriptGetter
	@Override
	public String getHref() {
		if (!isFamiliarResponsibleBrowsingContext()) {
			securityTest();
		}

		return super.getHref();
	}
	
	@ScriptGetter
	@Override
	public String getHash() {
		securityTest();
		return super.getHash();
	}
	
	@ScriptGetter
	@Override
	public String getHost() {
		securityTest();
		return super.getHost();
	}
	
	@ScriptGetter
	@Override
	public String getHostname() {
		securityTest();
		return super.getHostname();
	}
	
	@ScriptGetter
	@Override
	public String getOrigin() {
		securityTest();
		return super.getOrigin();
	}
	
	@ScriptGetter
	@Override
	public String getPassword() {
		securityTest();
		return super.getPassword();
	}
	
	@ScriptGetter
	@Override
	public String getPathname() {
		securityTest();
		return super.getPathname();
	}
	
	@ScriptGetter
	@Override
	public String getPort() {
		securityTest();
		return super.getPort();
	}
	
	@ScriptGetter
	@Override
	public String getProtocol() {
		securityTest();
		return super.getProtocol();
	}
	
	@ScriptGetter
	@Override
	public String getSearch() {
		securityTest();
		return super.getSearch();
	}
	
	@ScriptGetter
	@Override
	public URLSearchParams getSearchParams() {
		securityTest();
		return super.getSearchParams();
	}
	
	@ScriptGetter
	@Override
	public String getUsername() {
		securityTest();
		return super.getUsername();
	}
	
	@ScriptFunction
	@Override
	public String toString() {
		if (!isAssociatedDocumentEffectiveScriptOriginEqual()) {
			securityTest();
		}
		
		return "[object Location]";
	}
	
	public void onAddressChanged() {
		URL address = document.getAddress();
		String addressString = address.toExternalForm();
		setInput(addressString);
	}
	
	public String getQueryEncoding() {
		String encoding = document.getInputEncoding();
		
		return (encoding == null)? super.getQueryEncoding() : encoding;
	}
	
	@Override
	public void updateSteps(String value) {	
		boolean normalNavigation = false;
		
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (activeDocument != null) {
			normalNavigation = activeDocument.getDocumentReadiness() == DocumentReadiness.COMPLETE;
			//TODO: In the task in which the algorithm is running, an activation behavior is currently being processed whose click event was trusted, or
			//TODO: In the task in which the algorithm is running, the event listener for a trusted click event is being handled.
		
			if (normalNavigation) {
				assign(value);
			} else {
				replace(value);
			}
		}
	}
	
	/*
	 * The object's URLUtils interface's get the base algorithm must return the API base URL 
	 * (non-Javadoc)
	 * @see org.fit.cssbox.scriptbox.url.URLUtils#getBase()
	 */
	@Override
	protected ParserURL getBase() {
		URL baseUrl = getApiBaseUrl();
		return (baseUrl != null)? new WrappedURL(baseUrl) : null;
	}
	
	private URL getApiBaseUrl() {
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (activeDocument != null) {
			Window window = activeDocument.getWindow();
			ScriptSettings<?> settings = window.getScriptSettings();
			URL baseURL = settings.getBaseUrl();
			return baseURL;
		}
		
		return null;
	}
	
	private void navigate(String url, boolean replacementEnabled) {	
		BrowsingUnit browsingUnit = context.getBrowsingUnit();
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		ScriptSettings<?> settings = stack.getIncumbentScriptSettings();
		BrowsingContext sourceBrowsingContext = settings.getResposibleBrowsingContext();
		
		navigate(sourceBrowsingContext, url, replacementEnabled);
	}
	
	private void navigate(BrowsingContext sourceBrowsingContext, String url, boolean replacementEnabled) {
		URL baseUrl = getApiBaseUrl();
		
		URL netUrl = null;
		try {
		if (baseUrl != null) {
			netUrl = new URL(baseUrl, url);
		} else {
			netUrl = new URL(url);
		}
		} catch (MalformedURLException e) {
			throwTypeErrorException();
		}
		
		navigate(sourceBrowsingContext, netUrl, replacementEnabled);
	}
	
	private void navigate(BrowsingContext sourceBrowsingContext, URL netUrl, boolean replacementEnabled) {	
		controller.navigate(sourceBrowsingContext, netUrl, true, false, replacementEnabled);
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#security-location
	 */
	private void securityTest() {
		if (!isRelatedDocumentEffectiveScriptOriginEqual()) {
			throwSecurityErrorException();
		}
	}
	
	private boolean isAssociatedDocumentEffectiveScriptOriginEqual() {
		return isEffectiveScriptOriginEqual(document);
	}
	
	private boolean isRelatedDocumentEffectiveScriptOriginEqual() {
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		return isEffectiveScriptOriginEqual(activeDocument);
	}
	
	private boolean isEffectiveScriptOriginEqual(Html5DocumentImpl document) {
		BrowsingUnit browsingUnit = context.getBrowsingUnit();
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		ScriptSettings<?> settings = stack.getEntryScriptSettings();

		if (settings != null && document != null) {
			Origin<?> settingsOrigin = settings.getEffectiveScriptOrigin();
			Origin<?> scriptOrigin = document.getEffectiveScriptOrigin();
			
			return settingsOrigin.equals(scriptOrigin);
		}
		
		return settings == null;
	}
	
	private boolean isFamiliarResponsibleBrowsingContext() {
		BrowsingUnit browsingUnit = context.getBrowsingUnit();
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		ScriptSettings<?> settings = stack.getEntryScriptSettings();
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (settings != null && activeDocument != null) {
			BrowsingContext responsibleContext = settings.getResposibleBrowsingContext();

			return responsibleContext.isFamiliarWith(context);
		}
		
		return false;
	}
	
	private void throwSecurityErrorException() {
		throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
	}
}

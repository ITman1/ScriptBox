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
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.url.ParserURL;
import org.fit.cssbox.scriptbox.url.URLUtils;
import org.fit.cssbox.scriptbox.url.WrappedURL;

public class Location extends URLUtils {
	private Html5DocumentImpl document;
	private NavigationController controller;
	private BrowsingContext context;
	
	public Location(Html5DocumentImpl document) {
		this.document = document;
		this.context = document.getBrowsingContext();
		this.controller = context.getNavigationController();
		
		onAddressChanged();
	}
	
	@ScriptFunction
	public void assign(String url) {
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
		
		navigate(url, replacementEnabled);
	}
	
	@ScriptFunction
	public void replace(String url) {
		navigate(url, true);
	}
	
	@ScriptFunction
	public void reload() {
		// TODO: If the currently executing task is the dispatch of a resize event in response to the user resizing the browsing context
		// TODO: If the browsing context's active document is an iframe srcdoc document
		// TODO: If the browsing context's active document has its reload override flag set
	
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (activeDocument != null) {
			URL address = activeDocument.getAddress();
			
			navigate(context, address, true);
		}
	}
	
	@Override
	public String toString() {
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
	
	protected URL getApiBaseUrl() {
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		
		if (activeDocument != null) {
			Window window = activeDocument.getWindow();
			ScriptSettings<?> settings = window.getScriptSettings();
			return settings.getBaseUrl();
		}

		return null;
	}
	
	protected void navigate(String url, boolean replacementEnabled) {	
		BrowsingUnit browsingUnit = context.getBrowsingUnit();
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		ScriptSettings<?> settings = stack.getIncumbentScriptSettings();
		BrowsingContext sourceBrowsingContext = settings.getResposibleBrowsingContext();
		
		navigate(sourceBrowsingContext, url, replacementEnabled);
	}
	
	protected void navigate(BrowsingContext sourceBrowsingContext, String url, boolean replacementEnabled) {
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
	
	protected void navigate(BrowsingContext sourceBrowsingContext, URL netUrl, boolean replacementEnabled) {	
		controller.navigate(sourceBrowsingContext, netUrl, true, false, replacementEnabled);
	}
}

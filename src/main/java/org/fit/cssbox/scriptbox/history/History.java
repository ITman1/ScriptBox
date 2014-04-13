/**
 * History.java
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

package org.fit.cssbox.scriptbox.history;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.UrlUtils;
import org.fit.cssbox.scriptbox.url.UrlUtils.UrlComponent;

public class History {	
	protected StateObject state;
	protected Html5DocumentImpl document;
	
	public History(Html5DocumentImpl document) {
		this.document = document;
	}

	@ScriptGetter
	public int getLength() {
		testIsActive();
		
		JointSessionHistory jointHistory = getJointSessionHistory();
		return jointHistory.getLength();
	}
	
	@ScriptGetter
	public StateObject getState() {
		testIsActive();
		
		return state;
	}
	
	@ScriptFunction
	public void go(long delta) {
		testIsActive();
		
		JointSessionHistory jointHistory = getJointSessionHistory();
		jointHistory.traverse((int)delta);
	}
	
	@ScriptFunction
	public void back() {
		go(-1);
	}
	
	@ScriptFunction
	public void forward() {
		go(1);
	}
		
	@ScriptFunction
	public void pushState(StateObject data, String title, String url) {
		setState(data, title, url, false);
	}

	@ScriptFunction
	public void pushState(StateObject data, String title) {
		pushState(data, title, null);
	}
	
	@ScriptFunction
	public void replaceState(StateObject data, String title, String url) {
		setState(data, title, url, true);
	}
	
	@ScriptFunction
	public void replaceState(StateObject data, String title) {
		replaceState(data, title, null);
	}
	
	public void setState(StateObject state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "[object History]";
	}
		
	protected JointSessionHistory getJointSessionHistory() {
		BrowsingContext context = document.getBrowsingContext();
		BrowsingUnit unit = context.getBrowsingUnit();
		
		return unit.getJointSessionHistory();
	}
	
	protected void testIsActive() {
		if (!document.isFullyActive()) {
			throwSecurityError();
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-pushstate
	 */
	protected void setState(StateObject data, String title, String url, boolean replace){
		testIsActive();
		
		Window window = document.getWindow();
		BrowsingContext context = document.getBrowsingContext();
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		ScriptSettings<?> settings = window.getScriptSettings();
		
		// 1) Let cloned data be a structured clone of the specified data
		StateObject clonedDate = data.clone();
		
		// 2) If the third argument is not null, run these substeps
		URL newURL = null;
		if (url != null) {
			URL absoluteURL = null;
			URL baseURL = settings.getBaseUrl();
			try {
				absoluteURL = new URL(baseURL, url);
			} catch (MalformedURLException e) {
				throwSecurityError();
			}
			
			URL address = document.getAddress();
			
			boolean identicalURLs = UrlUtils.identicalComponents(absoluteURL, address, 
					UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT);
			
			if (!identicalURLs) {
				throwSecurityError();
			}
			
			Html5DocumentImpl responsibleDocument = settings.getResponsibleDocument();
			UrlOrigin absoluteUrlOrigin = new UrlOrigin(absoluteURL);
			Origin<?> responsibleDocumentOrigin = responsibleDocument.getOrigin();
			URL responsibleDocumentAddress = responsibleDocument.getAddress();
			boolean pathQueryDiffers = !UrlUtils.identicalComponents(absoluteURL, address, 
					UrlComponent.PATH, UrlComponent.QUERY);
			
			if (!responsibleDocumentOrigin.equals(absoluteUrlOrigin) && pathQueryDiffers) {
				throwSecurityError();
			}
			
			newURL = absoluteURL;
		}
		
		// 3) If the third argument is null, then let new URL be the URL of the current entry.
		if (url == null) {
			newURL = currentEntry.getURL();
		}
		
		// 4) If the method invoked was the pushState() method:
		if (!replace) {
			// Remove all the entries in the browsing context's session history after the current entry
			sessionHistory.removeAllAfterCurrentEntry();
			
			// Remove any tasks queued by the history traversal task source with Document family
			
			sessionHistory.removeAllTopLevelDocumentFamilyTasks();
			
			// appropriate, update the current entry 
			
			currentEntry.updatePersistedUserState();
			
			// Add a state object entry to the session history
			
			SessionHistoryEntry newEntry = new SessionHistoryEntry(sessionHistory);
			newEntry.setStateObject(clonedDate);			
			newEntry.setTitle(title, true);
			newEntry.setDocument(document);
			newEntry.setURL(newURL);
			newEntry.setBrowsingContextName(currentEntry.getBrowsingContextName());
			newEntry.setPpersistedUserState(currentEntry.getPersistedUserState());
			
			sessionHistory.add(newEntry);
			
			// Update the current entry to be this newly added entry.
			sessionHistory.setCurrentEntry(newEntry);
			currentEntry = newEntry;
		} else {
			currentEntry.setStateObject(clonedDate);			
			currentEntry.setTitle(title, true);
			currentEntry.setURL(newURL);
		}
		
		// 5) If the current entry in the session history represents a non-GET request (e.g. it was the result of a 
		// POST submission) then update it to instead represent a GET request (or equivalent).
		
		// 6) Set the document's address to new URL.
		document.setAddress(newURL);
		
		// 7) Set history.state to a structured clone of cloned data.
		state = clonedDate;
		
		// 8) Let the latest entry of the Document of the current entry be the current entry.
		document.setLatestEntry(currentEntry);
	}
	
	protected void throwSecurityError() {
		throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
	}
}

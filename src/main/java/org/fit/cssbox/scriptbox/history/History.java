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
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;
import org.fit.cssbox.scriptbox.window.Window;

/**
 * Class implementing History interface which allows traversing
 * history from the scripts.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#history-1">History interface</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class History {	
	protected StateObject state;
	protected Html5DocumentImpl document;
	
	public History(Html5DocumentImpl document) {
		this.document = document;
	}

	/**
	 * Returns the number of entries in the joint session history.
	 * 
	 * @return Number of entries in the joint session history.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-length">History length</a>
	 */
	@ScriptGetter
	public int getLength() {
		testIsActive();
		
		JointSessionHistory jointHistory = getJointSessionHistory();
		return jointHistory.getLength();
	}
	
	/**
	 * Returns the current state object.
	 * 
	 * @return Current state object.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#state-object">State object</a>
	 */
	@ScriptGetter
	public StateObject getState() {
		testIsActive();
		
		return state;
	}
	
	/**
	 * Goes back or forward the specified number of steps in the joint session history.
	 * 
	 * @param delta Number of traversal steps.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-go">History go()</a>
	 */
	@ScriptFunction
	public void go(long delta) {
		testIsActive();
		
		JointSessionHistory jointHistory = getJointSessionHistory();
		jointHistory.traverse((int)delta);
	}
	
	/**
	 * Goes back one step in the joint session history.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-back">History back()</a>
	 */
	@ScriptFunction
	public void back() {
		go(-1);
	}
	
	/**
	 * Goes forward one step in the joint session history.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-forward">History forward()</a>
	 */
	@ScriptFunction
	public void forward() {
		go(1);
	}
		
	/**
	 * Pushes the given data into the session history, with the given title,
	 * and, if provided and not null, the given URL
	 * 
	 * @param data Data to be pushed into history.
	 * @param title Title of the page to be stored inside history.
	 * @param url New URL of the new state history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-pushstate">History pushState()</a>
	 */
	@ScriptFunction
	public void pushState(StateObject data, String title, String url) {
		setState(data, title, url, false);
	}

	/**
	 * Pushes the given data into the session history, with the given title.
	 * 
	 * @param data Data to be pushed into history.
	 * @param title Title of the page to be stored inside history.
	 * @see #pushState(StateObject, String, String)
	 */
	@ScriptFunction
	public void pushState(StateObject data, String title) {
		pushState(data, title, null);
	}
	
	/**
	 * Updates the current entry in the session history to have the given 
	 * data, title, and, if provided and not null, URL
	 * 
	 * @param data Data to be replaced inside history.
	 * @param title Title of the page to be replaced inside history.
	 * @param url New URL of the current state history entry.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-replacestate">History replaceState()</a>
	 */
	@ScriptFunction
	public void replaceState(StateObject data, String title, String url) {
		setState(data, title, url, true);
	}
	
	/**
	 * Updates the current entry in the session history to have the given data and title.
	 * 
	 * @param data Data to be replaced inside history.
	 * @param title Title of the page to be replaced inside history.
	 * @see #replaceState(StateObject, String, String)
	 */
	@ScriptFunction
	public void replaceState(StateObject data, String title) {
		replaceState(data, title, null);
	}
	
	/**
	 * Sets new state object of this history object.
	 * 
	 * @param state New state object of this history object.
	 */
	public void setState(StateObject state) {
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "[object History]";
	}
		
	private JointSessionHistory getJointSessionHistory() {
		BrowsingContext context = document.getBrowsingContext();
		BrowsingUnit unit = context.getBrowsingUnit();
		
		return unit.getJointSessionHistory();
	}
	
	private void testIsActive() {
		if (!document.isFullyActive()) {
			throwSecurityError();
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#dom-history-pushstate
	 */
	private void setState(StateObject data, String title, String url, boolean replace){
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
			
			boolean identicalURLs = URLUtilsHelper.identicalComponents(absoluteURL, address, 
					UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT);
			
			if (!identicalURLs) {
				throwSecurityError();
			}
			
			Html5DocumentImpl responsibleDocument = settings.getResponsibleDocument();
			UrlOrigin absoluteUrlOrigin = new UrlOrigin(absoluteURL);
			Origin<?> responsibleDocumentOrigin = responsibleDocument.getOrigin();
			boolean pathQueryDiffers = !URLUtilsHelper.identicalComponents(absoluteURL, address, 
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
			
			context.getEventLoop().removeAllTopLevelDocumentFamilyTasks();
			
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
	
	private void throwSecurityError() {
		throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
	}
}

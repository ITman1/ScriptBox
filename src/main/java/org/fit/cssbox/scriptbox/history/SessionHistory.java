/**
 * SessionHistory.java
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

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.AuxiliaryBrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadyState;
import org.fit.cssbox.scriptbox.dom.events.WindowEventHandlers;
import org.fit.cssbox.scriptbox.dom.events.script.HashChangeEvent;
import org.fit.cssbox.scriptbox.dom.events.script.PopStateEvent;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;
import org.fit.cssbox.scriptbox.window.Window;

/**
 * Class representing history for a browsing context.
 * The sequence of Documents and additional informations in a browsing context is its session history.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#session-history">Session history</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class SessionHistory {
	protected BrowsingContext context;
	protected List<SessionHistoryEntry> entries;
	protected int currentEntryPosition;
	protected Set<SessionHistoryListener> listeners;
	
	/**
	 * Constructs session history for a given browsing context.
	 *  
	 * @param context Browsing context that owns this session history.
	 */
	public SessionHistory(BrowsingContext context) {
		this.context = context;
		this.entries = new ArrayList<SessionHistoryEntry>();
		this.listeners = new HashSet<SessionHistoryListener>();
		
		initSessionHistory();
	}
	
	/**
	 * Returns current entry of this session history.
	 * 
	 * @return Current entry of this session history
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#current-entry">Current entry</a>
	 */
	public SessionHistoryEntry getCurrentEntry() {
		if (currentEntryPosition != -1) {
			return entries.get(currentEntryPosition);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns all session history entries.
	 * 
	 * @return All session history entries.
	 */
	public List<SessionHistoryEntry> getSessionHistoryEntries() {
		return entries;
	}
	
	/**
	 * Sets current session history entry.
	 * 
	 * @param entry Entry to be set as current session history entry.
	 */
	public void setCurrentEntry(SessionHistoryEntry entry) {
		if (entry.getSessionHistory() == this) {
			int newPosition = entries.indexOf(entry);
			
			if (newPosition != -1) {
				currentEntryPosition = newPosition;
				fireCurrentEntryChanged(entry);
			}
		}
	}
	
	/**
	 * Returns length of this session history.
	 * 
	 * @return Length of this session history
	 */
	public int getLength() {
		return entries.size();
	}
	
	/**
	 * Discards this session history.
	 */
	public void discard() {
		if (context != null) {
			context = null;
			currentEntryPosition = -1;
			
			while (!entries.isEmpty()) {
				SessionHistoryEntry entry = entries.remove(0);
				Html5DocumentImpl doc = entry.getDocument();
				if (doc != null) {
					doc.discard();
				}
			}
						
			fireHistoryDestroyed();
		}
	}
	
	/**
	 * Removes session history entry from a given index.
	 * 
	 * @param index Index from which to remove session history entry.
	 */
	public void remove(int index) {
		if (index < 0) {
			return;
		}
	
		if (entries.size() > index) {
			SessionHistoryEntry entry = entries.remove(index);
			
			boolean currentChanged = false;
			if (index < currentEntryPosition) {
				currentEntryPosition--;
				currentChanged = true;
			}
			
			if (entries.isEmpty()) {
				currentEntryPosition = -1;
				currentChanged = true;
			}
			
			fireEntryRemoved(entry);
			
			if (currentChanged) {
				SessionHistoryEntry currentEntry = getCurrentEntry();
				fireCurrentEntryChanged(currentEntry);
			}
		}
	}
	
	/**
	 * Removes given session history entry.
	 * 
	 * @param entry Entry to be removed from this session history.
	 */
	public void remove(SessionHistoryEntry entry) {
		int index = entries.indexOf(entry);
		remove(index);
	}
	
	/**
	 * Removes session history entry before the passed one.
	 * 
	 * @param entry Entry before which should be removed previous entry.
	 */
	public void removeBefore(SessionHistoryEntry entry) {
		int index = entries.indexOf(entry);
		remove(index - 1);
	}
	
	/**
	 * Removes all entries after the current entry.
	 */
	public void removeAllAfterCurrentEntry() {
		SessionHistoryEntry currentEntry = getCurrentEntry();
		
		removeAllAfter(currentEntry);
	}
	
	/**
	 * Removes all entries after the passed one.
	 * 
	 * @param entry Entry after which should be removed all followed session history entries.
	 */
	public void removeAllAfter(SessionHistoryEntry entry) {
		int index = entries.indexOf(entry) + 1;
		
		while (entries.size() > index) {
			remove(index);
		}
	}

	/**
	 * Adds new session history entry into this session history.
	 * 
	 * @param entry New session history entry to be added into this session history
	 */
	public void add(SessionHistoryEntry entry) {
		if (entry.getSessionHistory() == this) {
			entries.add(entry);
			entry.setVisited(new Date());
			
			fireEntryInserted(entry);
		}
	}
	
	/**
	 * Returns associated browsing context.
	 * 
	 * @return Associated browsing context.
	 */
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	/**
	 * Registers event listener to this session history.
	 * 
	 * @param listener New event listener to be registered.
	 */
	public void addListener(SessionHistoryListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes event listener to this session history.
	 * 
	 * @param listener Event listener to be removed.
	 */
	public void removeListener(SessionHistoryListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Traverses session history from current entry to the specified entry.
	 * 
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @param replacementEnabled Flag signaling whether to replace current session history entry or not.
	 * @param asynchronousEvents Flag signaling whether to dispatch generated events, or simple fire in the current task.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#traverse-the-history">Traverse the history</a>
	 */
	public void traverseHistory(SessionHistoryEntry specifiedEntry, boolean replacementEnabled, boolean asynchronousEvents) {
		traverseHistory(context, specifiedEntry, replacementEnabled, asynchronousEvents);
	}
	
	/**
	 * Traverses session history from current entry to the specified entry 
	 * with asynchronousEvents set to false.
	 * 
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @param replacementEnabled Flag signaling whether to replace current session history entry or not.
	 * @see #traverseHistory(SessionHistoryEntry, boolean, boolean)
	 */
	public void traverseHistory(SessionHistoryEntry specifiedEntry, boolean replacementEnabled) {
		traverseHistory(context, specifiedEntry, replacementEnabled);
	}
	
	/**
	 * Traverses session history from current entry to the specified entry 
	 * with asynchronousEvents and replacementEnabled set to false.
	 * 
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @see #traverseHistory(SessionHistoryEntry, boolean, boolean)
	 */
	public void traverseHistory(SessionHistoryEntry specifiedEntry) {
		traverseHistory(context, specifiedEntry);
	}
	
	/**
	 * Traverses session history from current entry to the specified entry 
	 * with asynchronousEvents and replacementEnabled set to false.
	 * 
	 * @param specifiedBrowsingContext Browsing context of which is traversed the session history.
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @see #traverseHistory(BrowsingContext, SessionHistoryEntry, boolean, boolean)
	 */
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, false, false);
	}
	
	/**
	 * Traverses session history from current entry to the specified entry 
	 * with asynchronousEvents set to false.
	 * 
	 * @param specifiedBrowsingContext Browsing context of which is traversed the session history.
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @param replacementEnabled Flag signaling whether to replace current session history entry or not.
	 * @see #traverseHistory(BrowsingContext, SessionHistoryEntry, boolean, boolean)
	 */
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, replacementEnabled, false);
	}
	
	/**
	 * Traverses session history from current entry to the specified entry.
	 * 
	 * @param specifiedBrowsingContext Browsing context of which is traversed the session history.
	 * @param specifiedEntry Session history entry where to traverse this session history.
	 * @param replacementEnabled Flag signaling whether to replace current session history entry or not.
	 * @param asynchronousEvents Flag signaling whether to dispatch generated events, or simple fire in the current task.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#traverse-the-history">Traverse the history</a>
	 */
	public static void traverseHistory(final BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled, boolean asynchronousEvents ) {
		// 1) If there is no longer a Document object for the entry in question, 
		// navigate the browsing context to the resource and abort.
		Html5DocumentImpl specifiedDocument = specifiedEntry.getDocument();
		if (specifiedDocument == null || specifiedDocument.isUnloaded()) {
			BrowsingContext context = specifiedEntry.getSessionHistory().getBrowsingContext();
			context.getNavigationController().update(specifiedEntry);
			return;
		}

		SessionHistory sessionHistory = specifiedEntry.getSessionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
				
		// 2) If the current entry's title was not set by the pushState() or replaceState() methods, 
		// then set its title to document.title IDL attribute. 	
		if (!currentEntry.hasPushedStateTitle()) {
			String persistedTitle = currentDocument.getTitle();
			currentEntry.setTitle(persistedTitle);
		}
		
		// 3) Update the current entry to reflect any state that we want to persist.
		currentEntry.updatePersistedUserState();
		
		// 4) If the specified entry has a different Document object than the current entry, then run the following sub-steps.
		if (specifiedEntry.getDocument() != currentEntry.getDocument()) {
			// 4.1) Remove any tasks queued by the history traversal task source that are associated with 
			// any Document objects in the top-level browsing context's document family.
			specifiedBrowsingContext.getEventLoop().removeAllTopLevelDocumentFamilyTasks();
			
			// 4.2) If the origin of the Document of the specified entry is not the same as the origin 
			// of the Document of the current entry, then run the following sub-sub-steps
			DocumentOrigin specifiedEntryDocumentOrigin = specifiedDocument.getOriginContainer().getOrigin();
			DocumentOrigin currentEntryDocumentOrigin = currentDocument.getOriginContainer().getOrigin();
			List<SessionHistoryEntry> sessionEntries = sessionHistory.getSessionHistoryEntries();
			
			if (!specifiedEntryDocumentOrigin.equals(currentEntryDocumentOrigin)) {
				String browsingContextName = specifiedBrowsingContext.getName();
				for (SessionHistoryEntry entry : sessionEntries) {
					if (currentEntry.hasSameDocumentOrigin(entry) && entry.isContiguous(currentEntry)) {
						entry.setBrowsingContextName(browsingContextName);
					}
				}
				if (specifiedBrowsingContext.isTopLevelBrowsingContext() && !(specifiedBrowsingContext instanceof AuxiliaryBrowsingContext)) {
					specifiedBrowsingContext.setName("");
				}
			}
			
			// 4.3) Make the specified entry's Document object the active document of the browsing context.
			sessionHistory.currentEntryPosition = sessionHistory.entries.indexOf(specifiedEntry);
			
			// 4.4) If the specified entry has a browsing context name stored with it, then run the following sub-sub-steps
			String specifiedBrowsingContextName = specifiedEntry.getBrowsingContextName();
			if (specifiedBrowsingContextName != null) {
				specifiedBrowsingContext.setName(specifiedBrowsingContextName);
				
				for (SessionHistoryEntry entry : sessionEntries) {
					if (specifiedEntry.hasSameDocumentOrigin(entry) && entry.isContiguous(specifiedEntry)) {
						entry.setBrowsingContextName("");
					}
				}
			}
			
			// TODO: 4.5) If the specified entry's Document has any form controls whose autofill field name is "off", invoke the reset algorithm of each of those elements.
			
			// 4.6) If the current document readiness of the specified entry's Document is "complete", queue a task 
			if (specifiedDocument.getDocumentReadiness() == DocumentReadyState.COMPLETE) {
				specifiedBrowsingContext.getEventLoop().queueTask(new Task(TaskSource.DOM_MANIPULATION, specifiedDocument) {
					
					@Override
					public void execute() throws TaskAbortedException, InterruptedException {
						Html5DocumentImpl document = getDocument();
						
						// If the Document's page showing flag is true, then abort this task 
						if (document.isPageShowingFlag()) {
							return;
						}
						
						// Set the Document's page showing flag to true.
						document.setPageShowingFlag(true);
						
						// TODO: Run any session history document visibility change steps for Document that are defined by other applicable specifications
						// FIXME?: Should be load fired here?
						/*org.fit.cssbox.scriptbox.dom.events.script.Event event = new org.fit.cssbox.scriptbox.dom.events.script.Event(true, document);
						Window window = document.getWindow();
						event.initEvent("load", false, false);
						window.dispatchEvent(event);*/
						
						// TODO: Fire a trusted event with the name pageshow at the Window
					}
				});
			}

		}
		
		// 5) Set the document's address to the URL of the specified entry.
		specifiedDocument.setAddress(specifiedEntry.getURL());
		
		// 6) If the specified entry has a URL whose fragment identifier differs from that of the current entry
		boolean hashChanged = false;
		URL specifiedURI = specifiedEntry.getURL();
		URL currentURI = currentEntry.getURL();
		SessionHistoryEntry specifiedPushedEnty = specifiedEntry.getPushedEntry();
		SessionHistoryEntry currentPushedEnty = currentEntry.getPushedEntry();
		
		if (specifiedDocument == currentDocument && (!specifiedEntry.hasStateObject() || !currentEntry.hasStateObject()) && specifiedPushedEnty != currentEntry && currentPushedEnty != specifiedEntry) {
			hashChanged = !URLUtilsHelper.identicalComponents(specifiedURI, currentURI, UrlComponent.REF);
		}
		
		// 7) If the traversal was initiated with replacement enabled, remove  
		// the entry immediately before the specified entry in the session history
		if (replacementEnabled) {
			sessionHistory.removeBefore(specifiedEntry);
		}
		
		// 8) If the specified entry is not an entry with persisted user state, but 
		// its URL has a fragment identifier, scroll to the fragment identifier.
		String specifiedUriFragment =  specifiedURI.getRef();
		if (specifiedBrowsingContext instanceof IFrameContainerBrowsingContext) {
			if (!specifiedEntry.hasPersistedUserState() && specifiedUriFragment != null && !specifiedUriFragment.isEmpty()) {
				((IFrameContainerBrowsingContext)specifiedBrowsingContext).scrollToFragment(specifiedUriFragment);
			} else if (!specifiedEntry.hasPersistedUserState() && !specifiedEntry.hasStateObject()) {
				((IFrameContainerBrowsingContext)specifiedBrowsingContext).scroll(0, 0);
			}
		}
		
		// 9) If the entry is an entry with persisted user state, the user agent may update aspects of the document
		// and its rendering, for instance the scroll position or values of form fields, that it had previously recorded. 
		specifiedEntry.applyPersistedUserState();
		
		// 10) If the entry is a state object entry, let state be a structured clone of that state object. Otherwise, let state be null.
		StateObject state = null;
		if (specifiedEntry.hasStateObject()) {
			StateObject stateObject = specifiedEntry.getStateObject();
			state = stateObject.clone();
		}
		
		// 11) Set history.state to state.
		History history = specifiedDocument.getHistory();
		history.setState(state);
		
		// 12) Let state changed be true if the Document of the specified entry has a latest entry, 
		// and that entry is not the specified entry; otherwise let it be false.
		boolean stateChanged = false;
		SessionHistoryEntry specifiedLatestEntry = specifiedDocument.getLatestEntry();
		if (specifiedLatestEntry != null && specifiedLatestEntry != specifiedEntry) {
			stateChanged = true;
		}
		
		// 13) Let the latest entry of the Document of the specified entry be the specified entry.
		specifiedDocument.setLatestEntry(specifiedEntry);
		
		// 14) Fire PopStateEvent and HashChangeEvent
		Window windowTarget = specifiedDocument.getWindow();
		
		if (hashChanged) {
			HashChangeEvent hashChangeEvent = new HashChangeEvent();
			String oldURL = currentURI.toExternalForm();
			String newURL = specifiedURI.toExternalForm();
			hashChangeEvent.initEvent(WindowEventHandlers.onhashchange, true, false, oldURL, newURL);
		
			if (asynchronousEvents) {
				windowTarget.dispatchEvent(hashChangeEvent, windowTarget);
			} else {
				windowTarget.dispatchEvent(hashChangeEvent);
			}
		}
		
		if (stateChanged) {
			PopStateEvent popStateEvent = new PopStateEvent();
			popStateEvent.initEvent(WindowEventHandlers.onpopstate, true, false, state);
			
			if (asynchronousEvents) {
				windowTarget.dispatchEvent(popStateEvent, windowTarget);
			} else {
				windowTarget.dispatchEvent(popStateEvent);
			}
		}
		
		// 15) The current entry is now the specified entry
		sessionHistory.setCurrentEntry(specifiedEntry);
		
		sessionHistory.fireHistoryTravered(currentEntry, specifiedEntry);
	}
	
	private void initSessionHistory() {
		SessionHistoryEntry blankPageEntry = new SessionHistoryEntry(this, true);
		Html5DocumentImpl blankDocument = Html5DocumentImpl.createBlankDocument(context);
		blankDocument.implementSandboxing();
		
		blankPageEntry.setDocument(blankDocument);
		blankPageEntry.setVisited(new Date());
		blankPageEntry.setURL(blankDocument.getAddress());
		
		entries.add(blankPageEntry);
		currentEntryPosition = 0;
	}
	
	private void fireHistoryTravered(SessionHistoryEntry fromEntry, SessionHistoryEntry toEntry) {
		SessionHistoryEvent event = new SessionHistoryEvent(this, SessionHistoryEvent.EventType.TRAVERSED, toEntry, fromEntry);
		Set<SessionHistoryListener> listenersCopy = new HashSet<SessionHistoryListener>(listeners);
		
		for (SessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
	
	private void fireEntryInserted(SessionHistoryEntry entry) {
		SessionHistoryEvent event = new SessionHistoryEvent(this, SessionHistoryEvent.EventType.INSERTED, entry, null);
		Set<SessionHistoryListener> listenersCopy = new HashSet<SessionHistoryListener>(listeners);
		
		for (SessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
	
	private void fireEntryRemoved(SessionHistoryEntry entry) {
		SessionHistoryEvent event = new SessionHistoryEvent(this, SessionHistoryEvent.EventType.REMOVED, entry, null);
		Set<SessionHistoryListener> listenersCopy = new HashSet<SessionHistoryListener>(listeners);
		
		for (SessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
	
	private void fireCurrentEntryChanged(SessionHistoryEntry entry) {
		SessionHistoryEvent event = new SessionHistoryEvent(this, SessionHistoryEvent.EventType.CURRENT_CHANGED, entry, null);
		Set<SessionHistoryListener> listenersCopy = new HashSet<SessionHistoryListener>(listeners);
		
		for (SessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
	
	private void fireHistoryDestroyed() {
		SessionHistoryEvent event = new SessionHistoryEvent(this, SessionHistoryEvent.EventType.DESTROYED, null, null);
		Set<SessionHistoryListener> listenersCopy = new HashSet<SessionHistoryListener>(listeners);
		
		for (SessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
}

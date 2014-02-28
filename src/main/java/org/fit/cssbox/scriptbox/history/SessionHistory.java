package org.fit.cssbox.scriptbox.history;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.AuxiliaryBrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public class SessionHistory {

	
	protected BrowsingContext context;
	protected List<SessionHistoryEntry> entries;
	protected int currentEntryPosition;
	
	public SessionHistory(BrowsingContext context) {
		this.context = context;
		this.entries = new ArrayList<SessionHistoryEntry>();
		initSessionHistory();
	}
	
	public SessionHistoryEntry getCurrentEntry() {
		if (currentEntryPosition != -1) {
			return entries.get(currentEntryPosition);
		} else {
			return null;
		}
	}
	
	public List<SessionHistoryEntry> getSessionHistoryEntries() {
		return entries;
	}
	
	public void setCurrentEntry(SessionHistoryEntry entry) {
		currentEntryPosition = entries.indexOf(entry);
	}
	
	public int getLength() {
		return entries.size();
	}
	
	public void clean() {
		entries.clear();
		initSessionHistory();
	}
	
	public void remove(SessionHistoryEntry entry) {
		entries.remove(entry);
	}
	
	public void removeBefore(SessionHistoryEntry entry) {
		int index = entries.indexOf(entry);
		
		if (index > 0) {
			entries.remove(index - 1);
		}
	}
	
	public void filter(Predicate<SessionHistoryEntry> predicate) {
		entries.clear();
		Iterables.addAll(entries, Iterables.filter(entries, predicate));
	}
	
	public void add(SessionHistoryEntry entry) {
		entries.add(entry);
	}
	
	private void initSessionHistory() {
		SessionHistoryEntry blankPageEntry = new SessionHistoryEntry(this);
		Html5DocumentImpl blankDocument = Html5DocumentImpl.createBlankDocument(context);
		blankDocument.implementSandboxing();
		
		blankPageEntry.setSocument(blankDocument);
		
		blankPageEntry.setURL(blankDocument.getAddress());
		
		entries.add(blankPageEntry);
		currentEntryPosition = 0;
	}
	
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, false, false);
	}
	
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, replacementEnabled, false);
	}
	
	public static void traverseHistory(final BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled, boolean asynchronousEvents ) {
		/*
		 * 1) If there is no longer a Document object for the entry in question, 
		 * navigate the browsing context to the resource and abort.
		 */
		if (specifiedEntry.getDocument() == null) {
			BrowsingContext context = specifiedEntry.getSessionHistory().getBrowsingContext();
			context.getNavigationController().update(specifiedEntry);
			return;
		}
		
		/*
		 * TODO:
		 * 2) If the current entry's title was not set by the pushState() or replaceState() methods, then set its title 
		 * to the value returned by the document.title IDL attribute. 
		 */
		
		/*
		 * TODO: 
		 * 3) If appropriate, update the current entry in the browsing context's Document object's History object to reflect any state that the 
		 * user agent wishes to persist. The entry is then said to be an entry with persisted user state.
		 */
		

		SessionHistory sessionHistory = specifiedEntry.getSessionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		Html5DocumentImpl specifiedDocument = specifiedEntry.getDocument();
		
		/*
		 * 4) If the specified entry has a different Document object than the current entry, then run the following sub-steps.
		 */
		if (currentEntry != null && specifiedEntry.getDocument() != currentEntry.getDocument()) {
			// 4.1) Remove any tasks queued by the history traversal task source that are associated with 
			// any Document objects in the top-level browsing context's document family.
			final BrowsingUnit browsingUnit = specifiedBrowsingContext.getBrowsingUnit();
			browsingUnit.getEventLoop().filter(TaskSource.HISTORY_TRAVERSAL, new Predicate<Task>() {
				
				@Override
				public boolean apply(Task task) {
					BrowsingContext topLevel = browsingUnit.getWindowBrowsingContext();
					Collection<Html5DocumentImpl> documentFamily = topLevel.getDocumentFamily();
					return documentFamily.contains(task.getDocument());
				}
			});
			
			// 4.2) If the origin of the Document of the specified entry is not the same as the origin 
			// of the Document of the current entry, then run the following sub-sub-steps
			DocumentOrigin specifiedEntryDocumentOrigin = specifiedDocument.getOriginContainer().getOrigin();
			DocumentOrigin currentEntryDocumentOrigin = currentDocument.getOriginContainer().getOrigin();
			List<SessionHistoryEntry> sessionEntries = sessionHistory.getSessionHistoryEntries();
			
			if (!specifiedEntryDocumentOrigin.equals(currentEntryDocumentOrigin)) {
				String browsingContextName = specifiedBrowsingContext.getName();
				for (SessionHistoryEntry entry : sessionEntries) {
					if (currentEntry.hasSameDocumentOrigin(entry)) {
						entry.setBrowsingContextName(browsingContextName);
					}
				}
				if (specifiedBrowsingContext.isTopLevelBrowsingContext() && !(specifiedBrowsingContext instanceof AuxiliaryBrowsingContext)) {
					specifiedBrowsingContext.setName(null);
				}
			}
			
			// 4.3) Make the specified entry's Document object the active document of the browsing context.
			sessionHistory.setCurrentEntry(specifiedEntry);
			
			// 4.4) If the specified entry has a browsing context name stored with it, then run the following sub-sub-steps
			String specifiedBrowsingContextName = specifiedEntry.getBrowsingContextName();
			if (specifiedBrowsingContextName != null) {
				specifiedBrowsingContext.setName(specifiedBrowsingContextName);
				
				for (SessionHistoryEntry entry : sessionEntries) {
					if (specifiedEntry.hasSameDocumentOrigin(entry)) {
						entry.setBrowsingContextName(null);
					}
				}
			}
			
			// TODO: 4.5) If the specified entry's Document has any form controls whose autofill field name is "off", invoke the reset algorithm of each of those elements.
			
			// TODO: 4.6) If the current document readiness of the specified entry's Document is "complete", queue a task to run the following sub-sub-steps
		}
		
		// 5) Set the document's address to the URL of the specified entry.
		specifiedDocument.setAddress(specifiedEntry.getURL());
		
		// 6) If the specified entry has a URL whose fragment identifier differs from that of the current entry
		boolean hashChanged = false;
		URL specifiedURI = specifiedDocument.getAddress();
		URL currentURI = currentDocument.getAddress();
		String specifiedUriFragment = null;
		String currentUriFragment = null;
		
		if (specifiedURI != null && currentURI != null) {
			specifiedUriFragment = specifiedURI.getRef();
			currentUriFragment = currentURI.getRef();
			
			if (specifiedUriFragment != null && currentUriFragment != null) {
				if (!specifiedUriFragment.equals(currentUriFragment) && specifiedDocument == currentDocument) {
					hashChanged = true;
				}
			}
		}
		
		// 7) If the traversal was initiated with replacement enabled, remove  
		// the entry immediately before the specified entry in the session history
		if (replacementEnabled) {
			sessionHistory.removeBefore(specifiedEntry);
		}
		
		// 8) If the specified entry is not an entry with persisted user state, but 
		// its URL has a fragment identifier, scroll to the fragment identifier.
		if (!specifiedEntry.hasPersistedUserState() && specifiedUriFragment != null && !specifiedUriFragment.isEmpty()) {
			specifiedBrowsingContext.scrollToFragment(specifiedUriFragment);
		}
		
		// TODO: 9) If the entry is an entry with persisted user state, the user agent may update aspects of the document
		// and its rendering, for instance the scroll position or values of form fields, that it had previously recorded. 
		
		// 10) If the entry is a state object entry, let state be a structured clone of that state object. Otherwise, let state be null.
		StateObject state = null;
		if (specifiedEntry.hasStateObject()) {
			state = specifiedEntry.getStateObject().clone();
		}
		
		// TODO: 11) Set history.state to state.
		
		// 12) Let state changed be true if the Document of the specified entry has a latest entry, 
		// and that entry is not the specified entry; otherwise let it be false.
		boolean stateChanged = false;
		SessionHistoryEntry specifiedLatestEntry = specifiedDocument.getLatestEntry();
		if (specifiedLatestEntry != null && specifiedLatestEntry != specifiedEntry) {
			stateChanged = true;
		}
		
		// 13) Let the latest entry of the Document of the specified entry be the specified entry.
		specifiedDocument.setLatestEntry(specifiedEntry);
		
		// TODO: 14) and 15)
	}
	
}

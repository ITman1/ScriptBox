package org.fit.cssbox.scriptbox.history;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public class HistoryTraversalHelper {
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, false, false);
	}
	
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled) {
		traverseHistory(specifiedBrowsingContext, specifiedEntry, replacementEnabled, false);
	}
	
	public static void traverseHistory(BrowsingContext specifiedBrowsingContext, SessionHistoryEntry specifiedEntry, boolean replacementEnabled, boolean asynchronousEvents ) {
		if (specifiedEntry.getDocument() == null) {
			BrowsingContext context = specifiedEntry.getSessionHistory().getBrowsingContext();
			URL documentURL = specifiedEntry.getURL();
			context.navigate(documentURL);
			return;
		}
		
		/*
		 * TODO:
		 * If the current entry's title was not set by the pushState() or replaceState() methods, then set its title 
		 * to the value returned by the document.title IDL attribute. 
		 */
		
		/*
		 * TODO: 
		 * If appropriate, update the current entry in the browsing context's Document object's History object to reflect any state that the 
		 * user agent wishes to persist. The entry is then said to be an entry with persisted user state.
		 */
		
		SessionHistoryEntry currentEntry = specifiedEntry.getSessionHistory().getCurrentEntry();
		if (currentEntry != null && specifiedEntry.getDocument() != currentEntry.getDocument()) {
			
		}
		
	}
}

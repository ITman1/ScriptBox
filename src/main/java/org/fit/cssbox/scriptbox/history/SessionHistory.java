package org.fit.cssbox.scriptbox.history;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class SessionHistory {
	protected BrowsingContext context;
	protected List<SessionHistoryEntry> entries;
	protected int currentEntryPosition;
	
	public SessionHistory(BrowsingContext context) {
		this.context = context;
		initSessionHistory();
	}
	
	public SessionHistoryEntry getCurrentEntry() {
		if (currentEntryPosition != -1) {
			return entries.get(currentEntryPosition);
		} else {
			return null;
		}
	}
	
	public void clean() {
		entries.clear();
		initSessionHistory();
	}
	
	public List<SessionHistoryEntry> getSessionHistoryEntries() {
		return entries;
	}
	
	private void initSessionHistory() {
		SessionHistoryEntry blankPageEntry = new SessionHistoryEntry(this);
		Html5DocumentImpl blankDocument = Html5DocumentImpl.createBlankDocument(context);
		blankDocument.implementSandboxing();
		
		blankPageEntry.setSocument(blankDocument);
		
		try {
			blankPageEntry.setURL(blankDocument.getURI().toURL());
		} catch (MalformedURLException e) {
			blankPageEntry.setURL(null);
		}
		
		entries.add(blankPageEntry);
		currentEntryPosition = 0;
	}
	
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
}

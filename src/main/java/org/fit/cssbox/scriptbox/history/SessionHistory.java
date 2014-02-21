package org.fit.cssbox.scriptbox.history;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class SessionHistory {
	private BrowsingContext _context;
	private List<SessionHistoryEntry> _entries;
	private int _currentEntryPosition;
	
	public SessionHistory(BrowsingContext context) {
		_context = context;
		initSessionHistory();
	}
	
	public SessionHistoryEntry getCurrentEntry() {
		if (_currentEntryPosition != -1) {
			return _entries.get(_currentEntryPosition);
		} else {
			return null;
		}
	}
	
	public Html5DocumentImpl go(int delta) {
		return null;
	}
	
	public void clean() {
		_entries.clear();
		initSessionHistory();
	}
	
	public List<SessionHistoryEntry> getAllEntries() {
		return _entries;
	}
	
	private void initSessionHistory() {
		SessionHistoryEntry blankPageEntry = new SessionHistoryEntry();
		Html5DocumentImpl blankDocument = Html5DocumentImpl.createBlankDocument(_context);
		blankDocument.implementSandboxing();
		
		blankPageEntry.setSocument(blankDocument);
		
		try {
			blankPageEntry.setURL(blankDocument.getURI().toURL());
		} catch (MalformedURLException e) {
			blankPageEntry.setURL(null);
		}
		
		_entries.add(blankPageEntry);
		_currentEntryPosition = 0;
	}
	
}

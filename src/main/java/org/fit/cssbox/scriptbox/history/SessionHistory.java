package org.fit.cssbox.scriptbox.history;

import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;

public class SessionHistory {
	private BrowsingContext _context;
	private List<SessionHistoryEntry> _entries;
	private Iterator<SessionHistoryEntry> _currentEntryIterator;
	private SessionHistoryEntry _currentEntry;
	
	public SessionHistory(BrowsingContext context) {
		_context = context;
		initSessionHistory();
	}
	
	public ScriptableDocument getActiveDocument() {
		if (_currentEntry != null) {
			return _currentEntry.getDocument();
		} else {
			return null;
		}
		
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
		ScriptableDocument blankDocument = ScriptableDocument.createBlankDocument(_context);
		blankDocument.implementSandboxing();
		
		blankPageEntry.setSocument(blankDocument);
		
		try {
			blankPageEntry.setURL(blankDocument.getURI().toURL());
		} catch (MalformedURLException e) {
			blankPageEntry.setURL(null);
		}
		
		_entries.add(blankPageEntry);
		_currentEntry = blankPageEntry;
		_currentEntryIterator = _entries.iterator();
	}
}

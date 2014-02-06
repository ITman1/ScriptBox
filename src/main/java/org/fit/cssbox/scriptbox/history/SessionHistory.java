package org.fit.cssbox.scriptbox.history;

import java.util.Iterator;
import java.util.List;

import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;

public class SessionHistory {
	List<SessionHistoryEntry> entries;
	
	Iterator<SessionHistoryEntry> currentEntry;
	
	public ScriptableDocument getActiveDocument() {
		if (currentEntry.hasNext()) {
			return currentEntry.next().getDocument();
		} else {
			return null;
		}
		
	}
	
	public void clean() {
		entries.clear();
		currentEntry = entries.iterator();
	}
}

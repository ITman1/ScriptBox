package org.fit.cssbox.scriptbox.history;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class JointSessionHistory {
	private class VisitedComparator implements Comparator<SessionHistoryEntry> {
	    public int compare(SessionHistoryEntry e1, SessionHistoryEntry e2) {
	        if (e1.getVisited().before(e2.getVisited())) {
	            return -1;
	        } else if (e1.getVisited().after(e2.getVisited())) {
	            return 1;
	        } else {
	            return 0;
	        }
	    }
	}
	
	private BrowsingUnit _browsingUnit;
	private int _currentJointSessionHistoryEntryPosition;
	private ArrayList<SessionHistoryEntry> _historyEntries;
	
	public JointSessionHistory(BrowsingUnit browsingUnit) {
		_browsingUnit = browsingUnit;
		
		_historyEntries = new ArrayList<SessionHistoryEntry>();
	}
	
	public void traverse(int delta) {
		refreshJointSessionHistory();
		
		int destinationIndex = _currentJointSessionHistoryEntryPosition + delta;
		
		if (destinationIndex < 0 || destinationIndex >= _historyEntries.size()) {
			return;
		}
		
		SessionHistoryEntry specifiedEntry = _historyEntries.get(destinationIndex);
		BrowsingContext specifiedBrowsingContext = specifiedEntry.getSessionHistory().getBrowsingContext();
		
		// TODO: If the specified browsing context's active document's unload a document 
		// algorithm is currently running, abort these steps.
		
		_browsingUnit.queueTask(new HistoryTraversalTask(specifiedEntry, specifiedBrowsingContext));
	}
	
	private Set<SessionHistory> getAllSessionHistories() {
		Set<SessionHistory> entries = new HashSet<SessionHistory>();
		
		Collection<BrowsingContext> contexts = _browsingUnit.getWindowBrowsingContext().getDescendantContexts();
		
		for (BrowsingContext context : contexts) {
			SessionHistory sessionHistory = context.getSesstionHistory();
			if (sessionHistory != null) {
				entries.add(sessionHistory);
			}
		}
		
		return entries;
	}
	
	private void refreshJointSessionHistory() {
		SessionHistoryEntry currentJointSessionHistoryEntry = null;
		_currentJointSessionHistoryEntryPosition = -1;
		_historyEntries.clear();

		Set<SessionHistory> histories = getAllSessionHistories();
		ArrayList<SessionHistoryEntry> currentHistoryEntries = new ArrayList<SessionHistoryEntry>();
		
		for (SessionHistory history : histories) {
			SessionHistoryEntry currentHistoryEntry = history.getCurrentEntry();
			
			_historyEntries.addAll(history.getSessionHistoryEntries());
			
			if (currentHistoryEntry != null) {
				currentHistoryEntries.add(currentHistoryEntry);
			}
		}
				
		if (!currentHistoryEntries.isEmpty()) {
			Collections.sort(currentHistoryEntries, new VisitedComparator());
			currentJointSessionHistoryEntry = currentHistoryEntries.remove(currentHistoryEntries.size() - 1);
		}
		
		_historyEntries.removeAll(currentHistoryEntries);
		
		Collections.sort(_historyEntries, new VisitedComparator());
		
		if (currentJointSessionHistoryEntry != null) {
			_currentJointSessionHistoryEntryPosition = _historyEntries.indexOf(currentJointSessionHistoryEntry);
		}
	}
}

/**
 * JointSessionHistory.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingContextEvent;
import org.fit.cssbox.scriptbox.browser.BrowsingContextListener;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.history.JointSessionHistoryEvent.EventType;
import org.fit.cssbox.scriptbox.navigation.NavigationController;

/**
 * Class grouping history entries from all active document session history objects.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#joint-session-history">Joint session history</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JointSessionHistory {
	/*
	 * Compares session history entries by its visited time.
	 */
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
	
	/*
	 * History traversal task which ensures asynchronous traversing of the history.
	 */
	private class HistoryTraversalTask extends Task {
		private SessionHistoryEntry _specifiedEntry;
		private BrowsingContext _specifiedBrowsingContext;
		
		public HistoryTraversalTask(SessionHistoryEntry specifiedEntry, BrowsingContext specifiedBrowsingContext) {
			super(TaskSource.HISTORY_TRAVERSAL, specifiedBrowsingContext);
			
			_specifiedEntry = specifiedEntry;
			_specifiedBrowsingContext = specifiedBrowsingContext;
		}

		@Override
		public void execute() throws InterruptedException {
			// 1) If there is an ongoing attempt to navigate specified browsing context 
			// that has not yet matured, then cancel that attempt to navigate the browsing context.
			NavigationController controller = _specifiedBrowsingContext.getNavigationController();
			controller.cancelAllNonMaturedNavigationAttempts(_specifiedBrowsingContext);
			
			/*
			 * 2) If the specified browsing context's active document is not the same Document 
			 * as the Document of the specified entry, then run ...
			 * @see http://www.w3.org/html/wg/drafts/html/master/browsers.html#traverse-the-history-by-a-delta
			 */
			Html5DocumentImpl specifiedBrowsingContextDocument = _specifiedBrowsingContext.getActiveDocument();
			Html5DocumentImpl specifiedEntryDocument = _specifiedEntry.getDocument();
			if (specifiedBrowsingContextDocument != specifiedEntryDocument) {
				specifiedBrowsingContextDocument.fullyExitFullscreen();
				boolean shouldUnload = specifiedBrowsingContextDocument.promptToUnload();
				if (!shouldUnload) {
					return;
				} else {
					specifiedBrowsingContextDocument.unload(false);
				}
			}
			
			// 3) Traverse the history of the specified browsing context to the specified entry.
			SessionHistory.traverseHistory(_specifiedBrowsingContext, _specifiedEntry);
		}
	}
	
	/*
	 * Listener which ensures updating of the joint session history.
	 */
	private SessionHistoryListener _historyListener = new SessionHistoryListener() {
		@Override
		public void onHistoryEvent(SessionHistoryEvent event) {			
			switch (event.getEventType()) {
				case CURRENT_CHANGED:
					currentJointSessionHistoryEntry = event.getTarget();
					break;
				case TRAVERSED:
					SessionHistoryEntry fromEntry = event.getTarget();
					SessionHistoryEntry toEntry = event.getRelatedTarget();
					fireHistoryTravered(fromEntry, toEntry);
					break;
				default:
					break;
			}
			
			// FIXME: Not necessary to call refresh all the time
			refresh();
		}
	};
	
	/*
	 * Listener which ensures updating of the joint session history.
	 */
	private BrowsingContextListener _contextListener = new BrowsingContextListener() {
		
		@Override
		public void onBrowsingContextEvent(BrowsingContextEvent event) {
			BrowsingContext target = event.getTarget();
			SessionHistory sessionHistory = (target != null)? target.getSesstionHistory() : null;
			
			if (sessionHistory != null) {
				switch (event.getEventType()) {
					case INSERTED:
						sessionHistory.addListener(_historyListener);
						break;
					case REMOVED:
						sessionHistory.removeListener(_historyListener);
						break;
					default:
						break;
				}
			}
			
			// FIXME: Not necessary to call refresh all the time
			refresh();
		}
	};
	
	private BrowsingUnit _browsingUnit;
	private SessionHistoryEntry currentJointSessionHistoryEntry;
	private int _currentJointSessionHistoryEntryPosition;
	private ArrayList<SessionHistoryEntry> _historyEntries;
	private Set<JointSessionHistoryListener> listeners;
	
	/**
	 * Constructs joint session history for a given browsing unit.
	 * 
	 * @param browsingUnit Browsing unit that owns this joint session history.
	 */
	public JointSessionHistory(BrowsingUnit browsingUnit) {
		_browsingUnit = browsingUnit;
		
		listeners = new HashSet<JointSessionHistoryListener>();
		_historyEntries = new ArrayList<SessionHistoryEntry>();
		
		WindowBrowsingContext context = browsingUnit.getWindowBrowsingContext();
		SessionHistory sessionHistory = context.getSesstionHistory();
		
		context.addListener(_contextListener);
		sessionHistory.addListener(_historyListener);
		
		refresh();
	}
	
	/**
	 * Registers event listener to this joint session history.
	 * 
	 * @param listener New event listener to be registered.
	 */
	public void addListener(JointSessionHistoryListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes event listener to this joint session history.
	 * 
	 * @param listener Event listener to be removed.
	 */
	public void removeListener(JointSessionHistoryListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Returns current entry of this session history entry.
	 * 
	 * @return Current entry of this session history entry.
	 */
	public SessionHistoryEntry getCurrentEntry() {
		return currentJointSessionHistoryEntry;
	}
	
	/**
	 * Returns the position inside this joint session history.
	 * 
	 * @return Position inside this joint session history.
	 */
	public int getPosition() {
		return _currentJointSessionHistoryEntryPosition;
	}
	
	/**
	 * Returns the number of entries in the joint session history.
	 * 
	 * @return Number of entries in the joint session history.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#dom-history-length">History length</a>
	 */
	public int getLength() {
		return _historyEntries.size();
	}
	
	/**
	 * Goes back or forward the specified number of steps in this joint session history.
	 * 
	 * @param delta Number of traversal steps.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#traverse-the-history-by-a-delta">Traverse the history by a delta</a>
	 */
	public void traverse(int delta) {
		int destinationIndex = _currentJointSessionHistoryEntryPosition + delta;
		
		if (destinationIndex < 0 || destinationIndex >= _historyEntries.size()) {
			return;
		}
		
		SessionHistoryEntry specifiedEntry = _historyEntries.get(destinationIndex);
		BrowsingContext specifiedBrowsingContext = specifiedEntry.getSessionHistory().getBrowsingContext();
		
		// 5) If the specified browsing context's active document's unload a document 
		// algorithm is currently running, abort these steps.
		Html5DocumentImpl activeDocument = specifiedBrowsingContext.getActiveDocument();
		synchronized (activeDocument) { 
			if (activeDocument.isUnloadRunning()) {
				return;
			}
		}
		
		_browsingUnit.queueTask(new HistoryTraversalTask(specifiedEntry, specifiedBrowsingContext));
	}
	
	/**
	 * Refreshes this joint session history. This should be called if we change any 
	 * browsing context, or history session without firing corresponding events.
	 */
	public void refresh() {
		int wasPosition = _currentJointSessionHistoryEntryPosition;
		int wasLength = _historyEntries.size();

		_currentJointSessionHistoryEntryPosition = -1;
		_historyEntries.clear();

		Set<SessionHistory> histories = getAllSessionHistories();
		ArrayList<SessionHistoryEntry> currentHistoryEntries = new ArrayList<SessionHistoryEntry>();
		
		for (SessionHistory history : histories) {
			SessionHistoryEntry currentHistoryEntry = history.getCurrentEntry();
			
			List<SessionHistoryEntry> historyEntries = history.getSessionHistoryEntries();
			
			if (historyEntries != null) {
				_historyEntries.addAll(historyEntries);
			}
						
			if (currentHistoryEntry != null) {
				currentHistoryEntries.add(currentHistoryEntry);
			}
		}
				
		if (currentJointSessionHistoryEntry != null) {
			currentHistoryEntries.remove(currentJointSessionHistoryEntry);
		}
				
		_historyEntries.removeAll(currentHistoryEntries);
		
		Collections.sort(_historyEntries, new VisitedComparator());
		
		if (currentJointSessionHistoryEntry != null) {
			_currentJointSessionHistoryEntryPosition = _historyEntries.indexOf(currentJointSessionHistoryEntry);
		}
		
		if (_currentJointSessionHistoryEntryPosition == -1) {
			currentJointSessionHistoryEntry = null;
		}
		
		if (wasPosition != _currentJointSessionHistoryEntryPosition) {
			fireJointSessionHistoryEvent(EventType.POSITION_CHANGED);
		}
		
		if (wasLength != _historyEntries.size()) {
			fireJointSessionHistoryEvent(EventType.LENGTH_CHANGED);
		}
	}
	
	private Set<SessionHistory> getAllSessionHistories() {
		Set<SessionHistory> entries = new HashSet<SessionHistory>();
		
		BrowsingContext windowContext = _browsingUnit.getWindowBrowsingContext();
		SessionHistory windowSessionHistory = windowContext.getSesstionHistory();
		
		if (windowSessionHistory != null) {
			entries.add(windowSessionHistory);
		}
				
		Collection<BrowsingContext> nestedContexts = windowContext.getDescendantContexts();
		for (BrowsingContext context : nestedContexts) {
			SessionHistory sessionHistory = context.getSesstionHistory();
			if (sessionHistory != null) {
				entries.add(sessionHistory);
			}
		}
		
		return entries;
	}
	
	private void fireHistoryTravered(SessionHistoryEntry fromEntry, SessionHistoryEntry toEntry) {
		JointSessionHistoryEvent event = new JointSessionHistoryEvent(this, JointSessionHistoryEvent.EventType.TRAVERSED, toEntry, fromEntry);
		Set<JointSessionHistoryListener> listenersCopy = new HashSet<JointSessionHistoryListener>(listeners);
		
		for (JointSessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
	
	private void fireJointSessionHistoryEvent(EventType eventType) {
		JointSessionHistoryEvent event = new JointSessionHistoryEvent(this, eventType, null, null);
		Set<JointSessionHistoryListener> listenersCopy = new HashSet<JointSessionHistoryListener>(listeners);
		
		for (JointSessionHistoryListener listener : listenersCopy) {
			listener.onHistoryEvent(event);
		}
	}
}

package org.fit.cssbox.scriptbox.history;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;

public class HistoryTraversalTask extends Task {
	private SessionHistoryEntry _specifiedEntry;
	private BrowsingContext _specifiedBrowsingContext;
	
	public HistoryTraversalTask(SessionHistoryEntry specifiedEntry, BrowsingContext specifiedBrowsingContext) {
		super(TaskSource.HISTORY_TRAVERSAL, specifiedBrowsingContext);
		
		_specifiedEntry = specifiedEntry;
		_specifiedBrowsingContext = specifiedBrowsingContext;
	}

	@Override
	public void execute() {
		/*
		 * TODO: 
		 * 1) If there is an ongoing attempt to navigate specified browsing context 
		 * that has not yet matured (i.e. it has not passed the point of making its Document 
		 * the active document), then cancel that attempt to navigate the browsing context.
		 */
		
		/*
		 * TODO: 
		 * 2) If the specified browsing context's active document is not the same Document 
		 * as the Document of the specified entry, then run ...
		 * @see http://www.w3.org/html/wg/drafts/html/CR/browsers.html#traverse-the-history-by-a-delta
		 */
		
		HistoryTraversalHelper.traverseHistory(_specifiedBrowsingContext, _specifiedEntry);
	}
}

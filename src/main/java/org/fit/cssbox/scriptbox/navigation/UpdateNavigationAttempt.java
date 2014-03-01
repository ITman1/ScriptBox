package org.fit.cssbox.scriptbox.navigation;

import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;

public class UpdateNavigationAttempt extends NavigationAttempt {
	protected SessionHistoryEntry entry;
	
	public UpdateNavigationAttempt(NavigationController navigationController, SessionHistoryEntry entry) {
		super(navigationController, navigationController.getBrowsingContext(), entry.getURL(), false, false);

		this.entry = entry;
	}
	
	public SessionHistoryEntry getSessionHistoryEntry() {
		return entry;
	}

}

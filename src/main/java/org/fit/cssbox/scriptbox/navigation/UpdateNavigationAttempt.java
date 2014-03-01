package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;

public class UpdateNavigationAttempt extends NavigationAttempt {
	protected SessionHistoryEntry entry;
	
	public UpdateNavigationAttempt(NavigationController navigationController, SessionHistoryEntry entry) {
		super(navigationController, navigationController.getBrowsingContext(), entry.getURL());

		this.entry = entry;
	}

}

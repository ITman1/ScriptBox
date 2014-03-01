package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public class NewNavigationAttempt extends NavigationAttempt {

	public NewNavigationAttempt(NavigationController navigationController, BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride) {
		super(navigationController, sourceBrowsingContext, url, exceptionEnabled, explicitSelfNavigationOverride);
	}

}

package org.fit.cssbox.scriptbox.navigation;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public interface NavigationAttemptListener {
	public void onCancelled(NavigationAttempt attempt);
	public void onMatured(NavigationAttempt attempt);
	public void onCompleted(NavigationAttempt attempt);
	public void onEffectiveDestinationContextSelected(NavigationAttempt attempt, BrowsingContext context);
}

package org.fit.cssbox.scriptbox.navigation;

import org.fit.cssbox.scriptbox.resource.Resource;

public interface NavigationAttemptListener {
	public void onCancelled(NavigationAttempt attempt);
	public void onMatured(NavigationAttempt attempt);
	public void onResourceFetched(Resource resource);
}

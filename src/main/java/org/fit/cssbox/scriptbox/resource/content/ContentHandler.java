package org.fit.cssbox.scriptbox.resource.content;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.resource.Resource;

public abstract class ContentHandler {
	protected NavigationAttempt navigationAttempt;
	protected NavigationController navigationController;
	protected BrowsingContext context;
	
	public ContentHandler(NavigationAttempt navigationAttempt) {
		this.navigationAttempt = navigationAttempt;
		this.navigationController = navigationAttempt.getNavigationController();
		this.context = navigationController.getBrowsingContext();
	}
		
	public abstract void process(Resource resource);
}

package org.fit.cssbox.scriptbox.resource;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.navigation.NavigationController;

public abstract class ResourceHandler {
	protected NavigationController navigationController;
	protected BrowsingContext context;
	
	public ResourceHandler(NavigationController navigationController) {
		this.navigationController = navigationController;
		this.context = navigationController.getBrowsingContext();
	}
	
	public abstract void process(Resource resource);
}

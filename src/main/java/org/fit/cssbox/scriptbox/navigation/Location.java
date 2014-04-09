package org.fit.cssbox.scriptbox.navigation;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;

// TODO: https://dvcs.w3.org/hg/url/raw-file/tip/Overview.html#urlutils
public class Location {
	private NavigationController controller;
	private BrowsingContext context;
	
	public Location(BrowsingContext context) {
		this.context = context;
		this.controller = context.getNavigationController();
	}
	
	@ScriptFunction
	void assign(String url) {
		
	}
	
	@ScriptFunction
	void replace(String url) {
		
	}
	
	@ScriptFunction
	void reload() {
		
	}
	
	@Override
	public String toString() {
		return "[object Location]";
	}
}

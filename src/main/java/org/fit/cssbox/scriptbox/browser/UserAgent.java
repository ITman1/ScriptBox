package org.fit.cssbox.scriptbox.browser;

import java.net.URI;
import java.net.URL;
import java.util.Set;

public class UserAgent {
	public Set<BrowsingContext> getBrowsingContextsByName(String name) {
		return null;
	}
	
	public boolean scriptsSupported() {
		return true;
	}
	
	public boolean scriptsEnabled(URI page) {
		return true;
	}
	
	public boolean cookiesEnabled() {
		return true;
	}
	
	public BrowsingUnit createBrowsingUnit() {
		return new BrowsingUnit(this);
	}
}

package org.fit.cssbox.scriptbox.browser;

import java.net.URI;

public class UserAgent {
	public boolean scriptsSupported() {
		return true;
	}
	
	public boolean scriptsEnabled(URI page) {
		return true;
	}
	
	public boolean cookiesEnabled() {
		return true;
	}
	
	public boolean createBrowsingContext(URI page) {
		return true;
	}
}

package org.fit.cssbox.scriptbox.browser;

import java.net.URI;

public class WebBrowser extends Window {
	public boolean scriptsSupported() {
		return true;
	}
	
	public boolean scriptsEnabled(URI page) {
		return true;
	}
	
	public boolean cookiesEnabled() {
		return true;
	}
}

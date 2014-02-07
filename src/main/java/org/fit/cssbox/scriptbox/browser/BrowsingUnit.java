package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.events.EventLoop;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
	}
	
	public UserAgent getUserAgent() {
		return _userAgent;
	}
}

package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.events.EventLoop;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	private WindowBrowsingContext _windowBrowsingContext;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
	}
	
	public UserAgent getUserAgent() {
		return _userAgent;
	}
}

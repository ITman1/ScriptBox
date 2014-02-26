package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	private JointSessionHistory _jointSessionHistory;
	private WindowBrowsingContext _windowBrowsingContext;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
		_jointSessionHistory = new JointSessionHistory(this);
	}
	
	public UserAgent getUserAgent() {
		return _userAgent;
	}
	
	public WindowBrowsingContext getWindowBrowsingContext() {
		return _windowBrowsingContext;
	}
	
	public void queueTask(Task task) {
		_eventLoop.queueTask(task);
	}
	
	public EventLoop getEventLoop() {
		return _eventLoop;
	}
}

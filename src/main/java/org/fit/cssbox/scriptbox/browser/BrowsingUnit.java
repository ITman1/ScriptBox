package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;
import org.fit.cssbox.scriptbox.navigation.NavigationController;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	private JointSessionHistory _jointSessionHistory;
	private WindowBrowsingContext _windowBrowsingContext;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
		_jointSessionHistory = new JointSessionHistory(this);
		_eventLoop = new EventLoop();
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
	
	public JointSessionHistory getJointSessionHistory() {
		return _jointSessionHistory;
	}
	
	public void navigate(String address) {
		NavigationController navigationController = _windowBrowsingContext.getNavigationController();
		
		try {
			URL url = new URL(address);
			navigationController.navigate(_windowBrowsingContext, url, false, false, true);
		} catch (MalformedURLException e) {}
		
	}
}

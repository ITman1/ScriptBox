package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	private JointSessionHistory _jointSessionHistory;
	private WindowBrowsingContext _windowBrowsingContext;
	private ScriptSettingsStack _scriptSettingsStack;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
		_jointSessionHistory = new JointSessionHistory(this);
		_eventLoop = new EventLoop(this);
		_scriptSettingsStack = new ScriptSettingsStack();
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
	
	public ScriptSettingsStack getScriptSettingsStack() {
		return _scriptSettingsStack;
	}
		
	public JointSessionHistory getJointSessionHistory() {
		return _jointSessionHistory;
	}
	
	public void navigate(String address) throws MalformedURLException {
		URL url = new URL(address);
		navigate(url);
	}
	
	public void navigate(URL url) {
		NavigationController navigationController = _windowBrowsingContext.getNavigationController();
		navigationController.navigate(_windowBrowsingContext, url, false, false, true);		
	}
	
	public void destroy() {
		_windowBrowsingContext.discard();
		try {
			_eventLoop.abort(false);
		} catch (InterruptedException e) {
		}
	}
}

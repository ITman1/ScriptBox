package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.GlobalScriptCleanupJobs;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;

public class BrowsingUnit {
	private UserAgent _userAgent;
	private EventLoop _eventLoop;
	private JointSessionHistory _jointSessionHistory;
	private WindowBrowsingContext _windowBrowsingContext;
	private ScriptSettingsStack _scriptSettingsStack;
	private GlobalScriptCleanupJobs _globalScriptCleanupJobs;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
		_jointSessionHistory = new JointSessionHistory(this);
		_eventLoop = new EventLoop(this);
		_scriptSettingsStack = new ScriptSettingsStack();
		_globalScriptCleanupJobs = new GlobalScriptCleanupJobs();
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
	
	public GlobalScriptCleanupJobs getGlobalScriptCleanupJobs() {
		return _globalScriptCleanupJobs;
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
	
	public void destroy() {
		_windowBrowsingContext.destroyContext();
		try {
			_eventLoop.abort(false);
		} catch (InterruptedException e) {
		}
	}
}

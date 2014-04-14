/**
 * BrowsingUnit.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
	
	private boolean _discarded;
	
	public BrowsingUnit(UserAgent userAgent) {
		_userAgent = userAgent;
		
		_scriptSettingsStack = new ScriptSettingsStack();
		
		_windowBrowsingContext = new WindowBrowsingContext(this);
		
		_jointSessionHistory = new JointSessionHistory(this);
		_eventLoop = new EventLoop(this);
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
	
	public boolean isDiscarded() {
		return _discarded;
	}
	
	public void discard() {
		if (!_discarded) {
			_windowBrowsingContext.discard();
			try {
				_eventLoop.abort(false);
			} catch (InterruptedException e) {
			}
			
			_jointSessionHistory = null;
			_windowBrowsingContext = null;
			_scriptSettingsStack = null;
			
			_discarded = true;
		}
	}
}

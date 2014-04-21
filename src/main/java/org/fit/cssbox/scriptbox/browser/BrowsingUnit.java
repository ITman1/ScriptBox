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
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;

public class BrowsingUnit {
	public static class NoBarProp extends BarProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
	}
	
	public static class NoScrollBarsProp extends ScrollBarsProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
		
		@Override
		public void scroll(int xCoord, int yCoord) {
		}

		@Override
		public boolean scrollToFragment(String fragment) {
			return true;
		}

		@Override
		public int getScrollPositionX() {
			return -1;
		}

		@Override
		public int getScrollPositionY() {
			return -1;
		}
	}
	
	private static BarProp noBarAvailable = new NoBarProp();
	private static NoScrollBarsProp noScrollBarsAvailable = new NoScrollBarsProp();
	
	protected UserAgent _userAgent;
	protected EventLoop _eventLoop;
	protected JointSessionHistory _jointSessionHistory;
	protected WindowBrowsingContext _windowBrowsingContext;
	protected ScriptSettingsStack _scriptSettingsStack;
	
	protected boolean _discarded;
	
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
	
	public BarProp getMenubar() {
		return noBarAvailable;
	}

	public BarProp getPersonalbar() {
		return noBarAvailable;
	}

	public ScrollBarsProp getScrollbars() {
		return noScrollBarsAvailable;
	}

	public BarProp getStatusbar() {
		return noBarAvailable;
	}

	public BarProp getToolbar() {
		return noBarAvailable;
	}
	
	public BarProp getLocationbar() {
		return noBarAvailable;
	}
	
	public void showAlertDialog(String message) {
		
	}
	
	public boolean showConfirmDialog(String message) {
		return false;
	}
	
	public String showPromptDialog(String message, String defaultChoice) {
		return null;
	}
}

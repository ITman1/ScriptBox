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

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;

/**
 * Default class for creating custom browsing unit. 
 * 
 * Browsing unit collects all browsing contexts, has associated shared joint history,
 * provides to them the event loop and some additional UI interfaces. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#unit-of-related-browsing-contexts">Unit of related browsing contexts</a>
 */
public class BrowsingUnit {
	protected UserAgent userAgent;
	protected EventLoop eventLoop;
	protected JointSessionHistory jointSessionHistory;
	protected WindowBrowsingContext windowBrowsingContext;
	protected ScriptSettingsStack scriptSettingsStack;
	
	protected boolean _discarded;
	
	/**
	 * Constructs new browsing unit of the user agent.
	 * 
	 * @param userAgent User agent which owns this browsing unit.
	 */
	public BrowsingUnit(UserAgent userAgent) {
		this.userAgent = userAgent;
		
		this.eventLoop = new EventLoop(this);
		this.scriptSettingsStack = new ScriptSettingsStack();
		this.windowBrowsingContext = constructWindowBrowsingContext();
		
		// Has to be after windowBrowsingContext - because it registers event listeners above it
		this.jointSessionHistory = new JointSessionHistory(this);
	}
	
	/**
	 * Returns the user agent which owns and opened this browsing unit.
	 * 
	 * @return Opener user agent.
	 */
	public UserAgent getUserAgent() {
		return userAgent;
	}
	
	/**
	 * Opens new top-level auxiliary browsing context.
	 * 
	 * @param openerBrowsingContext Browsing context from which the auxiliary browsing context was created.
	 * @param name Name of the browsing context.
	 * @param createdByScript Specifies whether this context has been created by a script or not.
	 * @return New top-level auxiliary browsing context
	 */
	public AuxiliaryBrowsingContext openAuxiliaryBrowsingContext(BrowsingContext openerBrowsingContext, String name, boolean createdByScript) {
		return new AuxiliaryBrowsingContext(this, openerBrowsingContext, name, createdByScript);
	}
	
	/**
	 * Opens new IFRAME browsing context.
	 * 
	 * @param container Container browsing context.
	 * @param iframeElement IFRAME element which represents the container of the new browsing context.
	 * @return New nested IFRAME browsing context.
	 */
	public IFrameBrowsingContext openIFrameBrowsingContext(IFrameContainerBrowsingContext container, Html5IFrameElementImpl iframeElement) {
		return new IFrameBrowsingContext(container, iframeElement);
	}
	
	/**
	 * Constructs top level window browsing context.
	 * 
	 * @return Top level window browsing context.
	 */
	protected WindowBrowsingContext constructWindowBrowsingContext() {
		return new WindowBrowsingContext(this);
	}
	
	/**
	 * Returns main top-level browsing context which is the root for all browsing contexts.
	 * 
	 * @return Top-level browsing context for this browsing unit.
	 */
	public WindowBrowsingContext getWindowBrowsingContext() {
		return windowBrowsingContext;
	}
	
	/**
	 * Queues task into queue.
	 * 
	 * @param task New task to be queued into event loop.
	 */
	public void queueTask(Task task) {
		eventLoop.queueTask(task);
	}
	
	/**
	 * Returns associated event loop.
	 * 
	 * @return Associated event loop.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#event-loop">The event loop</a>
	 */
	public EventLoop getEventLoop() {
		return eventLoop;
	}
	
	/**
	 * Returns associated script settings stack.
	 * 
	 * @return Associated script settings stack
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#stack-of-script-settings-objects">Stack of script settings objects</a>
	 */
	public ScriptSettingsStack getScriptSettingsStack() {
		return scriptSettingsStack;
	}
		
	/**
	 * Returns associated joint session history.
	 * 
	 * @return Associated hoint session history
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#joint-session-history">Joint session history</a>
	 */
	public JointSessionHistory getJointSessionHistory() {
		return jointSessionHistory;
	}
	
	/**
	 * Navigates window browsing context to passed address.
	 * 
	 * @param address Address where to navigate window browsing context.
	 * @throws MalformedURLException MalformedURLException
	 * @see #navigate(URL)
	 */
	public void navigate(String address) throws MalformedURLException {
		URL url = new URL(address);
		navigate(url);
	}
	
	/**
	 * Navigates window browsing context to passed address.
	 * 
	 * @param url Address where to navigate window browsing context.
	 */
	public void navigate(URL url) {
		NavigationController navigationController = windowBrowsingContext.getNavigationController();
		navigationController.navigate(windowBrowsingContext, url, false, false, true);		
	}
	
	/**
	 * Test whether is this browsing unit discarded.
	 * 
	 * @return True if is this browsing unit discarded, otherwise false.
	 */
	public boolean isDiscarded() {
		return _discarded;
	}
	
	/**
	 * Discards this browsing unit.
	 */
	public void discard() {
		if (!_discarded) {
			windowBrowsingContext.discard();
			try {
				eventLoop.abort(false);
			} catch (InterruptedException e) {
			}
			
			jointSessionHistory = null;
			windowBrowsingContext = null;
			scriptSettingsStack = null;
			
			_discarded = true;
		}
	}
	
	/**
	 * Shows alert dialog with given message.
	 * 
	 * @param message Message to be displayed.
	 */
	public void showAlertDialog(String message) {
		
	}
	
	/**
	 * Shows confirm dialog with given message.
	 * 
	 * @param message Message to be displayed.
	 * @return True if dialog was submitted with OK option, otherwise false.
	 */
	public boolean showConfirmDialog(String message) {
		return false;
	}
	
	/**
	 * Shows prompt dialog with given message.
	 * 
	 * @param message Message to be displayed.
	 * @param defaultChoice Choice to be returned if user canceled the prompt.
	 * @return Value which was typed and submitted by user.
	 */
	public String showPromptDialog(String message, String defaultChoice) {
		return null;
	}
}

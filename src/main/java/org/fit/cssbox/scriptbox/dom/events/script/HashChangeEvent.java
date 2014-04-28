package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

/**
 * Represents pure script visible hash change event class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#hashchangeevent">Hash change event</a>
 */
public class HashChangeEvent extends Event {
	protected String oldURL;
	protected String newURL;
	
	public HashChangeEvent() {}
	
	public HashChangeEvent(boolean isTrusted, EventTarget targetOverride) {
		super(isTrusted, targetOverride);
	}
	
	/**
	 * Returns old URL before hash changed.
	 * 
	 * @return Old URL before hash changed.
	 */
	@ScriptGetter
	public String getOldURL() {
		return oldURL;
	}
	
	/**
	 * Returns new URL after hash changed.
	 * 
	 * @return New URL after hash changed.
	 */
	@ScriptGetter
	public String getNewURL() {
		return newURL;
	}

	/**
	 * Initializes this event.
	 * 
	 * @param eventTypeArg event type
	 * @param canBubbleArg bubbles flag
	 * @param cancelableArg cancelable flag
	 * @param oldURL old URL before hash changed
	 * @param newURL new URL after hash changed
	 */
	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, String oldURL, String newURL) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
		
		this.oldURL = oldURL;
		this.oldURL = oldURL;
	}
	
	@ScriptFunction
	@Override
	public String toString() {
		return "[object HashChangeEvent]";
	}
}

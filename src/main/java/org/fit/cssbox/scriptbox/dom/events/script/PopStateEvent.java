package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.history.StateObject;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

/**
 * Represents pure script visible pop state event class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#event-popstate">Pop state event</a>
 */
public class PopStateEvent extends Event {
	protected StateObject state;
	
	public PopStateEvent() {}
	
	public PopStateEvent(boolean isTrusted, EventTarget targetOverride) {
		super(isTrusted, targetOverride);
	}
	
	/**
	 * Returns state which was popped while traversing history.
	 * 
	 * @return State popped while traversing history.
	 */
	@ScriptGetter
	public StateObject getState() {
		return state;
	}

	/**
	 * Initializes this event.
	 * 
	 * @param eventTypeArg event type
	 * @param canBubbleArg bubbles flag
	 * @param cancelableArg cancelable flag
	 * @param state state which event carries
	 */
	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, StateObject state) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
		
		this.state = state;
	}
	
	@ScriptFunction
	@Override
	public String toString() {
		return "[object PopStateEvent]";
	}
}

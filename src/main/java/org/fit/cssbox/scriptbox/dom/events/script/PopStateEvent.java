package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.history.StateObject;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class PopStateEvent extends TrustedEvent {
	protected StateObject state;
	
	@ScriptGetter
	public StateObject getState() {
		return state;
	}

	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, boolean isTrusted, EventTarget targetOverride, StateObject state) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg, isTrusted, targetOverride);
		
		this.state = state;
	}
	
	@Override
	public String toString() {
		return "[object PopStateEvent]";
	}
}

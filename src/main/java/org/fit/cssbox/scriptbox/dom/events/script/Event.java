package org.fit.cssbox.scriptbox.dom.events.script;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class Event extends EventImpl {
	@ScriptGetter
	@Override
	public String getType() {
		return super.type;
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getTarget() {
		return super.target;
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getCurrentTarget() {
		return super.currentTarget;
	}
	
	@ScriptGetter
	@Override
	public short getEventPhase() {
		return super.eventPhase;
	}
	
	@ScriptGetter
	@Override
	public boolean getBubbles() {
		return super.bubbles;
	}
	
	@ScriptGetter
	@Override
	public boolean getCancelable() {
		return super.cancelable;
	}
	
	@ScriptGetter
	@Override
	public long getTimeStamp() {
		return super.getTimeStamp();
	}
	
	@ScriptFunction
	@Override
	public void stopPropagation() {
		super.stopPropagation();
	}
	
	@ScriptFunction
	@Override
	public void preventDefault() {
		super.preventDefault();
	}
	
	@ScriptFunction
	@Override
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
	}
	
	@Override
	public String toString() {
		return "[object Event]";
	}
}

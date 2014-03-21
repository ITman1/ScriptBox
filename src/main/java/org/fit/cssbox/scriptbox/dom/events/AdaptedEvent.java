package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class AdaptedEvent<EventTypeImpl extends EventImpl> {
	/* TODO: Final fields are not supported yet by engine */
	final static short CAPTURING_PHASE  = 1;
	final static short AT_TARGET        = 2;
	final static short BUBBLING_PHASE   = 3;

	protected EventTypeImpl eventImpl;
	
	public AdaptedEvent(EventTypeImpl eventImpl) {
		this.eventImpl = eventImpl;
	}
	
	@ScriptGetter
	public String getType() {
		return eventImpl.type;
	}
	
	@ScriptGetter
	public org.w3c.dom.events.EventTarget getTarget() {
		return eventImpl.target;
	}
	
	@ScriptGetter
	public org.w3c.dom.events.EventTarget getCurrentTarget() {
		return eventImpl.currentTarget;
	}
	
	@ScriptGetter
	public short getEventPhase() {
		return eventImpl.eventPhase;
	}
	
	@ScriptGetter
	public boolean getBubbles() {
		return eventImpl.bubbles;
	}
	
	@ScriptGetter
	public boolean getCancelable() {
		return eventImpl.cancelable;
	}
	
	@ScriptGetter
	public long getTimeStamp() {
		return eventImpl.getTimeStamp();
	}
	
	@ScriptFunction
	public void stopPropagation() {
		eventImpl.stopPropagation();
	}
	
	@ScriptFunction
	public void preventDefault() {
		eventImpl.preventDefault();
	}
	
	@ScriptFunction
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
		eventImpl.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
	}
	
	@Override
	public String toString() {
		return "[object Event]";
	}
	
	/* TODO?:
	// Introduced in DOM Level 3:
	readonly attribute DOMString       namespaceURI;
	// Introduced in DOM Level 3:
	boolean            isCustom();
	// Introduced in DOM Level 3:
	void               stopImmediatePropagation();
	// Introduced in DOM Level 3:
	boolean            isDefaultPrevented();
	// Introduced in DOM Level 3:
	void               initEventNS(in DOMString namespaceURIArg, 
	                               in DOMString eventTypeArg, 
	                               in boolean canBubbleArg, 
	                               in boolean cancelableArg);
	                               */
}

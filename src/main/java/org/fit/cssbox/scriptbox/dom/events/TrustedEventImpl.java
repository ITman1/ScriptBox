package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.EventImpl;

public class TrustedEventImpl extends EventImpl {
	protected boolean isTrusted;
	protected EventTarget targetOverride;
	
	public boolean getIsTrusted() {
		return isTrusted;
	}
	
	public EventTarget getTargetOverride() {
		return targetOverride;
	}

	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, boolean isTrusted, EventTarget targetOverride) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
		
		this.isTrusted = isTrusted;
		this.targetOverride = targetOverride;
	}
}

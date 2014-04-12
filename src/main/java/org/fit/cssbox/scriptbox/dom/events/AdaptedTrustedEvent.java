package org.fit.cssbox.scriptbox.dom.events;

import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class AdaptedTrustedEvent extends AdaptedEvent<TrustedEventImpl> {

	public AdaptedTrustedEvent(TrustedEventImpl eventImpl) {
		super(eventImpl);
	}
	
	@ScriptGetter
	public boolean getIsTrusted() {
		return eventImpl.isTrusted;
	}
	
	@ScriptGetter
	@Override
	public String getType() {
		return super.getType();
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getTarget() {
		return super.getTarget();
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getCurrentTarget() {
		return super.getCurrentTarget();
	}
	
	@ScriptGetter
	@Override
	public short getEventPhase() {
		return super.getEventPhase();
	}
	
	@ScriptGetter
	@Override
	public boolean getBubbles() {
		return super.getBubbles();
	}
	
	@ScriptGetter
	@Override
	public boolean getCancelable() {
		return super.getCancelable();
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

}

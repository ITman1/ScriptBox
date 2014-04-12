/**
 * AdaptedEvent.java
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

package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.script.Wrapper;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.w3c.dom.events.Event;

public class AdaptedEvent<EventTypeImpl extends EventImpl> implements Event, Wrapper<EventTypeImpl> {
	/* TODO: Final fields are not supported yet by engine */
	final static short CAPTURING_PHASE  = 1;
	final static short AT_TARGET        = 2;
	final static short BUBBLING_PHASE   = 3;

	protected EventTypeImpl eventImpl;
	
	public AdaptedEvent(EventTypeImpl eventImpl) {
		this.eventImpl = eventImpl;
	}

	@ScriptGetter
	@Override
	public String getType() {
		return eventImpl.type;
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getTarget() {
		return eventImpl.target;
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getCurrentTarget() {
		return eventImpl.currentTarget;
	}
	
	@ScriptGetter
	@Override
	public short getEventPhase() {
		return eventImpl.eventPhase;
	}
	
	@ScriptGetter
	@Override
	public boolean getBubbles() {
		return eventImpl.bubbles;
	}
	
	@ScriptGetter
	@Override
	public boolean getCancelable() {
		return eventImpl.cancelable;
	}
	
	@ScriptGetter
	@Override
	public long getTimeStamp() {
		return eventImpl.getTimeStamp();
	}
	
	@ScriptFunction
	@Override
	public void stopPropagation() {
		eventImpl.stopPropagation();
	}
	
	@ScriptFunction
	@Override
	public void preventDefault() {
		eventImpl.preventDefault();
	}
	
	@ScriptFunction
	@Override
	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg) {
		eventImpl.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
	}
	
	@Override
	public String toString() {
		return "[object Event]";
	}

	@Override
	public EventTypeImpl unwrap() {
		return eventImpl;
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

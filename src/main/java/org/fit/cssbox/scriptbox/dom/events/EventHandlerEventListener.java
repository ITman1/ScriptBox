package org.fit.cssbox.scriptbox.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class EventHandlerEventListener implements EventListener {
	protected EventHandler handler;
	protected EventTarget target;
	protected String eventType;
	
	public EventHandlerEventListener(EventTarget target, String eventType) {
		this.target = target;
		this.eventType = eventType;
	}
	
	public EventHandler getEventHandler() {
		return handler;
	}
	
	public void setEventHandler(EventHandler handler) {
		if (handler != null) {
			this.handler = handler;
			target.addEventListener(eventType, this);
		} else {
			deleteEventHandler();
		}
	}
	
	public void deleteEventHandler() {
		handler = null;
		target.removeEventListener(eventType, this);
	}

	@Override
	public void handleEvent(Event evt) {
		if (handler != null) {
			handler.handleEvent(evt);
		}
	}
}

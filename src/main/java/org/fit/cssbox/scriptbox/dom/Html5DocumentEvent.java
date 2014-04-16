package org.fit.cssbox.scriptbox.dom;

import java.util.EventObject;

public class Html5DocumentEvent extends EventObject {

	private static final long serialVersionUID = -3081482621112947040L;

	public enum EventType {
		ADDRESS_CHANGED
	};
	
	private EventType eventType;
	
	public Html5DocumentEvent(Html5DocumentImpl document, EventType eventType) {
		super(document);

		switch (eventType) {
			case ADDRESS_CHANGED:
				this.eventType = eventType;
		}
	}
	
	public EventType getEventType() {
		return eventType;
	}
}

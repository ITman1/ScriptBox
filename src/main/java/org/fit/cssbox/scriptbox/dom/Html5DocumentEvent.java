package org.fit.cssbox.scriptbox.dom;

import java.util.EventObject;

/**
 * Event class for events which are fired by Html5DocumentImpl.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class Html5DocumentEvent extends EventObject {

	private static final long serialVersionUID = -3081482621112947040L;

	public enum EventType {
		ADDRESS_CHANGED
	};
	
	private EventType eventType;
	
	/**
	 * Constructs event for Html5DocumentImpl documents.
	 * 
	 * @param document Document which fires the event.
	 * @param eventType Event type.
	 */
	public Html5DocumentEvent(Html5DocumentImpl document, EventType eventType) {
		super(document);

		switch (eventType) {
			case ADDRESS_CHANGED:
				this.eventType = eventType;
		}
	}
	
	/**
	 * Returns event type.
	 * 
	 * @return Event type.
	 */
	public EventType getEventType() {
		return eventType;
	}
}

package org.fit.cssbox.scriptbox.history;

import java.util.EventObject;

public class SessionHistoryEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	public enum EventType {
		TRAVERSED,
		INSERTED,
		REMOVED,
		DESTROYED
	};
	
	private EventType eventType;
	private SessionHistoryEntry target;
	private SessionHistoryEntry relatedTarget;
	
	public SessionHistoryEvent(SessionHistory sessionHistory, EventType eventType, SessionHistoryEntry target, SessionHistoryEntry relatedTarget) {
		super(sessionHistory);
		
		switch (eventType) {
			case TRAVERSED:
				this.relatedTarget = relatedTarget;
			case INSERTED:
			case REMOVED:
				this.target = target;
			case DESTROYED:
				this.eventType = eventType;
		}
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public SessionHistoryEntry getTarget() {
		return target;
	}

	public SessionHistoryEntry getRelatedTarget() {
		return relatedTarget;
	}

}

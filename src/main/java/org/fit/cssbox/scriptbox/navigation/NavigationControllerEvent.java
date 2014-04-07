package org.fit.cssbox.scriptbox.navigation;

import java.util.EventObject;

public class NavigationControllerEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	public enum EventType {
		NAVIGATION_CANCELLED,
		NAVIGATION_COMPLETED,
		NAVIGATION_NEW,
		DESTROYED
	};
	
	private EventType eventType;
	private NavigationAttempt attempt;
	
	public NavigationControllerEvent(NavigationController navigationController, EventType eventType, NavigationAttempt attempt) {
		super(navigationController);
		
		switch (eventType) {
			case NAVIGATION_CANCELLED:
			case NAVIGATION_COMPLETED:
			case NAVIGATION_NEW:
				this.attempt = attempt;
			case DESTROYED:
				this.eventType = eventType;
		}
	}
	
	public EventType getEventType() {
		return eventType;
	}

	public NavigationAttempt getNavigationAttempt() {
		return attempt;
	}
}

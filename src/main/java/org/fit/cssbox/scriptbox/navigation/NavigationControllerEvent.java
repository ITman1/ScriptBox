/**
 * NavigationControllerEvent.java
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

package org.fit.cssbox.scriptbox.navigation;

import java.util.EventObject;

/**
 * Event class for events which are fired by navigation controller.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class NavigationControllerEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	/**
	 * Navigation controller events.
	 * 
	 * @author Radim Loskot
	 * @version 0.9
	 * @since 0.9 - 21.4.2014
	 */
	public enum EventType {
		NAVIGATION_CANCELLED,	/** Navigation cancelled event */
		NAVIGATION_COMPLETED,	/** Navigation matured event */
		NAVIGATION_MATURED,		/** Navigation matured event */
		NAVIGATION_NEW,			/** New navigation in progress event */
		DESTROYED				/** Navigation controller destroyed */
	};
	
	private EventType eventType;
	private NavigationAttempt attempt;
	
	/**
	 * Constructs event for session history with the specified type and target.
	 * 
	 * @param navigationController Navigation controller which fires the event.
	 * @param eventType Event type.
	 * @param attempt Attempt which relates to this event.
	 */
	public NavigationControllerEvent(NavigationController navigationController, EventType eventType, NavigationAttempt attempt) {
		super(navigationController);
		
		switch (eventType) {
			case NAVIGATION_CANCELLED:
			case NAVIGATION_COMPLETED:
			case NAVIGATION_NEW:
			case NAVIGATION_MATURED:
				this.attempt = attempt;
			case DESTROYED:
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

	/**
	 * Returns navigation attempt that relates to this event.
	 * 
	 * @return Navigation attempt that relates to this event
	 */
	public NavigationAttempt getNavigationAttempt() {
		return attempt;
	}
}

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

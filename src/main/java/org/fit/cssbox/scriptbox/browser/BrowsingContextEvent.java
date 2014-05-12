/**
 * SessionHistoryEvent.java
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

package org.fit.cssbox.scriptbox.browser;

import java.util.EventObject;

/**
 * Event class for events which are fired by browsing contexts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class BrowsingContextEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	/**
	 * Browsing context events.
	 * 
	 * @author Radim Loskot
	 * @version 0.9
	 * @since 0.9 - 21.4.2014
	 */
	public enum EventType {
		INSERTED,  /** New browsing context is inserted. */
		REMOVED,   /** Browsing context is removed */
		DESTROYED  /** This browsing context has been destroyed */
	};
	
	private EventType eventType;
	private BrowsingContext target;
	
	/**
	 * Constructs event for browsing context with the specified type and target.
	 * 
	 * @param source Browsing context which fires the event.
	 * @param eventType Event type.
	 * @param target Target browsing context which might be appended to this event.
	 */
	public BrowsingContextEvent(BrowsingContext source, EventType eventType, BrowsingContext target) {
		super(source);
		
		switch (eventType) {
			case INSERTED:
			case REMOVED:
				this.target = target;
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
	 * Returns target to which relates this event.
	 * 
	 * @return Target to which relates this event.
	 */
	public BrowsingContext getTarget() {
		return target;
	}
}

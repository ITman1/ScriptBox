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

package org.fit.cssbox.scriptbox.history;

import java.util.EventObject;

/**
 * Event class for events which are fired by joint session history.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JointSessionHistoryEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	/**
	 * Joint session history events.
	 * 
	 * @author Radim Loskot
	 * @version 0.9
	 * @since 0.9 - 21.4.2014
	 */
	public enum EventType {
		POSITION_CHANGED,		/** Current position of the joint session history has changed */
		LENGTH_CHANGED,			/** Length of the joint session history has changed. */
		TRAVERSED				/** Joint session history has traversed to new entry */
	};
	
	private EventType eventType;
	private SessionHistoryEntry target;
	private SessionHistoryEntry relatedTarget;
	
	/**
	 * Constructs event for joint session history with the specified type and target.
	 * 
	 * @param jointSessionHistory Joint session history which fires the event.
	 * @param eventType Event type.
	 * @param target Target browsing context which might be appended to this event.
	 * @param relatedTarget Additional target which is set when event relates to two targets.
	 */
	public JointSessionHistoryEvent(JointSessionHistory jointSessionHistory, EventType eventType, SessionHistoryEntry target, SessionHistoryEntry relatedTarget) {
		super(jointSessionHistory);

		switch (eventType) {
			case TRAVERSED:
				this.relatedTarget = relatedTarget;
				this.target = target;
			case POSITION_CHANGED:
			case LENGTH_CHANGED:
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
	public SessionHistoryEntry getTarget() {
		return target;
	}

	/**
	 * Returns additional target which is set when event relates to two targets.
	 * 
	 * @return Additional target which is set when event relates to two targets.
	 */
	public SessionHistoryEntry getRelatedTarget() {
		return relatedTarget;
	}

}

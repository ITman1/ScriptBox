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

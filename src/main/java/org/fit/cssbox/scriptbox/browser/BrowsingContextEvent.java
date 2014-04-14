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

public class BrowsingContextEvent extends EventObject {

	private static final long serialVersionUID = -879099242022721983L;

	public enum EventType {
		INSERTED,
		REMOVED,
		DESTROYED
	};
	
	private EventType eventType;
	private BrowsingContext target;
	
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
	
	public EventType getEventType() {
		return eventType;
	}

	public BrowsingContext getTarget() {
		return target;
	}
}

/**
 * EventHandlerEventListener.java
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

package org.fit.cssbox.scriptbox.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class EventHandlerEventListener implements EventListener {
	protected EventHandler handler;
	protected EventTarget target;
	protected String eventType;
	
	public EventHandlerEventListener(EventTarget target, String eventType) {
		this.target = target;
		this.eventType = eventType;
	}
	
	public EventHandler getEventHandler() {
		return handler;
	}
	
	public void setEventHandler(EventHandler handler) {
		if (handler != null) {
			this.handler = handler;
			target.addEventListener(eventType, this);
		} else {
			deleteEventHandler();
		}
	}
	
	public void deleteEventHandler() {
		handler = null;
		target.removeEventListener(eventType, this);
	}

	@Override
	public void handleEvent(Event evt) {
		if (handler != null) {
			handler.handleEvent(evt);
		}
	}
}

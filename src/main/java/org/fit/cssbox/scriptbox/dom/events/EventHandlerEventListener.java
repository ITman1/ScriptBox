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

/**
 * Class ensuring automatically registering of the event listener above 
 * specific event target if an event handler is set to non-null and
 * removing of the event listener if event handler is null.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#eventhandler">Event handler</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class EventHandlerEventListener implements EventListener {
	protected EventHandler handler;
	protected EventTarget target;
	protected String eventType;
	
	/**
	 * Creates new event listener which will be automatically binded into
	 * specific event target, when event event handler is set.
	 * 
	 * @param target Event target where to register this event listener.
	 * @param eventType Event type of which will be registered event listener.
	 */
	public EventHandlerEventListener(EventTarget target, String eventType) {
		this.target = target;
		this.eventType = eventType;
	}
	
	/**
	 * Returns event handler.
	 * @return Event handler if there is any, otherwise null.
	 */
	public EventHandler getEventHandler() {
		return handler;
	}
	
	/**
	 * Sets new event handler on passing non-null value and registers this
	 * event listener for the corresponding event target. If null is passed
	 * removes this event listener from the target.
	 * 
	 * @param handler Event handler to be used for handling event of corresponding type.
	 */
	public void setEventHandler(EventHandler handler) {
		if (handler != null) {
			this.handler = handler;
			target.addEventListener(eventType, this);
		} else {
			deleteEventHandler();
		}
	}
	
	/**
	 * Removes event handler and unregisters this event listener above corresponding target.
	 */
	public void deleteEventHandler() {
		handler = null;
		target.removeEventListener(eventType, this);
	}

	/**
	 * Calls event handler if there is any associated with this object.
	 */
	@Override
	public void handleEvent(Event evt) {
		if (handler != null) {
			handler.handleEvent(evt);
		}
	}
}

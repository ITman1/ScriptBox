/**
 * EventTarget.java
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
 * Interface redefining an already existing W3C interface. It removes throws EventException
 * statement from {@link org.w3c.dom.events.EventTarget#dispatchEvent(Event)} method and 
 * defines duplicit methods to enable optional arguments.
 * 
 * @see <a href="http://www.w3.org/TR/DOM-Level-3-Events/#interface-EventTarget">Event target</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface EventTarget extends org.w3c.dom.events.EventTarget {
	public void addEventListener(String type, EventListener listener, boolean useCapture);
	public void removeEventListener(String type, EventListener listener, boolean useCapture);
	public void addEventListener(String type, EventListener listener);
	public void removeEventListener(String type, EventListener listener);
	public boolean dispatchEvent(Event evt);
}

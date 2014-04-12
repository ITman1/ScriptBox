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

/* Does not throw EventException as in DOM 2 
 * it has also old EventTarget API, for using in DOM */
public interface EventTarget extends org.w3c.dom.events.EventTarget {
	public void addEventListener(String type, EventListener listener, boolean useCapture);
	public void removeEventListener(String type, EventListener listener, boolean useCapture);
	public void addEventListener(String type, EventListener listener);
	public void removeEventListener(String type, EventListener listener);
	public boolean dispatchEvent(Event evt);
}

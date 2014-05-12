/**
 * DispatcherTask.java
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

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.w3c.dom.events.Event;

/**
 * Task which is used for dispatching DOM events asynchronously.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DispatcherTask extends Task {
	protected org.w3c.dom.events.EventTarget target;
	protected Event event;
	
	/**
	 * Constructs new dispatcher task.
	 * 
	 * @param document Document to which is associated this DOM event.
	 * @param target Target object where event has occurred.
	 * @param event Event which should be dispatched at the given target.
	 */
	public DispatcherTask(Html5DocumentImpl document, org.w3c.dom.events.EventTarget target, Event event) {
		super(TaskSource.USER_INTERACTION, document);
		
		this.target = target;
		this.event = event;
	}

	@Override
	public void execute() throws TaskAbortedException, InterruptedException {
		target.dispatchEvent(event);
	}

}

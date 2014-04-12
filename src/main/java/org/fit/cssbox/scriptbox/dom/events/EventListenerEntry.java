/**
 * EventListenerEntry.java
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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.w3c.dom.events.EventListener;

public class EventListenerEntry {
	public EventListener listener;
	public boolean useCapture;
	
	public EventListenerEntry(EventListener listener, boolean useCapture) {
		this.listener = listener;
		this.useCapture = useCapture;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(46, 12).
			append(listener).
			append(useCapture).
			toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof EventListenerEntry))
			return false;

		EventListenerEntry rhs = (EventListenerEntry) obj;
		return new EqualsBuilder().
			append(listener, rhs.listener).
			append(useCapture, rhs.useCapture).
			isEquals();
	}
}

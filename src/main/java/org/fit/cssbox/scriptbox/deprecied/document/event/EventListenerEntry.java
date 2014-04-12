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

package org.fit.cssbox.scriptbox.deprecied.document.event;

import org.w3c.dom.events.EventListener;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class EventListenerEntry {
	private String type;
	private EventListener listener;
	private boolean useCapture;
	
	public EventListenerEntry(String type, EventListener listener, boolean useCapture) {
		this.type = type;
		this.listener = listener;
		this.useCapture = useCapture;
	}

	public String getType() {
		return type;
	}

	public EventListener getListener() {
		return listener;
	}

	public boolean isUseCapture() {
		return useCapture;
	}
	
	public int hashCode() {
        return new HashCodeBuilder(12, 5).
            append(type).
            append(listener).
            toHashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof EventListenerEntry))
            return false;

        EventListenerEntry rhs = (EventListenerEntry) obj;
        return new EqualsBuilder().
            append(type, rhs.type).
            append(listener, rhs.listener).
            isEquals();
    }
}

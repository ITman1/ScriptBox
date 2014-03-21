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
		return new HashCodeBuilder(45, 12).
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

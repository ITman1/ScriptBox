package org.fit.cssbox.scriptbox.document.event;

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

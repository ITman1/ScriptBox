package org.fit.cssbox.scriptbox.dom.events;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

/* Does not throw EventException as in DOM 2 */
public interface EventTarget {
	public void addEventListener(String type, EventListener listener, boolean useCapture);
	public void removeEventListener(String type, EventListener listener, boolean useCapture);
	public boolean dispatchEvent(Event evt);
}

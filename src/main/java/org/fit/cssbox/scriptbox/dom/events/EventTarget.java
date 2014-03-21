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

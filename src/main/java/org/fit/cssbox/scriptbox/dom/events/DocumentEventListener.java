package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class DocumentEventListener implements EventListener {
	protected Html5DocumentImpl document;
	protected Window window;
	protected EventListener listener;
	
	public DocumentEventListener(Html5DocumentImpl document, EventListener listener) {
		this.document = document;
		this.window = document.getWindow();
		this.listener = listener;
	}

	@Override
	public void handleEvent(Event evt) {
		if (evt instanceof EventImpl) {
			return;
		}
		EventImpl event = (EventImpl)evt;
		org.w3c.dom.events.EventTarget currTarget = event.currentTarget;
		
		if (event.eventPhase == Event.CAPTURING_PHASE && currTarget.equals(document)) {
			event.currentTarget = window;
			window.dispatchEventFromDocument(event);
			event.currentTarget = currTarget;
			if (!event.stopPropagation) {
				listener.handleEvent(evt);
			}
		} else if (event.eventPhase == Event.BUBBLING_PHASE && currTarget.equals(document)) {
			listener.handleEvent(evt);
			event.currentTarget = window;
			if (!event.stopPropagation) {
				window.dispatchEventFromDocument(event);
			}
		} else {
			listener.handleEvent(evt);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		return listener.equals(obj);
	}
	
	@Override
	public int hashCode() {
		return listener.hashCode();
	}
}

package org.fit.cssbox.scriptbox.dom;

import java.util.EventListener;

/**
 * Represents interface for creating event listeners above Html5 documents.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see Html5DocumentImpl
 */
public interface Html5DocumentEventListener extends EventListener {
	public void onDocumentEvent(Html5DocumentEvent event);
}

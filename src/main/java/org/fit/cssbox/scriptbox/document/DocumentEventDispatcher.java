package org.fit.cssbox.scriptbox.document;

import java.util.HashSet;
import java.util.Set;

import org.w3c.dom.Node;

public class DocumentEventDispatcher {
	private Set<DocumentEventListener> listeners;
	
	public DocumentEventDispatcher() {
		listeners = new HashSet<DocumentEventListener>();
	}
	
	public void addDocumentEventListener(DocumentEventListener listener) {
		listeners.add(listener);
	}
	
	public void removeDocumentEventListener(DocumentEventListener listener) {
		listeners.remove(listener);
	}
	
	public void fireNodeLoaded(Node node) {
		for (DocumentEventListener listener : listeners) {
			listener.nodeLoaded(node);
		}
	}
	
	public void fireNodeInserted(Node node) {
		for (DocumentEventListener listener : listeners) {
			listener.nodeInserted(node);
		}
	}
	
	public void fireNodeRemoved(Node node) {
		for (DocumentEventListener listener : listeners) {
			listener.nodeRemoved(node);
		}
	}
	
	public void fireNodeCreated(Node node) {
		for (DocumentEventListener listener : listeners) {
			listener.nodeCreated(node);
		}
	}
}
package org.fit.cssbox.scriptbox.document;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public abstract class DocumentEventListener {
	
	private Document document;
	
	public DocumentEventListener(Document document) {
		this.document = document;
	}
	
	public abstract void nodeCreated(Node node);
	public abstract void nodeLoaded(Node node);
	public abstract void nodeInserted(Node node);
	public abstract void nodeRemoved(Node node);
	
	public Document getDocument() {
		return document;
	}
}

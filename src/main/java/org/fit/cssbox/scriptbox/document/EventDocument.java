package org.fit.cssbox.scriptbox.document;

import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
//import org.apache.html.dom.HTMLDocumentImpl;

public class EventDocument extends DocumentImpl {
	
	private static final long serialVersionUID = 1L;

	protected DocumentEventDispatcher dispatcher;
	
	protected EventListener domEventListener = new EventListener() {

		@Override
		public void handleEvent(Event event) {
			if (event.getType().equals("DOMNodeRemoved")) {
				dispatcher.fireNodeRemoved((Node)event.getCurrentTarget());
			} else if (event.getType().equals("DOMNodeInserted")) {
				dispatcher.fireNodeInserted((Node)event.getCurrentTarget());
			}
		}
		
	};
	
	public EventDocument() {
        super();
        initDocument();
    }

    public EventDocument(boolean grammarAccess) {
        super(grammarAccess);
        initDocument();
    }

    public EventDocument(DocumentType doctype) {
        super(doctype);
        initDocument();
    }

    public EventDocument(DocumentType doctype, boolean grammarAccess) {
        super(doctype, grammarAccess);
    }
	
	public void addDocumentEventListener(DocumentEventListener listener) {
		dispatcher.addDocumentEventListener(listener);
	}
	
	public void removeDocumentEventListener(DocumentEventListener listener) {
		dispatcher.removeDocumentEventListener(listener);
	}
	
	protected void initDocument() {
		dispatcher = new DocumentEventDispatcher();
		
	    addEventListener("DOMNodeRemoved", domEventListener, false);
	    addEventListener("DOMNodeInserted", domEventListener, false);
	}

}

package org.fit.cssbox.scriptbox.document.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;

public class EventDOMParser extends DOMParser implements EventProcessingProvider {
	protected XMLDocumentHandler superXMLDocumentHandler;
	protected XMLDocumentHandler eventXMLDocumentHandler;
	protected List<EventListenerEntry> listeners;
	
	public EventDOMParser() {
		listeners = new ArrayList<EventListenerEntry>();
		initParser();
	}
	
	public void addDocumentEventListener(String type, EventListener listener, boolean useCapture) {
		listeners.add(new EventListenerEntry(type, listener, useCapture));
	}
	
	public void removeDocumentEventListener(EventListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void reset() throws XNIException {
		super.reset();
		
		initParser();
	}
	
	protected void initParser() {	
		superXMLDocumentHandler = getConfiguration().getDocumentHandler();
		eventXMLDocumentHandler = new EventDocumentHandlerDecorator(superXMLDocumentHandler, this);
	    
	    getConfiguration().setDocumentHandler(eventXMLDocumentHandler);
	}
	
	@Override
	public Node getCurrentNode() {
    	return fCurrentNode;
    }

	@Override
	public XMLParserConfiguration getConfiguration() {
    	return fConfiguration;
    }

	@Override
	public DocumentImpl getDocumentImpl() {
		if (fDocument instanceof DocumentImpl) {
			return (DocumentImpl)fDocument;
		} else {
			return null;
		}
		
	}

	@Override
	public Collection<EventListenerEntry> getListeners() {
		return listeners;
	}
}

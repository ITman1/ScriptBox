package org.fit.cssbox.scriptbox.deprecied.document.event;

import java.io.IOException;

import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventListener;
import org.xml.sax.SAXException;

public class EventDOMSource extends DOMSource
{
	private EventDOMParser _parser;
	
    public EventDOMSource(DocumentSource src)
    {
        super(src);
    }

	public void addDocumentEventListener(String type, EventListener listener, boolean useCapture) {
		getParser().addDocumentEventListener(type, listener, useCapture);
	}
	
	public void removeDocumentEventListener(EventListener listener) {
		getParser().removeDocumentEventListener(listener);
	}
	
    public void reset() {
    	getParser().reset();
    }
    
    @Override
    public Document parse() throws SAXException, IOException
    {        
    	EventDOMParser parser = getParser();
    	
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        
        if (charset != null) {
        	parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
        }
        		
        parser.parse(new org.xml.sax.InputSource(getDocumentSource().getInputStream()));
        
        return parser.getDocument();
    }
    
    protected EventDOMParser getParser() {
    	if (_parser == null) {
            _parser = instantizeEventDOMParser();
    	}
    	
    	return _parser;
    }
    
    protected EventDOMParser instantizeEventDOMParser() {
    	return new EventDOMParser();
    }

}

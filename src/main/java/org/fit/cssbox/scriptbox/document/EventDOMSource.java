package org.fit.cssbox.scriptbox.document;

import java.io.IOException;

import org.fit.cssbox.io.DOMSource;
import org.fit.cssbox.io.DocumentSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class EventDOMSource extends DOMSource
{

    public EventDOMSource(DocumentSource src)
    {
        super(src);
    }

    public Document parse(DocumentEventListener listener) throws SAXException, IOException
    {
        EventDOMParser parser = new EventDOMParser();
        
        parser.addDocumentEventListener(listener);
        parser.setProperty("http://cyberneko.org/html/properties/names/elems", "lower");
        
        if (charset != null) {
        	parser.setProperty("http://cyberneko.org/html/properties/default-encoding", charset);
        }
        		
        parser.parse(new org.xml.sax.InputSource(getDocumentSource().getInputStream()));
        
        return parser.getDocument();
    }
    
    @Override
    public Document parse() throws SAXException, IOException
    {
        return parse(null);
    }

}

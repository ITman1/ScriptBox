package org.fit.cssbox.scriptbox.document;

import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Node;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class EventDOMParser extends DOMParser implements EventParserProcessingProvider {
	protected XMLDocumentHandler superXMLDocumentHandler;
	protected XMLDocumentHandler eventXMLDocumentHandler;
	protected DocumentEventDispatcher dispatcher;
	
	public EventDOMParser() {
		initParser();
	}
	
	public void addDocumentEventListener(DocumentEventListener listener) {
		dispatcher.addDocumentEventListener(listener);
	}
	
	public void removeDocumentEventListener(DocumentEventListener listener) {
		dispatcher.removeDocumentEventListener(listener);
	}
	
	protected void initParser() {
		dispatcher = new DocumentEventDispatcher();
		
		superXMLDocumentHandler = getConfiguration().getDocumentHandler();
		eventXMLDocumentHandler = new EventDocumentHandlerDecorator(superXMLDocumentHandler, this);
	    
	    getConfiguration().setDocumentHandler(eventXMLDocumentHandler);
	}
	
	@Override
	public void reset() throws XNIException {
		super.reset();
		
		try {
			setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.DocumentImpl");
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
	public DocumentEventDispatcher getDocumentEventDispatcher() {
		return dispatcher;
	}
}

package org.fit.cssbox.scriptbox.document.script;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.fit.cssbox.scriptbox.document.event.EventDocumentHandlerDecorator;
import org.fit.cssbox.scriptbox.document.event.EventProcessingProvider;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

public class ScriptDOMParser extends EventDOMParser {
	
	private boolean primaryDocumentParser;
	
	private class ScriptDocumentHandler extends EventDocumentHandlerDecorator {

		public ScriptDocumentHandler(XMLDocumentHandler oldHandler, EventProcessingProvider processingInfoProvider) {
			super(oldHandler, processingInfoProvider);
		}
		
		@Override
		public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
			super.startDocument(locator, encoding, namespaceContext, augs);
			
			DocumentImpl document = processingInfoProvider.getDocumentImpl();
			
			if (document instanceof ScriptableDocument) {
				((ScriptableDocument)document).setParserOrigin(primaryDocumentParser);
			}
		}
	};
	
	ScriptDOMParser(boolean primaryDocumentParser) {
		this.primaryDocumentParser = primaryDocumentParser;
	}
	
	protected void initParser() {	
		superXMLDocumentHandler = getConfiguration().getDocumentHandler();
		eventXMLDocumentHandler = new ScriptDocumentHandler(superXMLDocumentHandler, this);
	    
	    getConfiguration().setDocumentHandler(eventXMLDocumentHandler);

		try {
			setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.fit.cssbox.scriptbox.document.script.ScriptableDocument");
		} catch (SAXNotRecognizedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package org.fit.cssbox.scriptbox.deprecied.document.event;

import java.util.Collection;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class EventDocumentHandlerDecorator implements XMLDocumentHandler {
	protected static final String ERROR_DOMAIN = "http://fit.vutbr.cz/fit/annotations/document";
      
	protected XMLDocumentHandler oldHandler;
	protected EventProcessingProvider processingInfoProvider;
      
	public EventDocumentHandlerDecorator(XMLDocumentHandler oldHandler, EventProcessingProvider processingInfoProvider) {
		this.oldHandler = oldHandler;
		this.processingInfoProvider = processingInfoProvider;
	}

	@Override
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
		oldHandler.startDocument(locator, encoding, namespaceContext, augs);
		DocumentImpl document = processingInfoProvider.getDocumentImpl();
		
		if (document != null) {
			Collection<EventListenerEntry> listeners = processingInfoProvider.getListeners();
			
			for (EventListenerEntry listener : listeners) {
				document.addEventListener(listener.getType(), listener.getListener(), listener.isUseCapture());
			}
		}
	}

	@Override
	public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
		oldHandler.xmlDecl(version, encoding, standalone, augs);
	}

	@Override
	public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
		oldHandler.doctypeDecl(rootElement, publicId, systemId, augs);
	}

	@Override
	public void comment(XMLString text, Augmentations augs) throws XNIException {
		oldHandler.comment(text, augs);
	}

	@Override
	public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
		oldHandler.processingInstruction(target, data, augs);
	}

	@Override
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		oldHandler.startElement(element, attributes, augs);
	}

	@Override
	public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		oldHandler.emptyElement(element, attributes, augs);
	}

	@Override
	public void startGeneralEntity(String name, XMLResourceIdentifier identifier, String encoding, Augmentations augs) throws XNIException {
		oldHandler.startGeneralEntity(name, identifier, encoding, augs);
	}

	@Override
	public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
		oldHandler.textDecl(version, encoding, augs);
	}

	@Override
	public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
		oldHandler.endGeneralEntity(name, augs);
	}

	@Override
	public void characters(XMLString text, Augmentations augs) throws XNIException {
		oldHandler.characters(text, augs);
	}

	@Override
	public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
		oldHandler.ignorableWhitespace(text, augs);		
	}

	@Override
	public void endElement(QName element, Augmentations augs) throws XNIException {
		oldHandler.endElement(element, augs);		
	}

	@Override
	public void startCDATA(Augmentations augs) throws XNIException {
		oldHandler.startCDATA(augs);		
	}

	@Override
	public void endCDATA(Augmentations augs) throws XNIException {
		oldHandler.endCDATA(augs);		
	}

	@Override
	public void endDocument(Augmentations augs) throws XNIException {
		oldHandler.endDocument(augs);	
	}

	@Override
	public void setDocumentSource(XMLDocumentSource source) {
		oldHandler.setDocumentSource(source);
	}

	@Override
	public XMLDocumentSource getDocumentSource() {
		return oldHandler.getDocumentSource();
	}
	
	public void reset() {
		
	}
}

package org.fit.cssbox.scriptbox.document;

import java.util.HashSet;
import java.util.Set;

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
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class EventDocumentHandlerDecorator implements XMLDocumentHandler {
      protected static final String ERROR_DOMAIN = "http://fit.vutbr.cz/fit/annotations/document";
      
      private XMLDocumentHandler oldHandler;
      private EventParserProcessingProvider processingInfoProvider;
      private DocumentEventDispatcher dispatcher;
      private Set<Element> startedElements;
      
      public EventDocumentHandlerDecorator(XMLDocumentHandler oldHandler, EventParserProcessingProvider processingInfoProvider) {
          this.oldHandler = oldHandler;
          this.processingInfoProvider = processingInfoProvider;
          this.dispatcher = processingInfoProvider.getDocumentEventDispatcher();
          
          reset();
      }

      @Override
      public void xmlDecl(String string, String string1, String string2, Augmentations a) throws XNIException {
          oldHandler.xmlDecl(string, string1, string2, a);
      }

      @Override
      public void doctypeDecl(String string, String string1, String string2, Augmentations a) throws XNIException {
          oldHandler.doctypeDecl(string, string1, string2, a);
      }

      @Override
      public void processingInstruction(String string, XMLString xmls, Augmentations a) throws XNIException {
          oldHandler.processingInstruction(string, xmls, a);
      }

      @Override
      public void emptyElement(QName qname, XMLAttributes xmla, Augmentations a) throws XNIException {
          oldHandler.emptyElement(qname, xmla, a);
      }

      @Override
      public void startGeneralEntity(String string, XMLResourceIdentifier xmlri, String string1, Augmentations a) throws XNIException {
          oldHandler.startGeneralEntity(string, xmlri, string1, a);
      }

      @Override
      public void textDecl(String string, String string1, Augmentations a) throws XNIException {
          oldHandler.textDecl(string, string1, a);
      }

      @Override
      public void endGeneralEntity(String string, Augmentations a) throws XNIException {
          oldHandler.endGeneralEntity(string, a);
      }

      @Override
      public void ignorableWhitespace(XMLString xmls, Augmentations a) throws XNIException {
          oldHandler.ignorableWhitespace(xmls, a);
      }

      @Override
      public void startCDATA(Augmentations a) throws XNIException {
          oldHandler.startCDATA(a);
      }

      @Override
      public void endCDATA(Augmentations a) throws XNIException {
          oldHandler.endCDATA(a);
      }

      @Override
      public void setDocumentSource(XMLDocumentSource xmlds) {
          oldHandler.setDocumentSource(xmlds);
      }

      @Override
      public XMLDocumentSource getDocumentSource() {
        return oldHandler.getDocumentSource();
      }
      
      @Override
      public void startDocument(XMLLocator xmll, String string, NamespaceContext nc, Augmentations augs) throws XNIException {
          oldHandler.startDocument(xmll, string, nc, augs);
      }

      @Override
      public void startElement(QName elementName, XMLAttributes attrs, Augmentations augs) throws XNIException {
          oldHandler.startElement(elementName, attrs, augs);
          
          if (processingInfoProvider.getCurrentNode().getNodeType() == Node.ELEMENT_NODE) {
              Element element = (Element) processingInfoProvider.getCurrentNode();        
              startedElements.add(element);
          }
      }

      // TODO: Text nodes may be incomming in chunks.
      @Override
      public void characters(XMLString text, Augmentations augs) throws XNIException {
          oldHandler.characters(text, augs);

          if (text.length == 0) {
             return;
          }

          Node textNode = processingInfoProvider.getCurrentNode().getLastChild();
          
          if ((textNode != null) && (textNode.getNodeType() == Node.TEXT_NODE)) {
              dispatcher.fireNodeLoaded(textNode);
          }
      }

      @Override
      public void comment(XMLString commentText, Augmentations augs) throws XNIException {
          oldHandler.comment(commentText, augs);

          if (commentText.length == 0) {
            return;
          }

          Node commentNode = processingInfoProvider.getCurrentNode().getLastChild();

          if ((commentNode != null) && (commentNode.getNodeType() == Node.COMMENT_NODE)) {
        	  dispatcher.fireNodeLoaded(commentNode);         
          }
      }

      @Override
      public void endDocument(Augmentations augs) throws XNIException {
          oldHandler.endDocument(augs);
      } // endDocument()

      @Override
      public void endElement(QName qname, Augmentations augs) throws XNIException {
    	    Node elementNode = processingInfoProvider.getCurrentNode();
    	    oldHandler.endElement(qname, augs);

    	    if (startedElements.contains(elementNode)) {
    	    	dispatcher.fireNodeLoaded(elementNode);
    	  }
      }
      		
      public void reset() {
    	  this.startedElements = new HashSet<Element>();
      }
}

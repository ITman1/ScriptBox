package org.fit.cssbox.scriptbox.deprecied.document.event;

import java.util.Collection;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface EventProcessingProvider {
    public Node getCurrentNode();
    public XMLParserConfiguration getConfiguration();
    public DocumentImpl getDocumentImpl();
    public void setDocument(Document document);
    public void setDocumentImpl(CoreDocumentImpl documentImpl);
    public Document getDocument();
    public Collection<EventListenerEntry> getListeners();
    public boolean isDocumentFragmentParser();
    public boolean isParserAborted();
    public void setStorePSVI(boolean storePSVI);
    public void setCurrentNode(Node currentNode);
    public boolean isDeferNodeExpansion();
    
    public String get_DEFAULT_DOCUMENT_CLASS_NAME();
    public String get_CORE_DOCUMENT_CLASS_NAME();
    public String get_PSVI_DOCUMENT_CLASS_NAME();
}

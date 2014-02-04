package org.fit.cssbox.scriptbox.document.event;

import java.util.Collection;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.dom.DocumentImpl;
import org.w3c.dom.Node;

public interface EventProcessingProvider {
    public Node getCurrentNode();
    public XMLParserConfiguration getConfiguration();
    public DocumentImpl getDocumentImpl();
    public Collection<EventListenerEntry> getListeners();
}

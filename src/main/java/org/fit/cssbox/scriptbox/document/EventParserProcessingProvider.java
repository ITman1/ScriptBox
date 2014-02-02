package org.fit.cssbox.scriptbox.document;

import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.Node;

public interface EventParserProcessingProvider {
    public Node getCurrentNode();
    public XMLParserConfiguration getConfiguration();
    public DocumentEventDispatcher getDocumentEventDispatcher();
}

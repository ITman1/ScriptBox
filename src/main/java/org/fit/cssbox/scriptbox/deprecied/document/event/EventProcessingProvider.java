/**
 * EventProcessingProvider.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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

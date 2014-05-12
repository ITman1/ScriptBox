/**
 * EventDOMParser.java
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

package org.fit.cssbox.scriptbox.deprecied.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventListener;

public class EventDOMParser extends DOMParser {
	protected List<EventListenerEntry> listeners;
	protected EventProcessingProvider processingProvider;
	protected boolean parserAborted;
	
	private class EventProcessingProviderImpl implements EventProcessingProvider {
		@Override
		public Node getCurrentNode() {
	    	return fCurrentNode;
	    }

		@Override
		public XMLParserConfiguration getConfiguration() {
	    	return fConfiguration;
	    }

		@Override
		public DocumentImpl getDocumentImpl() {
			if (fDocument instanceof DocumentImpl) {
				return (DocumentImpl)fDocument;
			} else {
				return null;
			}
			
		}

		@Override
		public Collection<EventListenerEntry> getListeners() {
			return listeners;
		}

		@Override
		public boolean isDocumentFragmentParser() {
			return false;
		}

		@Override
		public boolean isParserAborted() {
			return parserAborted;
		}

		@Override
		public void setDocument(Document document) {
			fDocument = document;
		}

		@Override
		public void setDocumentImpl(CoreDocumentImpl documentImpl) {
			fDocumentImpl = documentImpl;
		}

		@Override
		public Document getDocument() {
			return fDocument;
		}

		@Override
		public void setStorePSVI(boolean storePSVI) {
			fStorePSVI = storePSVI;
		}

		@Override
		public String get_DEFAULT_DOCUMENT_CLASS_NAME() {
			return DEFAULT_DOCUMENT_CLASS_NAME;
		}

		@Override
		public String get_CORE_DOCUMENT_CLASS_NAME() {
			return CORE_DOCUMENT_CLASS_NAME;
		}

		@Override
		public String get_PSVI_DOCUMENT_CLASS_NAME() {
			return PSVI_DOCUMENT_CLASS_NAME;
		}

		@Override
		public void setCurrentNode(Node currentNode) {
			fCurrentNode = currentNode;
		}

		@Override
		public boolean isDeferNodeExpansion() {
			return fDeferNodeExpansion;
		}
	};
	
	private XMLDocumentHandler _superXMLDocumentHandler;
	
	public EventDOMParser() {
		listeners = new ArrayList<EventListenerEntry>();
		processingProvider = new EventProcessingProviderImpl();
		initParser();
	}
	
	public void addDocumentEventListener(String type, EventListener listener, boolean useCapture) {
		listeners.add(new EventListenerEntry(type, listener, useCapture));
	}
	
	public void removeDocumentEventListener(EventListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void reset() throws XNIException {
		super.reset();
		
		initParser();
	}
	
	protected void initParser() {			
		XMLDocumentHandler _eventXMLDocumentHandler = instantizeEventDocumentHandlerDecorator();
		
		processingProvider.getConfiguration().setDocumentHandler(_eventXMLDocumentHandler);
	}
	
	protected EventDocumentHandlerDecorator instantizeEventDocumentHandlerDecorator() {
		if (_superXMLDocumentHandler == null) {
			_superXMLDocumentHandler = processingProvider.getConfiguration().getDocumentHandler();
		}
		return new EventDocumentHandlerDecorator(_superXMLDocumentHandler, processingProvider);
	}

}

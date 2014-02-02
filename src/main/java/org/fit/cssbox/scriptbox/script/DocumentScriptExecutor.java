package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.document.DocumentEventListener;
import org.fit.cssbox.scriptbox.document.EventDocument;
import org.fit.cssbox.scriptbox.script.document.dom.ContextScriptElement;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentScriptExecutor {
	private static DocumentScriptExecutor instance;
	//private static String SCRIPT_ELEMENT_NAME = "script";
	private static String DEFAULT_SCRIPT_MIME_TYPE = "text/javascript";
	
	private Map<EventDocument, DocumentContext> documentContexts;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	class DocumentEventHandler extends DocumentEventListener {
		
		DocumentEventHandler(Document document) {
			super(document);
		}
		
		@Override
		public void nodeLoaded(Node node) {			
			if (node instanceof ContextScriptElement) {
				prepareScriptElement((ContextScriptElement)node);
			}
		}

		@Override
		public void nodeInserted(Node node) {}

		@Override
		public void nodeRemoved(Node node) {}

		@Override
		public void nodeCreated(Node node) {
			
		}
		
	};
	
	private DocumentScriptExecutor() {
		documentContexts = new HashMap<EventDocument, DocumentContext>();
		documentScriptEngineManager = DocumentScriptEngineManager.getInstance();
	}
	
	public static synchronized DocumentScriptExecutor getInstance() {
		if (instance == null) {
			instance = new DocumentScriptExecutor();
		}
		
		return instance;
	}
	
	public void registerDocumentContext(DocumentContext documentContext) {
		EventDocument document = documentContext.getDocument();
		DocumentContext storedDocumentContext = documentContexts.get(document);
		
		if (storedDocumentContext == null && storedDocumentContext != documentContext) {
			unregisterDocumentContext(storedDocumentContext);
		}
		
		if (storedDocumentContext != documentContext) {
			documentContexts.put(document, documentContext);
			documentContext.setExecutorHandler(new DocumentEventHandler(document));
			document.addDocumentEventListener(documentContext.getExecutorHandler());
		}
	}
	
	public void unregisterDocumentContext(DocumentContext documentContext) {
		documentContexts.remove(documentContext.getDocument());
		
		EventDocument document = documentContext.getDocument();
		document.removeDocumentEventListener(documentContext.getExecutorHandler());
		
		documentContext.setExecutorHandler(null);
	}
		
	protected boolean prepareScriptElement(ContextScriptElement scriptElement) {		
		/* Step 1*/
		if (scriptElement.isAlreadyStarted()) {
			return false;
		}
		
		/* Step 2 */
		if (scriptElement.isParserInserted()) {
			scriptElement.setWasParserInserted(true);
			scriptElement.setParserInserted(false);
		}
		
		/* Step 3 */
		if (scriptElement.isWasParserInserted() && scriptElement.getAsync() == null) {
			scriptElement.setForceAsync(true);
		}
		
		/* Step 4 */
		if (scriptElement.getSrc() == null) {
			boolean isEmpty = true;
			NodeList childNodes = scriptElement.getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				
				if (node.getNodeType() == Node.TEXT_NODE) {
					isEmpty = isEmpty && node.getNodeValue().isEmpty();
				} else if (node.getNodeType() != Node.COMMENT_NODE) {
					isEmpty = false;
				}
			}

			if (isEmpty) {
				return false;
			}
		}
		
		/* Step 5 */
		// TODO: Re-check the interpretation of the HTML5 specification
		if (!documentContexts.containsKey((scriptElement.getOwnerDocument()))) {
			return false;
		}
		
		/* Step 6 */
		if ((scriptElement.getType() != null && scriptElement.getType().isEmpty())
				|| (scriptElement.getType() == null && scriptElement.getLang() != null && scriptElement.getLang().isEmpty())
				|| (scriptElement.getType() == null && scriptElement.getLang() == null)) {
			scriptElement.setScriptMimeType(DEFAULT_SCRIPT_MIME_TYPE);
		} else if (scriptElement.getType() != null) {
			scriptElement.setScriptMimeType(scriptElement.getType().trim());
		} else if (scriptElement.getLang() != null && !scriptElement.getLang().isEmpty()) {
			scriptElement.setScriptMimeType("text/" + scriptElement.getLang().trim());
		}
		
		/* Step 7 */
		if (!documentScriptEngineManager.isSupported(scriptElement.getScriptMimeType())) {
			return false;
		}
		
		/* Step 8 */
		if (scriptElement.isWasParserInserted()) {
			scriptElement.setParserInserted(true);
			scriptElement.setForceAsync(false);
		}
		
		/* Step 9 */
		scriptElement.setAlreadyStarted(true);
		
		/* Step 10 */
		if (scriptElement.isParserInserted() && scriptElement.getCreatorDocument() != scriptElement.getOwnerDocument()) {
			return false;
		}
		
		/* Step 11 */
		// TODO: Script disabled feature
			
		/* Step 12 */
		// TODO: Script for and event features
			
		/* Step 13 */
		// TODO: Script encoding feature	
		
		return true;
	}
}

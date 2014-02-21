package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentScriptExecutor {
	private static DocumentScriptExecutor instance;
	
	private Map<Html5DocumentImpl, DocumentContext> documentContexts;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	private DocumentScriptExecutor() {
		documentContexts = new HashMap<Html5DocumentImpl, DocumentContext>();
		documentScriptEngineManager = DocumentScriptEngineManager.getInstance();
	}
	
	public static synchronized DocumentScriptExecutor getInstance() {
		if (instance == null) {
			instance = new DocumentScriptExecutor();
		}
		
		return instance;
	}
	
	public void registerDocumentContext(DocumentContext documentContext) {
		Html5DocumentImpl document = documentContext.getDocument();
		DocumentContext storedDocumentContext = documentContexts.get(document);
		
		if (storedDocumentContext == null && storedDocumentContext != documentContext) {
			unregisterDocumentContext(storedDocumentContext);
		}
		
		if (storedDocumentContext != documentContext) {
			documentContexts.put(document, documentContext);
		}
	}
	
	public void unregisterDocumentContext(DocumentContext documentContext) {
		documentContexts.remove(documentContext.getDocument());
	}
		
	
}

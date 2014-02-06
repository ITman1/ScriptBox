package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.document.script.ScriptElement;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentScriptExecutor {
	private static DocumentScriptExecutor instance;
	
	private Map<ScriptableDocument, DocumentContext> documentContexts;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	private DocumentScriptExecutor() {
		documentContexts = new HashMap<ScriptableDocument, DocumentContext>();
		documentScriptEngineManager = DocumentScriptEngineManager.getInstance();
	}
	
	public static synchronized DocumentScriptExecutor getInstance() {
		if (instance == null) {
			instance = new DocumentScriptExecutor();
		}
		
		return instance;
	}
	
	public void registerDocumentContext(DocumentContext documentContext) {
		ScriptableDocument document = documentContext.getDocument();
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

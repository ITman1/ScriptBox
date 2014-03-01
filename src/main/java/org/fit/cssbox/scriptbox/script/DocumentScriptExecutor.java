package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DocumentScriptExecutor {
	/*private static DocumentScriptExecutor instance;
	
	private Map<Html5DocumentImpl, Html5DocumentImpl> documentContexts;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	private DocumentScriptExecutor() {
		documentContexts = new HashMap<Html5DocumentImpl, Html5DocumentImpl>();
		documentScriptEngineManager = DocumentScriptEngineManager.getInstance();
	}
	
	public static synchronized DocumentScriptExecutor getInstance() {
		if (instance == null) {
			instance = new DocumentScriptExecutor();
		}
		
		return instance;
	}
	
	public void registerDocumentContext(Html5DocumentImpl documentContext) {
		Html5DocumentImpl document = documentContext.getDocument();
		Html5DocumentImpl storedDocumentContext = documentContexts.get(document);
		
		if (storedDocumentContext == null && storedDocumentContext != documentContext) {
			unregisterDocumentContext(storedDocumentContext);
		}
		
		if (storedDocumentContext != documentContext) {
			documentContexts.put(document, documentContext);
		}
	}
	
	public void unregisterDocumentContext(Html5DocumentImpl documentContext) {
		documentContexts.remove(documentContext.getDocument());
	}
		
	*/
}

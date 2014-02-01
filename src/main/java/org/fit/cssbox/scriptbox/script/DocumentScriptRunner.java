package org.fit.cssbox.scriptbox.script;

import java.util.List;
import java.util.Map;

import org.fit.cssbox.scriptbox.document.EventDocument;

public abstract class DocumentScriptRunner {
	private Map<EventDocument, DocumentScriptEngine> documentScriptEngines;
	
	public DocumentScriptEngine initDocumentScriptEngine(EventDocument document) {
		DocumentScriptEngine documentScriptEngine = documentScriptEngines.get(document);
		
		if (documentScriptEngine == null) {
			DocumentScriptEngineFactory factory = getDocumentScriptEngineFactory();
			
			documentScriptEngine = factory.getDocumentScriptEngine(document);
			documentScriptEngines.put(document, documentScriptEngine);
		}
		
		return documentScriptEngine;
	}
	
	public void destroyDocumentScriptEngine(EventDocument document) {
		documentScriptEngines.remove(document);
	}
	
	public void getDocumentScriptEngine(EventDocument document) {
		documentScriptEngines.get(document);
	}
	
	public abstract List<String> getMimeTypes();
	
	protected abstract DocumentScriptEngineFactory getDocumentScriptEngineFactory();
}

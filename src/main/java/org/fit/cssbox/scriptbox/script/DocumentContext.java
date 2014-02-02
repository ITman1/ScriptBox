package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.util.URI;
import org.fit.cssbox.scriptbox.document.DocumentEventListener;
import org.fit.cssbox.scriptbox.document.EventDocument;

public class DocumentContext {
	private boolean destroyed;
	private URI baseURI;
	private DocumentEventListener executorHandler;
	
	private EventDocument document;
	private Map<Class<? extends DocumentScriptEngine>, DocumentScriptEngine> scriptEngines;
	
	private DocumentContext(EventDocument document) {
		this.document = document;

		scriptEngines = new HashMap<Class<? extends DocumentScriptEngine>, DocumentScriptEngine>();
	}
	
	public static DocumentContext createContext(EventDocument document) {
		return new DocumentContext(document);
	}

	public void destroyContext() {
		destroyed = true;
	}
	
	public EventDocument getDocument() {
		if (destroyed) {
			return null;
		} else {
			return document;
		}
	}
	
	public void addDocumentScriptEngine(DocumentScriptEngine scriptEngine) {
		scriptEngines.put(scriptEngine.getClass(), scriptEngine);
	}
	
	public void getDocumentScriptEngine(Class<? extends DocumentScriptEngine> engineClass) {
		scriptEngines.get(engineClass);
	}
	
	public URI getBaseURI() {
		return baseURI;
	}
	
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
	}

	public DocumentEventListener getExecutorHandler() {
		return executorHandler;
	}

	public void setExecutorHandler(DocumentEventListener executorHandler) {
		this.executorHandler = executorHandler;
	}
}

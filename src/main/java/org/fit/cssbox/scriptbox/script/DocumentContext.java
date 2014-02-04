package org.fit.cssbox.scriptbox.script;

import java.util.HashMap;
import java.util.Map;

import org.apache.xerces.util.URI;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;

public class DocumentContext {
	private boolean destroyed;
	private URI baseURI;
	
	private ScriptableDocument document;
	private Map<Class<? extends DocumentScriptEngine>, DocumentScriptEngine> scriptEngines;
	
	private DocumentContext(ScriptableDocument document) {
		this.document = document;

		scriptEngines = new HashMap<Class<? extends DocumentScriptEngine>, DocumentScriptEngine>();
	}
	
	public static DocumentContext createContext(ScriptableDocument document) {
		return new DocumentContext(document);
	}

	public void destroyContext() {
		destroyed = true;
	}
	
	public ScriptableDocument getDocument() {
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
}

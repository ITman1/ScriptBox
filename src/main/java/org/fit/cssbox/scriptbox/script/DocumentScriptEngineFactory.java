package org.fit.cssbox.scriptbox.script;

import org.fit.cssbox.scriptbox.document.EventDocument;

public interface DocumentScriptEngineFactory {
	public DocumentScriptEngine getDocumentScriptEngine(EventDocument document);
}

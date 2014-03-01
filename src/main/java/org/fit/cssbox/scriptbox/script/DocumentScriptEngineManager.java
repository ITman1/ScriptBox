package org.fit.cssbox.scriptbox.script;

import org.fit.cssbox.scriptbox.misc.MimeContentRegistryBase;

public class DocumentScriptEngineManager extends MimeContentRegistryBase<DocumentScriptEngineFactory, DocumentScriptEngine> {
	static private DocumentScriptEngineManager instance;
	
	private DocumentScriptEngineManager() {}
	
	public static synchronized DocumentScriptEngineManager getInstance() {
		if (instance == null) {
			instance = new DocumentScriptEngineManager();
		}
		
		return instance;
	}
}

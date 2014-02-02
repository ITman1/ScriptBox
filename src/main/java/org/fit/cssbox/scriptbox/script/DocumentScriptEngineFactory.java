package org.fit.cssbox.scriptbox.script;

import java.util.List;

public abstract class DocumentScriptEngineFactory {
	public boolean isSupported(String mimeType) {
		List<String> supportedMimeTypes = getMimeTypes();
		
		mimeType = mimeType.trim();
		
		for (String supportedMimeType : supportedMimeTypes) {
			if (supportedMimeType.equals(mimeType)) {
				return true;
			}
		}
		
		return false;
	}
	
	public abstract DocumentScriptEngine getDocumentScriptEngine(DocumentContext documentContext);
	public abstract List<String> getMimeTypes();
	
}

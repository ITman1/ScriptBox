package org.fit.cssbox.scriptbox.document.script;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.fit.cssbox.scriptbox.document.event.EventDOMSource;

public class ScriptDOMSource extends EventDOMSource {

	private boolean primaryDocumentParser;
		
	public ScriptDOMSource(DocumentSource src, boolean primaryDocumentParser) {
		super(src);
		
		this.primaryDocumentParser = primaryDocumentParser;
	}
	
	@Override
	protected EventDOMParser instantizeEventDOMParser() {
		return new ScriptDOMParser(primaryDocumentParser);
	}
	
}

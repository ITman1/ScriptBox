package org.fit.cssbox.scriptbox.document.script;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.fit.cssbox.scriptbox.document.event.EventDOMSource;

public class ScriptDOMSource extends EventDOMSource {
		
	private BrowsingContext _browsingContext;
	
	public ScriptDOMSource(DocumentSource src, BrowsingContext browsingContext) {
		super(src);
		_browsingContext = browsingContext;
	}
	
	@Override
	protected EventDOMParser instantizeEventDOMParser() {
		return new ScriptDOMParser(_browsingContext);
	}
	
}

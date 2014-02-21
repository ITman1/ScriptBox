package org.fit.cssbox.scriptbox.document.script;

import java.net.URISyntaxException;

import org.fit.cssbox.io.DocumentSource;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.fit.cssbox.scriptbox.document.event.EventDOMSource;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class ScriptDOMSource extends EventDOMSource {
		
	private BrowsingContext _browsingContext;
	
	public ScriptDOMSource(DocumentSource src, BrowsingContext browsingContext) {
		super(src);
		_browsingContext = browsingContext;
	}
	
	@Override
	protected EventDOMParser instantizeEventDOMParser() {
		Html5DocumentImpl document = null;
		
		try {
			document = Html5DocumentImpl.createDocument(_browsingContext, src.getURL().toURI());
		} catch (URISyntaxException e) {
			//FIXME: Maybe throw custom exception here or log it.
		}
		
		return new ScriptDOMParser(document);
	}
	
}

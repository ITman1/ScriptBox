package org.fit.cssbox.scriptbox.resource.handlers;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.resource.ResourceHandler;

public abstract class RenderedResourceHandler extends ResourceHandler {
	protected Html5DocumentImpl createDocument(BrowsingContext context, URL url) {
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		
		Html5DocumentImpl recycleWindowDocument = null;
		if (sessionHistory.getLength() == 1 && currentDocument != null && currentDocument.getAddress().equals(Html5DocumentImpl.DEFAULT_URL)) {
			recycleWindowDocument = currentDocument;
		}
		
		// FIXME: Set the document's referrer to the address of the resource from which Request-URIs 
		// are obtained as determined when the fetch algorithm obtained the resource
		Html5DocumentImpl newDocument = Html5DocumentImpl.createDocument(context, url, recycleWindowDocument);
		
		newDocument.implementSandboxing();
		
		BrowsingContext ancestorContext = context;
		do  {		
			if (ancestorContext.get == this) {
				return true;
			}
			
			ancestorContext = ancestorContext.getCreatorContext();
		} while (ancestorContext != null);
		
		return newDocument;
	}
}

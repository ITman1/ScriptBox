package org.fit.cssbox.scriptbox.events;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * From HTML5 specification:
 * Each task in a browsing context event loop is associated with a Document; 
 */
public class Task {
	private Html5DocumentImpl _document;
	
	/*
	 * if the task was queued in the context of an element, then it is the element's Document
	 */
	public Task(Element element) {
		Document document = element.getOwnerDocument();
		
		if (document instanceof Html5DocumentImpl) {
			_document = (Html5DocumentImpl)document;
		}
	}

	/*
	 * if the task was queued in the context of a browsing context, then it is the browsing 
	 * context's active document at the time the task was queued
	 */
	public Task(BrowsingContext browsingContext) {
		_document = browsingContext.getActiveDocument();
	}
	
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	public void execute() {
		
	}
}

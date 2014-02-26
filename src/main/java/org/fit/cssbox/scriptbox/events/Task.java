package org.fit.cssbox.scriptbox.events;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * From HTML5 specification:
 * Each task in a browsing context event loop is associated with a Document; 
 */
public abstract class Task {
	private Html5DocumentImpl _document;
	private TaskSource _source;
	
	private Task(TaskSource source) {
		_source = source;
	}
	
	/*
	 * if the task was queued in the context of an element, then it is the element's Document
	 */
	public Task(TaskSource source, Element element) {
		this(source);
		
		Document document = element.getOwnerDocument();
		
		if (document instanceof Html5DocumentImpl) {
			_document = (Html5DocumentImpl)document;
		}
	}

	/*
	 * if the task was queued in the context of a browsing context, then it is the browsing 
	 * context's active document at the time the task was queued
	 */
	public Task(TaskSource source, BrowsingContext browsingContext) {
		this(source);
		
		_document = browsingContext.getActiveDocument();
	}
	
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	public TaskSource getTaskSource() {
		return _source;
	}
	
	public BrowsingUnit getBrowsingUnit() {
		return _document.getBrowsingContext().getBrowsingUnit();
	}
	
	public abstract void execute();
}

package org.fit.cssbox.scriptbox.events;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * From HTML5 specification:
 * Each task in a browsing context event loop is associated with a Document; 
 */
public abstract class Task implements Executable {
	private Html5DocumentImpl _document;
	private TaskSource _source;
	protected boolean started;
	protected boolean finished;
	
	private Task(TaskSource source) {
		_source = source;
	}
	
	public Task(TaskSource source, Html5DocumentImpl document) {
		this(source);
		
		_document = document;
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
	
	public BrowsingContext getBrowsingContext() {
		return _document.getBrowsingContext();
	}
	
	public TaskSource getTaskSource() {
		return _source;
	}
	
	public BrowsingUnit getBrowsingUnit() {
		return _document.getBrowsingContext().getBrowsingUnit();
	}
	
	public EventLoop getEventLoop() {
		return getBrowsingUnit().getEventLoop();
	}
	
	public synchronized boolean isRunning() {
		return started && !finished;
	}
	
	public synchronized boolean isStarted() {
		return started;
	}
	
	public synchronized boolean isFinished() {
		return finished;
	}
	
	public synchronized void join() throws InterruptedException {
		if (!finished) {
			wait();
		}
	}
	
	protected synchronized void onStarted() {
		started = true;
	}
	
	protected synchronized void onFinished() {
		finished = true;
		notifyAll();
	}
	
	public abstract void execute() throws TaskAbortedException, InterruptedException;
}

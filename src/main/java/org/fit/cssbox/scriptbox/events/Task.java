/**
 * Task.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.events;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represents corresponding abstract class for tasks where each
 * task in a browsing context event loop is associated with a Document.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#event-loop">Event loop</a>
 */
public abstract class Task implements Executable {
	private Html5DocumentImpl _document;
	private TaskSource _source;
	protected boolean started;
	protected boolean finished;
	
	private Task(TaskSource source) {
		_source = source;
	}
	
	/**
	 * Constructs task of the given task source and for a specified document.
	 * @param source Task source where to put this task.
	 * @param document Document with which is this task associated.
	 */
	public Task(TaskSource source, Html5DocumentImpl document) {
		this(source);
		
		_document = document;
	}
	
	/**
	 * Constructs task from the element.
	 * 
	 * @param source Task source where to put this task.
	 * @param element Element of which document will be used to create a task.
	 */
	public Task(TaskSource source, Element element) {
		this(source);
		/*
		 * if the task was queued in the context of an element, then it is the element's Document
		 */
		Document document = element.getOwnerDocument();
		
		if (document instanceof Html5DocumentImpl) {
			_document = (Html5DocumentImpl)document;
		}
	}

	/**
	 * Constructs task with a active document of the passed browsing context.
	 * 
	 * @param source Task source where to put this task.
	 * @param browsingContext Browsing context which will serve for retrieving active document which belongs to this task.
	 */
	public Task(TaskSource source, BrowsingContext browsingContext) {
		this(source);
		/*
		 * if the task was queued in the context of a browsing context, then it is the browsing 
		 * context's active document at the time the task was queued
		 */
		_document = browsingContext.getActiveDocument();
	}
	
	/**
	 * Returns associated document.
	 * 
	 * @return Associated document.
	 */
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	/**
	 * Returns associated browsing context.
	 * 
	 * @return Associated browsing context.
	 */
	public BrowsingContext getBrowsingContext() {
		return _document.getBrowsingContext();
	}
	
	/**
	 * Returns associated task source.
	 * 
	 * @return Associated task source.
	 */
	public TaskSource getTaskSource() {
		return _source;
	}
	
	/**
	 * Returns associated browsing unit.
	 * 
	 * @return Associated browsing unit.
	 */
	public BrowsingUnit getBrowsingUnit() {
		return _document.getBrowsingContext().getBrowsingUnit();
	}
	
	/**
	 * Returns associated event loop.
	 * 
	 * @return Associated event loop.
	 */
	public EventLoop getEventLoop() {
		return getBrowsingUnit().getEventLoop();
	}
	
	/**
	 * Tests whether is this task running or not.
	 * 
	 * @return True if is this task running, otherwise false.
	 */
	public synchronized boolean isRunning() {
		return started && !finished;
	}
	
	/**
	 * Tests whether has this task already started.
	 * 
	 * @return True if has this task already started, otherwise false.
	 */
	public synchronized boolean isStarted() {
		return started;
	}
	
	/**
	 * Tests whether has this task already finished.
	 * 
	 * @return True if has this task already finished, otherwise false.
	 */
	public synchronized boolean isFinished() {
		return finished;
	}
	
	/**
	 * Pauses current thread and wait for completion of this task.
	 * 
	 * @throws InterruptedException Is thrown when current thread is interrupted.
	 */
	public synchronized void join() throws InterruptedException {
		if (!finished) {
			wait();
		}
	}
	
	/**
	 * Callback method for notifying that event loop will execute this task.
	 */
	protected synchronized void onStarted() {
		started = true;
	}
	
	/**
	 * Callback method for notifying that event loop completed execution of this task.
	 */
	protected synchronized void onFinished() {
		finished = true;
		notifyAll();
	}
	
	public abstract void execute() throws TaskAbortedException, InterruptedException;
}

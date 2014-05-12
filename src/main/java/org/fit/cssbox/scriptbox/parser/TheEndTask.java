/**
 * TheEndTask.java
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

package org.fit.cssbox.scriptbox.parser;

import java.util.Collection;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadyState;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.events.script.Event;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.window.Window;

/**
 * Class of the task which finalizes parsing of the document and fires load event.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/syntax.html#the-end">The end</a>
 */
public class TheEndTask extends ParserFinishedTask {

	protected Html5DocumentParser parser;
	
	public TheEndTask(Html5DocumentParser parser) {
		super(TaskSource.DOM_MANIPULATION, parser.getDocument());
		
		this.parser = parser;
	}

	@Override
	public void execute() throws TaskAbortedException, InterruptedException {
		Html5DocumentImpl document = getDocument();

		// 1) Set the current document readiness to "interactive" and the insertion point to undefined.
		document.setDocumentReadiness(DocumentReadyState.INTERACTIVE);
		
		// TODO: 2) Pop all the nodes off the stack of open elements.
		
		executeFirstOnFinishScript();
	}
	
	/*
	 * 3) If the list of scripts that will execute when the document has finished parsing is not empty
	 */
	protected void executeFirstOnFinishScript() throws TaskAbortedException {
		final List<Html5ScriptElementImpl> scripts = parser.getOnFinishScripts();
		if (!scripts.isEmpty()) {
			final Html5ScriptElementImpl script = scripts.get(0);
			getEventLoop().spinForCondition(new Runnable() {
				
				@Override
				public void run() {
					synchronized (parser) {
						while (!script.isReadyToBeParserExecuted()) {
							try {
								parser.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}, new Executable() {
				
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					script.executeScript();
					parser.removeOnFinishScript(script);
					
					List<Html5ScriptElementImpl> scripts = parser.getOnFinishScripts();
					
					if (!scripts.isEmpty()) {
						executeFirstOnFinishScript();
					}
					
					contentLoaded();
				}
			});
		}
		
		contentLoaded();
	}
	
	protected void contentLoaded() throws TaskAbortedException {		
		// 4) Queue a task to fire a simple event that bubbles named DOMContentLoaded at the Document.
		Html5DocumentImpl document = getDocument();
		Window window = document.getWindow();
		window.fireSimpleEvent("DOMContentLoaded", document, true, false);
		
		// 5) Spin the event loop until the set of scripts that will execute as soon as possible and 
		// the list of scripts that will execute in order as soon as possible are empty.
		getEventLoop().spinForCondition(new Runnable() {
			
			@Override
			public void run() {
				synchronized (parser) {
					Collection<Html5ScriptElementImpl> asapScripts = parser.getASAPScripts();
					Collection<Html5ScriptElementImpl> inOrderASAPScripts = parser.getInOrderASAPScripts();
					while (!asapScripts.isEmpty() || !inOrderASAPScripts.isEmpty()) {
						try {
							parser.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, new Executable() {
			
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				// 6) Spin the event loop until there is nothing that delays the load event in the Document.
				getEventLoop().spinForCondition(new Runnable() {
					
					@Override
					public void run() {
						Html5DocumentImpl document = getDocument();
						BrowsingContext context = document.getBrowsingContext();
						
						if (context instanceof IFrameContainerBrowsingContext) {
							IFrameContainerBrowsingContext container = (IFrameContainerBrowsingContext)context;

							synchronized (container) {
								boolean shouldWait = false;
								do {
									shouldWait = container.hasDelayingLoadEventsMode();
									
									if (shouldWait) {
										try {
											container.wait();
										} catch (InterruptedException e) {
											e.printStackTrace();
											throw new UnknownException(e);
										}
									}

								} while (shouldWait);
								
							}
						}
					}
				}, new Executable() {
					@Override
					public void execute() throws TaskAbortedException, InterruptedException {
						documentComplete();
					}
				});
				
			}
		});
	}
	
	protected void documentComplete() {
		// 7) Queue a task to run the following substeps
		getEventLoop().queueTask(new Task(getTaskSource(), getDocument()) {

			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				Html5DocumentImpl document = getDocument();
				Window window = document.getWindow();
				
				// 7.1) Set the current document readiness to "complete".
				document.setDocumentReadiness(DocumentReadyState.COMPLETE);
				
				Event event = new Event(true, document);
				event.initEvent("load", false, false);
				window.dispatchEvent(event);
			}
			
		});
		
		Task onFinishedTask = parser.getOnFinishedTask();
		if (onFinishedTask != null) {
			getEventLoop().queueTask(onFinishedTask);
		}
		
		// TODO: 8), 9), 10), 11), 12) steps
		Html5DocumentImpl document = getDocument();
		document.setPageShowingFlag(true);
	}
}
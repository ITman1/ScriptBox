package org.fit.cssbox.scriptbox.document.script;

import java.util.Collection;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.events.TrustedEventImpl;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;

/*
 * See: http://www.w3.org/html/wg/drafts/html/CR/syntax.html#the-end
 */
public class TheEndTask extends ParserFinishedTask {
	private class ParserFinishedTaskExceptionInjector extends ParserFinishedTask {
		protected ParserFinishedTask wrapped;
		
		public ParserFinishedTaskExceptionInjector(ParserFinishedTask wrapped) {
			super(wrapped.getTaskSource(), wrapped.getDocument());

			this.wrapped = wrapped;
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {}
		
		public void inject(Exception exception) {
			wrapped.exception = exception;
		}
	}
	
	protected ScriptableDocumentParser parser;
	
	public TheEndTask(ScriptableDocumentParser parser) {
		super(TaskSource.DOM_MANIPULATION, parser.getDocument());
		
		this.parser = parser;
	}

	@Override
	public void execute() throws TaskAbortedException, InterruptedException {
		Html5DocumentImpl document = getDocument();
		
		// 1) Set the current document readiness to "interactive" and the insertion point to undefined.
		document.setDocumentReadiness(DocumentReadiness.INTERACTIVE);
		
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
				}
			});
			return;
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
				// TODO?: 6) Spin the event loop until there is nothing that delays the load event in the Document.
				documentComplete();
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
				document.setDocumentReadiness(DocumentReadiness.COMPLETE);
				
				TrustedEventImpl event = new TrustedEventImpl();
				event.initEvent("load", false, false, true, document);
				window.dispatchEvent(event);
			}
			
		});
		
		ParserFinishedTask onFinishedTask = parser.getOnFinishedTask();
		if (onFinishedTask != null) {
			ParserFinishedTaskExceptionInjector injector = new ParserFinishedTaskExceptionInjector(onFinishedTask);
			injector.inject(exception);
			getEventLoop().queueTask(onFinishedTask);
		}
		
		// TODO: 8), 9), 10), 11), 12) steps
	}
}
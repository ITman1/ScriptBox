package org.fit.cssbox.scriptbox.resource.handlers;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5IFrameElement;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.navigation.UpdateSessionHistoryTask;
import org.fit.cssbox.scriptbox.resource.ResourceHandler;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.w3c.dom.Element;

public abstract class RenderedResourceHandler extends ResourceHandler {

	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#update-the-session-history-with-the-new-page
	 */
	protected class UpdateSessionHistoryTask extends Task {

		private Html5DocumentImpl newDocument;
		private boolean entryUpdate;
		
		public UpdateSessionHistoryTask(Html5DocumentImpl taskDocument, Html5DocumentImpl newDocument, boolean entryUpdate) {
			super(TaskSource.NETWORKING, taskDocument);
			
			this.newDocument = newDocument;
			this.entryUpdate = entryUpdate;
		}

		@Override
		public void execute() {
			Html5DocumentImpl oldDocument = getDocument();
			
			oldDocument.unload(false);
			// FIXME: If this instance of the navigation algorithm ...
		}
		
	}

	public RenderedResourceHandler(NavigationController navigationController) {
		super(navigationController);
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * See: http://www.w3.org/html/wg/drafts/html/CR/browsers.html#update-the-session-history-with-the-new-page
	 */
	protected void update(Html5DocumentImpl newDocument) {
		SessionHistoryEntry currentEntry = getCurrentEntry();
		Html5DocumentImpl taskDocument = (currentEntry != null)? currentEntry.getDocument() : null;
		context.getEventLoop().queueTask(new UpdateSessionHistoryTask(taskDocument, newDocument));
	}
	
	
	protected Html5DocumentImpl createDocument(BrowsingContext context, URL url) {
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		
		// 1) Create a new Window object, and associate it with the Document
		Html5DocumentImpl recycleWindowDocument = null;
		if (sessionHistory.getLength() == 1 && currentDocument != null && currentDocument.getAddress().equals(Html5DocumentImpl.DEFAULT_URL)) {
			recycleWindowDocument = currentDocument; 
		}
		
		// FIXME: 2) Set the document's referrer to the address of the resource from which Request-URIs 
		// are obtained as determined when the fetch algorithm obtained the resource
		Html5DocumentImpl newDocument = Html5DocumentImpl.createDocument(context, url, recycleWindowDocument);
		
		// 3) Implement the sandboxing for the Document.
		newDocument.implementSandboxing();
		
		// 4) ...
		boolean hasSandboxedFullscreen = false;
		BrowsingContext ancestorContext = context;
		do  {	
			Html5DocumentImpl activeDocument = ancestorContext.getActiveDocument();
			
			if (activeDocument.getActiveSandboxingFlagSet().contains(SandboxingFlag.FULLSCREEN_BROWSING_CONTEXT_FLAG)) {
				hasSandboxedFullscreen = true;
				break;
			}
			
			ancestorContext = ancestorContext.getCreatorContext();
		} while (ancestorContext != null);
		
		boolean fullscreenEnabled = true;
		
		Element contextContainer = context.getContainer();
		BrowsingContext parentContext = context.getParentContext();
		Html5DocumentImpl parentDocument = (parentContext != null)? parentContext.getActiveDocument() : null;
		fullscreenEnabled = fullscreenEnabled && (contextContainer == null || contextContainer instanceof Html5IFrameElement);
		fullscreenEnabled = fullscreenEnabled && (parentDocument == null || parentDocument.isFullscreenEnabledFlag());
		
		if (!hasSandboxedFullscreen && fullscreenEnabled) {
			newDocument.setEnableFullscreenFlag(true);
		}
		
		return newDocument;
	}
}

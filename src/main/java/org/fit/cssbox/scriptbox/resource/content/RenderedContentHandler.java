package org.fit.cssbox.scriptbox.resource.content;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocumentParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5IFrameElement;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.UpdateNavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.w3c.dom.Element;

public abstract class RenderedContentHandler extends ContentHandler {

	protected class ScrollToFragmentRunnable implements Executable {
		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			String fragment = navigationAttempt.getURL().getRef();
			
			if (fragment == null) {
				return;
			}
			
			if (context.scrollToFragment(fragment)) {
				return;
			}
			
			DocumentReadiness readiness = context.getActiveDocument().getDocumentReadiness();
			if (readiness.equals(DocumentReadiness.LOADING)) {
				context.getEventLoop().spinForAmountTime(300, this);
			}
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#update-the-session-history-with-the-new-page
	 */
	protected class UpdateSessionHistoryTask extends Task {

		private Html5DocumentImpl newDocument;
		
		public UpdateSessionHistoryTask(Html5DocumentImpl taskDocument, Html5DocumentImpl newDocument) {
			super(TaskSource.NETWORKING, taskDocument);
			
			this.newDocument = newDocument;
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			Html5DocumentImpl oldDocument = getDocument();
			
			// 1) Unload the document
			oldDocument.unload(false);

			// 2) If the navigation was initiated for entry update then update otherwise insert new one
			if (navigationAttempt instanceof UpdateNavigationAttempt) {
				SessionHistoryEntry updateEntry = ((UpdateNavigationAttempt)navigationAttempt).getSessionHistoryEntry();
				SessionHistory sessionHistory = updateEntry.getSessionHistory();
						
				// TODO: Update also any other entries that referenced the same document as that entry
				updateEntry.setSocument(newDocument);
				sessionHistory.traverseHistory(updateEntry);
			} else {
				SessionHistory sessionHistory = context.getSesstionHistory();
				SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
				
				sessionHistory.removeAllAfter(currentEntry);
				
				SessionHistoryEntry newEntry = new SessionHistoryEntry(sessionHistory);
				
				Resource resource = navigationAttempt.getResource();
				URL entryAddress = (resource != null)? resource.getAddress() : newDocument.getAddress();
				newEntry.setURL(entryAddress);
				newEntry.setSocument(newDocument);
				sessionHistory.add(newEntry);
				
				sessionHistory.traverseHistory(newEntry, navigationAttempt.hasReplacementEnabled());
			}
						
			// 3) The navigation algorithm has now matured
			navigationAttempt.mature();
			
			ScrollToFragmentRunnable performScrolling = new ScrollToFragmentRunnable();
			performScrolling.execute();
		}
		
	}

	public RenderedContentHandler(NavigationAttempt navigationAttempt) {
		super(navigationAttempt);
	}
	
	/*
	 * See: http://www.w3.org/html/wg/drafts/html/CR/browsers.html#update-the-session-history-with-the-new-page
	 */
	protected void updateSessionHistory(Html5DocumentImpl newDocument) {
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl taskDocument = (currentEntry != null)? currentEntry.getDocument() : null;
		context.getEventLoop().queueTask(new UpdateSessionHistoryTask(taskDocument, newDocument));
	}
	
	
	protected Html5DocumentImpl createDocument(BrowsingContext context, URL url, String mimeType, ScriptableDocumentParser parser) {
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		
		// 1) Create a new Window object, and associate it with the Document
		Html5DocumentImpl recycleWindowDocument = null;
		if (sessionHistory.getLength() == 1 && currentDocument != null && currentDocument.hasDefaultAddress()) {
			recycleWindowDocument = currentDocument; 
		}
		
		// FIXME: 2) Set the document's referrer to the address of the resource from which Request-URIs 
		// are obtained as determined when the fetch algorithm obtained the resource
		Html5DocumentImpl newDocument = Html5DocumentImpl.createDocument(context, url, recycleWindowDocument, mimeType, parser);
		
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

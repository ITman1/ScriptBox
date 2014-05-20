/**
 * RenderedContentHandler.java
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

package org.fit.cssbox.scriptbox.resource.content;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadyState;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5IFrameElement;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerListener;
import org.fit.cssbox.scriptbox.navigation.UpdateNavigationAttempt;
import org.fit.cssbox.scriptbox.parser.Html5DocumentParser;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.w3c.dom.Element;

/**
 * Abstract class for all content handlers - that parses documents.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class DOMContentHandler extends ContentHandler {
	
	/*
	 * Proceeds 1), 2) and 3) steps of the update session history with the new page:
	 * http://www.w3.org/html/wg/drafts/html/master/browsers.html#update-the-session-history-with-the-new-page
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
			navigationAttempt.unloadDocument(oldDocument);

			// 2) If the navigation was initiated for entry update then update otherwise insert new one
			SessionHistory sessionHistory = null;
			if (navigationAttempt instanceof UpdateNavigationAttempt) {
				SessionHistoryEntry updateEntry = ((UpdateNavigationAttempt)navigationAttempt).getSessionHistoryEntry();
				sessionHistory = updateEntry.getSessionHistory();
						
				// TODO?: Update also any other entries that referenced the same document as that entry
				updateEntry.setDocument(newDocument);
				sessionHistory.traverseHistory(updateEntry);
			} else {
				sessionHistory = context.getSesstionHistory();
				SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
				
				sessionHistory.removeAllAfter(currentEntry);
				
				SessionHistoryEntry newEntry = new SessionHistoryEntry(sessionHistory);
				
				Resource resource = navigationAttempt.getResource();
				URL entryAddress = (resource != null)? resource.getAddress() : newDocument.getAddress();
				newEntry.setURL(entryAddress);
				newEntry.setDocument(newDocument);
				sessionHistory.add(newEntry);
				
				sessionHistory.traverseHistory(newEntry, navigationAttempt.hasReplacementEnabled());
			}
						
			// 3) The navigation algorithm has now matured
			navigationAttempt.mature();
			
			SessionHistoryEntry newEntry = sessionHistory.getCurrentEntry();
			if (!newEntry.hasPersistedUserState()) {
				ScrollToFragmentRunnable performScrolling = new ScrollToFragmentRunnable();
				performScrolling.execute();
			}
		}
	}
	
	/*
	 * Proceeds 4), 5) and 6) steps of the update session history with the new page:
	 * http://www.w3.org/html/wg/drafts/html/master/browsers.html#update-the-session-history-with-the-new-page
	 */
	private class ScrollToFragmentRunnable implements Executable {
		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			String fragment = navigationAttempt.getURL().getRef();
			
			if (fragment == null) {
				return;
			}
			
			if (!(context instanceof IFrameContainerBrowsingContext)) {
				return;
			}
			
			if (((IFrameContainerBrowsingContext)context).scrollToFragment(fragment)) {
				return;
			}
			
			DocumentReadyState readiness = context.getActiveDocument().getDocumentReadiness();
			if (readiness.equals(DocumentReadyState.LOADING)) {
				context.getEventLoop().spinForAmountTime(300, this);
			}
		}
	}
	
	/*
	 * Listener that listens whether was navigation cancelled.
	 * If yes, then aborts created document.
	 */
	private NavigationControllerListener abortDocumentListener = new NavigationControllerListener() {
		
		@Override
		public void onNavigationEvent(NavigationControllerEvent event) {
			NavigationAttempt attempt = event.getNavigationAttempt();
			
			if (attempt == navigationAttempt) {
				switch (event.getEventType()) {
				case NAVIGATION_NEW:
					break;
				case DESTROYED:
				case NAVIGATION_CANCELLED:
					if (renderableDocument != null) {
						renderableDocument.abort();
					}
				case NAVIGATION_COMPLETED:
				case NAVIGATION_MATURED:
					navigationController.removeListener(abortDocumentListener);
					break;
				}
			}
		}
	};
	
	protected Html5DocumentImpl renderableDocument;

	/**
	 * Constructs rendered content handler for a given navigation attempt.
	 * 
	 * @param navigationAttempt Navigation attempt which invoked this content handler.
	 */
	public DOMContentHandler(NavigationAttempt navigationAttempt) {
		super(navigationAttempt);

		navigationController.addListener(abortDocumentListener);
	}
	
	/**
	 * Updates session history with a new document.
	 * 
	 * @param newDocument New document that should be inserted into history.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#update-the-session-history-with-the-new-page">Update the session history with the new page</a>
	 */
	protected void updateSessionHistory(Html5DocumentImpl newDocument) {
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl taskDocument = (currentEntry != null)? currentEntry.getDocument() : null;
		context.getEventLoop().queueTask(new UpdateSessionHistoryTask(taskDocument, newDocument));
	}
		
	/**
	 * Returns or constructs document for this content handler.
	 * 
	 * @param context Browsing context where to put document.
	 * @param url Address of the document.
	 * @param mimeType MIME type of the document.
	 * @param parser Parser which ensures parsing of the document.
	 * @return New or already constructed document for this rendered content handler.
	 */
	protected Html5DocumentImpl getRenderableDocument(BrowsingContext context, URL url, String mimeType, Html5DocumentParser parser) {
		if (renderableDocument != null) {
			return renderableDocument;
		}
		
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		Html5DocumentImpl currentDocument = currentEntry.getDocument();
		
		// 1) Create a new Window object, and associate it with the Document
		Html5DocumentImpl recycleWindowDocument = null;
		if (sessionHistory.getLength() == 1 && currentDocument != null && currentDocument.hasDefaultAddress() && navigationAttempt.hasReplacementEnabled() && currentEntry.isDefaultEntry()) {
			recycleWindowDocument = currentDocument; // FIXME: According to specification there should be also origin check, but we do know it yet, it is known after 2nd step
		}
		
		// FIXME: 2) Set the document's referrer to the address of the resource from which Request-URIs 
		// are obtained as determined when the fetch algorithm obtained the resource
		renderableDocument = Html5DocumentImpl.createDocument(context, url, recycleWindowDocument, mimeType, parser);
		
		// 3) Implement the sandboxing for the Document.
		renderableDocument.implementSandboxing();
		
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
			renderableDocument.setEnableFullscreenFlag(true);
		}
		
		return renderableDocument;
	}
}

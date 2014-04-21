/**
 * NavigationAttempt.java
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

package org.fit.cssbox.scriptbox.navigation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerRegistry;
import org.fit.cssbox.scriptbox.resource.content.ErrorHandler;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchRegistry;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;

import com.google.common.base.Predicate;

public abstract class NavigationAttempt {
	protected class AsyncPerformThread extends Thread {
		@Override
		public void run() {
			// 17) Let gone async be true.
			goneAsync = true;
			
			try {
				performFromHandleRedirects();
			} catch (InterruptedException e) {
				onCancelled();
			} finally {
				synchronized (NavigationAttempt.this) {
					asyncPerformThread = null;
				}
			}
		}
		
		@Override
		public String toString() {
			String sourceUrl = (url != null)? url.toExternalForm() : "(no url)";
			return "NavigationAttempt Thread - " + sourceUrl;
		}
	};
	
	protected final static NavigationAttemptListener EMPTY_NAVIGATION_ATTEMPT_LISTENER = new NavigationAttemptListener() {
		
		@Override
		public void onMatured(NavigationAttempt attempt) {}
		
		@Override
		public void onEffectiveDestinationContextSelected(NavigationAttempt attempt, BrowsingContext context) {}
		
		@Override
		public void onCancelled(NavigationAttempt attempt) {}

		@Override
		public void onCompleted(NavigationAttempt attempt) {}
	};
	
	protected final Predicate<NavigationAttempt> nonEqualOriginPredicate = new Predicate<NavigationAttempt>() {
		
		@Override
		public boolean apply(NavigationAttempt attempt) {
			if (sourceBrowsingContext == destinationBrowsingContext && attempt.runningUnloadDocument) {
				UrlOrigin urlOrigin = new UrlOrigin(url);
				UrlOrigin runningNavigateUrlOrigin = new UrlOrigin(attempt.getURL());
				if (!urlOrigin.equals(runningNavigateUrlOrigin)) {
					return true;
				}
			}
			return false;
		}
	};
	
	protected boolean completed;
	protected boolean matured;
	protected boolean cancelled;
	protected AsyncPerformThread asyncPerformThread;
	protected NavigationController navigationController;
	protected BrowsingContext sourceBrowsingContext;
	protected boolean exceptionEnabled;
	protected boolean explicitSelfNavigationOverride;
	protected boolean replacementEnabled;
	protected URL url;
	protected boolean isUnloadRunning;
	
	protected boolean runningUnloadDocument;

	protected BrowsingContext destinationBrowsingContext;
	protected boolean goneAsync;
	protected List<Fetch> fetches;
	protected FetchRegistry fetchRegistry;
	protected ContentHandlerRegistry resourceHandlerRegistry;
	protected Resource resource;
	protected NavigationAttemptListener listener;
	
	public NavigationAttempt(NavigationController navigationController, BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride, boolean replacementEnabled) {
		this.navigationController = navigationController;
		this.sourceBrowsingContext = sourceBrowsingContext;
		this.url = url;
		this.exceptionEnabled = exceptionEnabled;
		this.explicitSelfNavigationOverride = explicitSelfNavigationOverride;
		this.replacementEnabled = replacementEnabled;
		this.fetches = new ArrayList<Fetch>();
		this.fetchRegistry = FetchRegistry.getInstance();
		this.resourceHandlerRegistry = ContentHandlerRegistry.getInstance();
		this.listener = EMPTY_NAVIGATION_ATTEMPT_LISTENER;
	}
	
	public NavigationController getNavigationController() {
		return navigationController;
	}
	
	public BrowsingContext getDestinationBrowsingContext() {
		return destinationBrowsingContext;
	}
	
	public BrowsingContext getSourceBrowsingContext() {
		return sourceBrowsingContext;
	}
	
	public boolean hasExceptionEnabled() {
		return exceptionEnabled;
	}
	
	public boolean hasExplicitSelfNavigationOverride() {
		return explicitSelfNavigationOverride;
	}
	
	public boolean hasReplacementEnabled() {
		return replacementEnabled;
	}
	
	public synchronized boolean isCompleted() {
		return completed;
	}
	
	public void unloadDocument(Html5DocumentImpl document) {
		synchronized (this) {
			isUnloadRunning = true;
		}
		
		document.unload(false);
		
		synchronized (this) {
			isUnloadRunning = true;
		}
	}
	
	public synchronized boolean isUnloadRunning() {
		return isUnloadRunning;
	}
	
	public void complete() {
		boolean wasCompleted = false;
		boolean isCancelled = false;
		synchronized (this) {
			isCancelled = cancelled;
			wasCompleted = completed;
			completed = true;
		}
		
		if (!isCancelled && !wasCompleted) {
			onCompleted();
		}
	}
	
	public synchronized boolean isMatured() {
		return matured;
	}
	
	public void mature() {
		boolean wasMatured = false;
		boolean isCancelled = false;
		synchronized (this) {
			isCancelled = cancelled;
			wasMatured = matured;
			matured = true;
		}

		if (!isCancelled && !wasMatured) {
			onMatured();
		}
	}
	
	public synchronized Resource getResource() {
		return resource;
	}
	
	public synchronized URL getURL() {
		return url;
	}
	
	public synchronized boolean isCancelled() {
		return cancelled;
	}
	
	public synchronized void cancel() {
		if (!cancelled && !completed) {
			cancelled = true;
			
			// if is already running asynchronous thread then abort it
			if (asyncPerformThread != null) {
				asyncPerformThread.interrupt();
			} else {
				onCancelled();
			}
			
			for (Fetch fetch : fetches) {
				try {
					fetch.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public synchronized String getContentType() {
		return (resource != null)? resource.getContentType() : null;
	}
	
	// Long duration process - no synchronized
	
	public void perform() {
		perform(EMPTY_NAVIGATION_ATTEMPT_LISTENER);
	}
	
	/*
	 * See: http://www.w3.org/html/wg/drafts/html/CR/browsers.html#navigate
	 */
	public void perform(NavigationAttemptListener listener) {
		if (cancelled) {
			return;
		}
		
		this.listener = listener;
		
		destinationBrowsingContext = navigationController.getBrowsingContext();
		
		// TODO: 1) Release the storage mutex.
		
		// 2) Check if the source is allowed to navigate the destination context.
		/*
		 * TODO?:  If these steps are aborted here, the user agent may instead offer to open the new resource 
	 	 * in a new top-level browsing context or in the top-level browsing context of the source browsing context
		 */
		if (!sourceBrowsingContext.isAllowedToNavigate(destinationBrowsingContext)) {
			if (exceptionEnabled) {
				throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
			}
			return;
		}
		
		// 3) Selects effective destination browsing context that will be actually used for the navigation
		destinationBrowsingContext = selectEffectiveDestinationContext(destinationBrowsingContext);
		onEffectiveDestinationContextSelected(destinationBrowsingContext);

			
		// 4) If there is already navigation attempt running with the different origin then abort
		if (navigationController.existsNavigationAttempt(nonEqualOriginPredicate)) {
			onCancelled();
			return;
		}
		
		Html5DocumentImpl destinationActiveDocument = destinationBrowsingContext.getActiveDocument();
		
		// 5) If traverse the history by a delta algorithm stepped to unload active document then abort
		synchronized (destinationActiveDocument) {
			Task unloadTask = destinationActiveDocument.getUnloadTask();
			if (unloadTask != null && unloadTask.getTaskSource() == TaskSource.HISTORY_TRAVERSAL) {
				onCancelled();
				return;
			}
		}
		
		// 6) If prompt to unload above active document is running then abort
		if (destinationActiveDocument.isPromptToUnloadRunning()) {
			onCancelled();
			return;
		}
		
		// 7) Let gone async be false.	
		goneAsync = false;
		
		try {
			performFromFragmentIdentifiers();
		} catch (InterruptedException e) {
			onCancelled();
		}
	}
	
	protected void performFromFragmentIdentifiers() throws InterruptedException {
		testForInterruption();
		
		// 8) Apply the URL parser for new and old resource and if only fragment is different then navigate to fragment only
		if (shouldBeFragmentNavigated()) {
			String fragment = url.getRef();
			navigateToFragmentIdentifier(fragment);
			complete();
			return;
		}
				
		testForInterruption();
		
		// 9) Accept new navigation attempt and abort all currently running
		if (goneAsync == false) {
			navigationController.cancelAllNonMaturedNavigationAttempts(destinationBrowsingContext, this);
		}
				
		testForInterruption();
		
		Html5DocumentImpl currentDocument = destinationBrowsingContext.getActiveDocument();
		
		// 10) Abort if new navigation does not affects the destination browsing context
		if (!affectsBrowsingContext()) {
			asyncCancel();
			return;
		}
				
		testForInterruption();
		
		// 11) Prompt to unload an old document
		if (goneAsync == false && currentDocument.promptToUnload() == false) {
			asyncCancel();
			return;
		}
		
		testForInterruption();
				
		// 12) Abort an old document.
		if (goneAsync == false) {
			currentDocument.abort();
		}
		
		testForInterruption();
		
		// 13) If resource is not fetchable then apply corresponding handler and abort
		if (!fetchRegistry.isFetchable(url)) {
			handleUnableToFetch();
			asyncCancel();
			return;
		}
		
		testForInterruption();
		
		// 14) If we are navigating into iframe context then delay load of whole page
		if (destinationBrowsingContext instanceof IFrameBrowsingContext) {
			((IFrameBrowsingContext)destinationBrowsingContext).delayLoadEvents();
		}
		
		testForInterruption();
		
		// 15) Obtain the resource, on failure abort
		// TODO: If the resource has already been obtained then skip
		/*
		 * TODO: If the resource is being fetched using a method other than one equivalent to HTTP's GET, 
		 * or, if the navigation algorithm was invoked as a result of the form submission algorithm, then 
		 * the fetching algorithm must be invoked from the origin of the active document of the source browsing context, if any.
		 * Otherwise, if the browsing context being navigated is a child browsing context, then the fetching 
		 * algorithm must be invoked from the browsing context scope origin of the browsing context container 
		 * of the browsing context being navigated, if it has one.
		 */
		synchronized (this) {
			resource = obtainResource();
			if (resource == null) {
				asyncCancel();
				return;
			}
		}
		
		testForInterruption();
		
		// 16) If gone async is false, return and continue asynchronously
		if (goneAsync == false) {
			synchronized (this) {
				asyncPerformThread = new AsyncPerformThread();
				asyncPerformThread.start();
				return;
			}
		} else {		
			performFromHandleRedirects();
		}
	}
	
	protected void performFromHandleRedirects() throws InterruptedException {
		testForInterruption();
		
		// 18) Handle redirects and abort on different origins
		boolean shouldRedirect = false;
		boolean isRedirectValid = false;
		synchronized (this) {
			shouldRedirect = resource.shouldRedirect();
			isRedirectValid = resource.isRedirectValid();
		}
		
		if (shouldRedirect) {
			if (isRedirectValid) {
				url = resource.getRedirectUrl();
				performFromFragmentIdentifiers();
			} else {
				// TODO: Maybe throw an security error.
				asyncCancel();
				return;
			}
		}
		
		testForInterruption();
		
		// 19) Wait for incoming byte(s) or abort if resource is empty
		// FIXME: Wait for 10 seconds - reach it from constant or from User agent settings
		synchronized (this) {
			if (!resource.waitForBytes(10000)) {
				// TODO: Throw timeout or similar exception
				asyncCancel();
				return;
			}
		}
		
		testForInterruption();
		
		// 20) TODO: Fallback in prefer-online mode
		
		// 21) TODO: Fallback for fallback entries
				
		performFromResourceHandling();
	}
	
	protected void performFromResourceHandling() throws InterruptedException {
		testForInterruption();
		
		// 22) Handle resources that are not valid (e.g. do not contain metadata and the content) and attachments
		synchronized (this) {
			if (!resource.isContentValid()) {
				ContentHandler errorHandler = resource.getErrorHandler();
				
				if (errorHandler != null) {
					errorHandler.process(resource);
				}
				
				asyncCancel();
				return;
			}
		}
		
		testForInterruption();
		
		synchronized (this) {
			if (resource.isAttachment()) {
				downloadResource();
			}
		}
		
		testForInterruption();
		
		// 23) Get content type.
		synchronized (this) {
			String contentType = resource.getContentType();
			if (contentType == null) {
				// TODO: Maybe throw an exception.
				asyncCancel();
				return;
			}
		}
		
		testForInterruption();
		
		// 24) and 25) Handling of document and inline contents is merged here, registry is delegated for the distinction
		ContentHandler handler = resourceHandlerRegistry.getHandlerForNavigationAttempt(this);
		if (handler != null) {
			handler.process(resource);
			return;
		} else {
			// 26) If unknown type then download the resource
			downloadResource();
		}
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#scroll-to-fragid
	 */
	protected void navigateToFragmentIdentifier(String fragment) {
		SessionHistory sessionHistory = destinationBrowsingContext.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		
		// 1) Remove all the entries in the browsing context's session history after the current entry
		
		sessionHistory.removeAllAfterCurrentEntry();
		
		// 2) Remove any tasks queued by Document family object
		
		sessionHistory.removeAllTopLevelDocumentFamilyTasks();
		
		// 3) Append a new entry at the end of the History object 
		
		SessionHistoryEntry newEntry = new SessionHistoryEntry(sessionHistory);
		
		newEntry.setDocument(currentEntry.getDocument());
		newEntry.setURL(url);
		newEntry.setBrowsingContextName(currentEntry.getBrowsingContextName());
		newEntry.setStateObject(currentEntry.getStateObject());
		
		sessionHistory.add(newEntry);
		
		// 4) Traverse the history to the new entry
		
		sessionHistory.traverseHistory(newEntry, false, true);
	}
	
	/*
	 * TODO: See http://www.w3.org/html/wg/drafts/html/CR/links.html#as-a-download
	 */
	protected void downloadResource() {
		
	}
	
	protected Resource obtainResource() {
		Fetch fetch = fetchRegistry.getFetch(sourceBrowsingContext, destinationBrowsingContext, url, true, true, true, null);
		
		if (fetch == null) {
			return null;
		}
		
		fetches.add(fetch);
		
		try {
			fetch.fetch();
		} catch (IOException e) {
			// TODO: Maybe throw exception.
			return null;
		}
		
		Resource resource = fetch.getResource();
		if (resource == null) {
			// TODO: Maybe throw exception.
			return null;
		}
		
		return resource;
	}
	
	protected void handleUnableToFetch() {
		ErrorHandler errorHandler = resourceHandlerRegistry.getErrorHandler(this);
		
		if (errorHandler != null) {
			errorHandler.handle(url);
		}
	}
	
	protected boolean shouldBeFragmentNavigated() {
		Html5DocumentImpl currentDocument = destinationBrowsingContext.getActiveDocument();
		URL currentURL = currentDocument.getAddress();
		boolean identicalUrls = URLUtilsHelper.identicalComponents(url, currentURL, UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, UrlComponent.PATH, UrlComponent.QUERY);
				
		String fragment = url.getRef();
		if (identicalUrls && fragment != null) {
			return true;
		}
		
		return false;
	}
	
	protected synchronized void asyncCancel() throws InterruptedException {
		if (!cancelled && !completed) {
			if (asyncPerformThread == Thread.currentThread()) {
				throw new InterruptedException();
			}
			
			cancel();
		}
	}
	
	protected void onEffectiveDestinationContextSelected(BrowsingContext destinationBrowsingContext) {
		fireEffectiveDestinationContextSelected(destinationBrowsingContext);
	}
	
	protected void onCompleted() {
		resetDelayingLoadEventsMode();
		fireCompleted();
	}
	
	protected void onMatured() {
		resetDelayingLoadEventsMode();
		fireMatured();
	}
	
	protected void onCancelled() {
		resetDelayingLoadEventsMode();
		fireCancelled();
	}
	
	private void resetDelayingLoadEventsMode() {
		if (destinationBrowsingContext instanceof IFrameBrowsingContext) {
			IFrameBrowsingContext context = (IFrameBrowsingContext)destinationBrowsingContext;

			if (context.hasDelayingLoadEventsMode()) {
				context.resetDelayingLoadEventsMode();
			}
		}
	}
	
	private void fireEffectiveDestinationContextSelected(BrowsingContext destinationBrowsingContext) {
		listener.onEffectiveDestinationContextSelected(this, destinationBrowsingContext);
	}
	
	private void fireCompleted() {
		listener.onCompleted(this);
	}
	
	private void fireMatured() {
		listener.onMatured(this);
	}
	
	private void fireCancelled() {
		EventLoop eventLoop = sourceBrowsingContext.getEventLoop();
		Thread loopThread = eventLoop.getEventThread();
		
		if (Thread.currentThread() == loopThread) {
			listener.onCancelled(NavigationAttempt.this);
		} else {
			eventLoop.queueTask(new Task(TaskSource.NETWORKING, sourceBrowsingContext) {
				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					listener.onCancelled(NavigationAttempt.this);
				}
			});
		}
	}
	
	protected boolean affectsBrowsingContext() {	
		return resourceHandlerRegistry.existsErrorHandler(url);
	}
	
	protected synchronized void testForInterruption() throws InterruptedException {
		if (asyncPerformThread != null && asyncPerformThread.isInterrupted()) {
			throw new InterruptedException();
		}
	}
	
	protected BrowsingContext selectEffectiveDestinationContext(BrowsingContext navigatedContext) {
		if (sourceBrowsingContext == navigatedContext && hasSeamlessFlag(navigatedContext) && !explicitSelfNavigationOverride) {
			while (navigatedContext.getCreatorContext() != null) {
				navigatedContext = navigatedContext.getCreatorContext();
				
				if (hasSeamlessFlag(navigatedContext)) {
					break;
				}
			}
		}
		
		return navigatedContext;
	}
	
	public static boolean hasSeamlessFlag(BrowsingContext context) {
		if (context instanceof IFrameBrowsingContext) {
			if (((IFrameBrowsingContext)context).getSeamlessFlag()) {
				return true;
			}
		}
		return false;
	}
}

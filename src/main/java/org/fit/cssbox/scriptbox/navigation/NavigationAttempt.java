package org.fit.cssbox.scriptbox.navigation;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerRegistry;
import org.fit.cssbox.scriptbox.resource.content.ErrorHandler;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchRegistry;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.UrlUtils;
import org.fit.cssbox.scriptbox.url.UrlUtils.UrlComponent;

import com.google.common.base.Predicate;

public abstract class NavigationAttempt {
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
	protected Thread asyncPerformThread;
	protected NavigationController navigationController;
	protected BrowsingContext sourceBrowsingContext;
	protected boolean exceptionEnabled;
	protected boolean explicitSelfNavigationOverride;
	protected boolean replacementEnabled;
	protected URL url;
	
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
	
	public void complete() {
		synchronized (this) {
			completed = true;
		}
		
		listener.onCompleted(this);
	}
	
	public synchronized boolean isMatured() {
		return matured;
	}
	
	public void mature() {
		synchronized (this) {
			matured = true;
		}

		listener.onMatured(this);
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
		cancelled = true;
		
		// if is already running asynchronous thread then abort it
		if (asyncPerformThread != null) {
			asyncPerformThread.interrupt();
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
		if (!isAllowedToNavigate(sourceBrowsingContext, destinationBrowsingContext)) {
			if (exceptionEnabled) {
				throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
			}
			return;
		}
		
		// 3) Selects effective destination browsing context that will be actually used for the navigation
		destinationBrowsingContext = selectEffectiveDestinationContext(destinationBrowsingContext);
		listener.onEffectiveDestinationContextSelected(this, destinationBrowsingContext);

			
		// 4) If there is already navigation attempt running with the different origin then abort
		if (navigationController.existsNavigationAttempt(nonEqualOriginPredicate)) {
			fireCancelled();
			return;
		}
		
		Html5DocumentImpl destinationActiveDocument = destinationBrowsingContext.getActiveDocument();
		
		// 5) If unload above active document is running then abort
		if (destinationActiveDocument.isUnloadRunning()) {
			fireCancelled();
			return;
		}
		
		// 6) If prompt to unload above active document is running then abort
		if (destinationActiveDocument.isPromptToUnloadRunning()) {
			fireCancelled();
			return;
		}
		
		// 7) Let gone async be false.	
		goneAsync = false;
		
		try {
			performFromFragmentIdentifiers();
		} catch (InterruptedException e) {
			fireCancelled();
		}
	}
	
	protected void performFromFragmentIdentifiers() throws InterruptedException {
		testForInterruption();
		
		// 8) Apply the URL parser for new and old resource and if only fragment is different then navigate to fragment only
		if (shouldBeFragmentNavigated()) {
			String fragment = url.getRef();
			destinationBrowsingContext.scrollToFragment(fragment);
			complete();
			return;
		}
				
		testForInterruption();
		
		// 9) Accept new navigation attempt and abort all currently running
		// FIXME?: Abort document if already exists and all fetches
		if (goneAsync == false) {
			navigationController.cancelNavigationAttempts(new Predicate<NavigationAttempt>() {
				
				@Override
				public boolean apply(NavigationAttempt arg) {
					return arg != NavigationAttempt.this;
				}
			});
		}
				
		testForInterruption();
		
		Html5DocumentImpl currentDocument = destinationBrowsingContext.getActiveDocument();
		
		// 10) Abort if new navigation does not affects the destination browsing context
		if (!affectsBrowsingContext()) {
			fireCancelled();
			return;
		}
				
		testForInterruption();
		
		// 11) Prompt to unload an old document
		if (goneAsync == false && currentDocument.promptToUnload() == false) {
			fireCancelled();
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
			fireCancelled();
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
				fireCancelled();
				return;
			}
		}
		
		testForInterruption();
		
		// 16) If gone async is false, return and continue asynchronously
		if (goneAsync == false) {
			synchronized (this) {
				asyncPerformThread = new Thread() {
					@Override
					public void run() {
						// 17) Let gone async be true.
						goneAsync = true;
						
						try {
							performFromHandleRedirects();
						} catch (InterruptedException e) {
							fireCancelled();
						} finally {
							synchronized (NavigationAttempt.this) {
								asyncPerformThread = null;
							}
						}
					}
				};
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
				fireCancelled();
				return;
			}
		}
		
		testForInterruption();
		
		// 19) Wait for incoming byte(s) or abort if resource is empty
		// FIXME: Wait for 10 seconds - reach it from constant or from User agent settings
		synchronized (this) {
			if (!resource.waitForBytes(10000)) {
				// TODO: Throw timeout or similar exception
				fireCancelled();
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
				
				fireCancelled();
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
				fireCancelled();
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
	 * TODO: See http://www.w3.org/html/wg/drafts/html/CR/links.html#as-a-download
	 */
	protected void downloadResource() {
		
	}
	
	protected Resource obtainResource() {
		Fetch fetch = fetchRegistry.getFetch(sourceBrowsingContext, destinationBrowsingContext, url);
		
		if (fetch == null) {
			return null;
		}
		
		fetches.add(fetch);
		
		try {
			fetch.fetch(true);
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
		boolean identicalUrls = UrlUtils.identicalComponents(url, currentURL, UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, UrlComponent.PATH, UrlComponent.QUERY);
				
		String fragment = url.getRef();
		if (identicalUrls && fragment != null) {
			return true;
		}
		
		return false;
	}
	
	protected void fireCancelled() {
		sourceBrowsingContext.getEventLoop().queueTask(new Task(TaskSource.NETWORKING, sourceBrowsingContext) {
			
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				listener.onCancelled(NavigationAttempt.this);
			}
		});
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
	
	/*
	 * FIXME: Rename local variable to something more clear than a, b, c
	 */
	public static boolean isAllowedToNavigate(BrowsingContext sourceBrowsingContext, BrowsingContext destinationBrowsingContext) {
		BrowsingContext a = sourceBrowsingContext;
		BrowsingContext b = destinationBrowsingContext;
		
		if (a != b && !a.isAncestorOf(b) && !b.isTopLevelBrowsingContext() && a.getActiveDocument().
				getActiveSandboxingFlagSet().contains(SandboxingFlag.NAVIGATION_BROWSING_CONTEXT_FLAG)) {
			return false;
		}
		
		if (b.isTopLevelBrowsingContext() && b.isAncestorOf(a) && a.getActiveDocument().
				getActiveSandboxingFlagSet().contains(SandboxingFlag.TOPLEVEL_NAVIGATION_BROWSING_CONTEXT_FLAG)) {
			return false;
		}
		
		/*
		 * TODO: Otherwise, if B is a top-level browsing context, and is neither A 
		 * nor one of the ancestor browsing contexts of A, and A's Document's active 
		 * sandboxing flag set has its sandboxed navigation browsing context flag set, 
		 * and A is not the one permitted sandboxed navigator of B, then abort these steps negatively.
		 */
		//if (b.isTopLevelBrowsingContext() && a != b && !b.isAncestorOf(a) && a.getActiveDocument().)
		
		return true;
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

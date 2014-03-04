package org.fit.cssbox.scriptbox.navigation;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
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
	
	protected boolean matured;
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
	
	public boolean isMatured() {
		return matured;
	}
	
	public void mature() {
		matured = true;
		listener.onMatured(this);
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
	
	public URL getURL() {
		return url;
	}
	
	public void perform() {
		perform(EMPTY_NAVIGATION_ATTEMPT_LISTENER);
	}
	
	public void cancel() {
		listener.onCancelled(this);
	}
	
	public String getContentType() {
		return (resource != null)? resource.getContentType() : null;
	}
	
	/*
	 * See: http://www.w3.org/html/wg/drafts/html/CR/browsers.html#navigate
	 */
	public void perform(NavigationAttemptListener listener) {
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
				// TODO: Throw SecurityError exception.
			}
			return;
		}
		
		// 3) Selects effective destination browsing context that will be actually used for the navigation
		destinationBrowsingContext = selectEffectiveDestinationContext(destinationBrowsingContext);
		listener.onEffectiveDestinationContextSelected(this, destinationBrowsingContext);

			
		// 4) If there is already navigation attempt running with the different origin then abort
		if (navigationController.existsNavigationAttempt(nonEqualOriginPredicate)) {
			return;
		}
		
		Html5DocumentImpl destinationActiveDocument = destinationBrowsingContext.getActiveDocument();
		
		// 5) If unload above active document is running then abort
		if (destinationActiveDocument.isUnloadRunning()) {
			return;
		}
		
		// 6) If prompt to unload above active document is running then abort
		if (destinationActiveDocument.isPromptToUnloadRunning()) {
			return;
		}
		
		// 7) Let gone async be false.	
		goneAsync = false;
		
		performFromFragmentIdentifiers();
	}
	
	protected void performFromFragmentIdentifiers() {
		// 8) Apply the URL parser for new and old resource and if only fragment is different then navigate to fragment only
		if (shouldBeFragmentNavigated()) {
			navigateToFragment(url.getRef());
			return;
		}
				
		// 9) Accept new navigation attempt and abort all currently running
		// FIXME?: Abort document if already exists and all fetches
		if (goneAsync == false) {
			navigationController.cancelAllNavigationAttempts();
		}
				
		Html5DocumentImpl currentDocument = destinationBrowsingContext.getActiveDocument();
		
		// 10) Abort if new navigation does not affects the destination browsing context
		if (!affectsBrowsingContext()) {
			return;
		}
				
		// 11) Prompt to unload an old document
		if (goneAsync == false && currentDocument.promptToUnload() == false) {
			return;
		}
				
		// 12) Abort an old document.
		if (goneAsync == false) {
			currentDocument.abort();
		}
		
		// 13) If resource is not fetchable then apply corresponding handler and abort
		if (!fetchRegistry.isFetchable(url)) {
			handleUnableToFetch();
			return;
		}
		
		// 14) If we are navigating into iframe context then delay load of whole page
		if (destinationBrowsingContext instanceof IFrameBrowsingContext) {
			((IFrameBrowsingContext)destinationBrowsingContext).delayLoadEvents();
		}
		
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
		resource = obtainResource();
		if (resource == null) {
			return;
		}
		
		// 16) If gone async is false, return and continue asynchronously
		if (goneAsync == false) {
			Thread asyncPerform = new Thread() {
				@Override
				public void run() {
					// 17) Let gone async be true.
					goneAsync = true;
					
					performFromHandleRedirects();
				}
			};
			asyncPerform.start();
			return;
		} else {		
			performFromHandleRedirects();
		}
	}
	
	protected void performFromHandleRedirects() {
		// 18) Handle redirects and abort on different origins
		if (resource.shouldRedirect()) {
			if (resource.isRedirectValid()) {
				performFromFragmentIdentifiers();
			} else {
				// TODO: Maybe throw an security error.
				return;
			}
		}
		
		// 19) Wait for incoming byte(s) or abort if resource is empty
		// FIXME: Wait for 10 seconds - reach it from constant or from User agent settings
		if (!resource.waitForBytes(10000)) {
			// TODO: Throw timeout or similar exception
			return;
		}
		
		// 20) TODO: Fallback in prefer-online mode
		
		// 21) TODO: Fallback for fallback entries
				
		performFromResourceHandling();
	}
	
	protected void performFromResourceHandling() {
		// 22) Handle resources that are not valid (e.g. do not contain metadata and the content) and attachments
		if (!resource.isContentValid()) {
			ContentHandler errorHandler = resource.getErrorHandler();
			
			if (errorHandler != null) {
				errorHandler.process(resource);
			}
		}
		
		if (resource.isAttachment()) {
			downloadResource();
		}
		
		// 23) Get content type.
		String contentType = resource.getContentType();
		if (contentType == null) {
			// TODO: Maybe throw an exception.
			return;
		}
		
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
		Fetch fetch = fetchRegistry.getFetch(destinationBrowsingContext, url);
		
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
		boolean identicalUrls = UrlUtils.identicalComponents(url, currentURL, UrlComponent.PROTOCOL, UrlComponent.HOST, UrlComponent.PORT, UrlComponent.PATH, UrlComponent.QUERY);
				
		String fragment = url.getRef();
		if (identicalUrls && fragment != null) {
			return true;
		}
		
		return false;
	}
	
	protected boolean affectsBrowsingContext() {	
		return resourceHandlerRegistry.existsErrorHandler(url);
	}
	
	protected void navigateToFragment(String fragment) {
		
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

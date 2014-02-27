package org.fit.cssbox.scriptbox.navigation;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;

public class NavigationAttempt {
	protected boolean matured;
	protected NavigationController navigationController;
	protected BrowsingContext sourceBrowsingContext;
	protected boolean exceptionEnabled;
	protected boolean explicitSelfNavigationOverride;
	protected URL url;
	
	private boolean goneAsync;
	
	public NavigationAttempt(NavigationController navigationController, BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride) {
		this.navigationController = navigationController;
		this.sourceBrowsingContext = sourceBrowsingContext;
		this.exceptionEnabled = exceptionEnabled;
		this.explicitSelfNavigationOverride = explicitSelfNavigationOverride;
		this.url = url;
	}
	
	public void mature() {
		matured = true;
	}
	
	public boolean isMatured() {
		return matured;
	}
	
	public NavigationController getNavigationController() {
		return navigationController;
	}
	
	public URL getURL() {
		return url;
	}
	
	public void navigate() {
	BrowsingContext destinationBrowsingContext = context;
		
		// TODO: 1) Release the storage mutex.
		
		// 2) If the source browsing context is not allowed to navigate the browsing context being navigated, then abort these steps.
		// TODO: If these steps are aborted here, the user agent may instead offer to open the new resource 
		// in a new top-level browsing context or in the top-level browsing context of the source browsing context
		if (!sourceBrowsingContext.isAllowedToNavigate(destinationBrowsingContext)) {
			return;
		}
		
		// 3) ...
		if (sourceBrowsingContext == destinationBrowsingContext && hasSeamlessFlag(destinationBrowsingContext) && !explicitSelfNavigationOverride) {
			while (destinationBrowsingContext.getCreatorContext() != null) {
				destinationBrowsingContext = destinationBrowsingContext.getCreatorContext();
				
				if (hasSeamlessFlag(destinationBrowsingContext)) {
					break;
				}
			}
		}

		synchronized (destinationBrowsingContext) {
			boolean runningNavigate = destinationBrowsingContext.navigateRunning;
			boolean runningUnloadDocument = destinationBrowsingContext.navigateUnloadDocument;
			URL runningNavigateURL = destinationBrowsingContext.navigateURL;
			
			// 4) ...
			if (sourceBrowsingContext == destinationBrowsingContext && runningNavigate && runningUnloadDocument) {
				UrlOrigin urlOrigin = new UrlOrigin(url);
				UrlOrigin runningNavigateUrlOrigin = new UrlOrigin(runningNavigateURL);
				if (urlOrigin.equals(runningNavigateUrlOrigin)) {
					return;
				}
			}
			
			context.navigateRunning = true;
			context.navigateUnloadDocument = false;
			context.navigateURL = url;
		}
		
		//TODO: 5) and 6)
		
		// 7) Let gone async be false.
		goneAsync = false;
		
		// 8) ...
		Html5DocumentImpl currentDocument = destinationBrowsingContext.getActiveDocument();
		URL currentURL = currentDocument.getAddress();
		boolean identicalUrls = currentURL.getProtocol().equals(url.getProtocol());
		identicalUrls = identicalUrls && currentURL.getHost().equals(url.getHost());
		identicalUrls = identicalUrls && currentURL.getPort() == url.getPort();
		identicalUrls = identicalUrls && currentURL.getPath().equals(url.getPath());
		identicalUrls = identicalUrls && currentURL.getQuery().equals(url.getQuery());
		
		String fragment = url.getRef();
		if (identicalUrls && fragment != null) {
			navigateToFragment(sourceBrowsingContext, fragment);
			return;
		}
		
		// 9) ...
		if (goneAsync == false) {
			cancelAllNavigations();
		}
		
		// 10)
		if (!affectsBrowsingContext()) {
			return;
		}
		
		// 11) ...
		if (goneAsync == false && currentDocument.promptToUnload() == false) {
			return;
		}
		
		// 12)
		if (goneAsync == false) {
			currentDocument.abort();
		}
		
		/*
		 * Remove following lines - simplification only.
		 */
		
		// TODO: 13) and 14)
		/*
		 * TODO: Missing implementation.
		 * FIXME: Missing implementation.
		 * @see
		 */
	}
	
public void cancel() {
		
	}

	private Resource getResourceTestOnly(URL url) {

		URLConnection conn = url.openConnection();
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (compatible; SwingBox/1.x; Linux; U) CSSBox/4.x (like Gecko)");
        conn.setRequestProperty("Accept-Charset", "utf-8");

        if (conn instanceof HttpsURLConnection) {
            System.out.println("$ Connection is HTTPS !!");
        }
        else if (conn instanceof HttpURLConnection)
        {
            HttpURLConnection hconn = (HttpURLConnection) conn;
            hconn.setInstanceFollowRedirects(false);

            int response = hconn.getResponseCode();
            boolean redirect = (response >= 300 && response <= 399);

            if (redirect) {
                String loc = conn.getHeaderField("Location");
                if (loc.startsWith("http", 0)) {
                	url = new URL(loc);
                } else {
                	url = new URL(url, loc);
                }
                return getResourceTestOnly(url);
            }
        }

        return conn.getInputStream();
	}
	
	private boolean affectsBrowsingContext() {
		return true;
	}
	
	private void cancelAllNavigations() {
		
	}
	
	private void navigateToFragment(BrowsingContext sourceBrowsingContext, String fragment) {
		
	}
	
	private static boolean hasSeamlessFlag(BrowsingContext context) {
		if (context instanceof IFrameBrowsingContext) {
			if (((IFrameBrowsingContext)context).seamlessBrowsingFlag) {
				return true;
			}
		}
		return false;
	}
}

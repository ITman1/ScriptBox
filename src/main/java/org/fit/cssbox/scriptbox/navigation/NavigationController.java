package org.fit.cssbox.scriptbox.navigation;

import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.SwingUtilities;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.history.SessionHistory.UpdateSessionHistoryTask;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;

public class NavigationController {
	
	private BrowsingContext context;
	
	public NavigationController(BrowsingContext context) {
		this.context = context;
	}
	

	public void update(SessionHistoryEntry entry) {
		
	}
	
	public void navigate(BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride) {
	
		
	}
	
	public void cancel() {
		
	}
	
	public BrowsingContext getBrowsingContext() {
		return context;
	}

}

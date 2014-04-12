package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;

@FetchPreamble (protocols = {"https"})
public class HttpsFetch extends HttpFetch {
	protected class HttpsResource extends HttpResource {

		public HttpsResource(BrowsingContext context, HttpURLConnection conn, boolean isSafe) {
			super(context, conn, isSafe);
		}
	}
		
	public HttpsFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		super(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
	}
	
	public HttpsFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		super(sourceContext, destinationContext, url);
	}

	@Override
	protected HttpResource createHttpResource(BrowsingContext context, HttpURLConnection httpConn, boolean isSafe) {
		if (httpConn instanceof HttpsURLConnection) {
			return new HttpsResource(context, httpConn, isSafe);
		} else {
			return null;
		}
	}
}

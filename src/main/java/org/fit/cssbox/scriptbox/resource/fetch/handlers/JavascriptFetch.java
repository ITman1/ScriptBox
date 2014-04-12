package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.url.javascript.Handler.JavaScriptURLConnection;

@FetchPreamble (protocols = {"javascript"})
public class JavascriptFetch extends HttpFetch {
	public class JavaScriptResource extends HttpResource {
		
		public JavaScriptResource(BrowsingContext context, HttpURLConnection connection) {
			super(context, connection, false);
		}
						
		@Override
		public boolean isContentValid() {
			try {
				return conn.getResponseCode() == 200;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		public boolean isAttachment() {
			return false;
		}
		
		@Override
		public ContentHandler getErrorHandler() {
			return null;
		}
		
		@Override
		public boolean shouldRedirect() {
			return false;
		}
		
		@Override
		public URL getRedirectUrl() {
			return null;
		}
		
		@Override
		public boolean isRedirectValid() {
			return false;
		}
		
		@Override
		public URL getOverrideAddress() {
			return context.getActiveDocument().getAddress();
		}
		
		@Override
		public String getReferrer() {
			return "";
		}
		
		@Override
		public boolean waitForBytes(int timeout) {
			BufferedInputStream is = getInputStream();
			return is != null;
		}
	}	
		
	public JavascriptFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		super(sourceContext, destinationContext, url);
	}

	@Override
	protected void fetchImpl() throws IOException {
	}

	@Override
	protected Resource getResourceImpl() {
		JavaScriptURLConnection connection = new JavaScriptURLConnection(url, sourceContext, destinationContext);
		try {
			connection.connect();
			return new JavaScriptResource(destinationContext, connection);
		} catch (IOException e) {
			e.printStackTrace();
			throw new UnknownException(e);
		}
		
	}

}

package org.fit.cssbox.scriptbox.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public class Resource {
	protected URLConnection conn;
	protected BrowsingContext context;

	public Resource(URLConnection conn, BrowsingContext context) {
		this.conn = conn;
		this.context = context;
	}
	
	public InputStream getInputStream() {
		try {
			return conn.getInputStream();
		} catch (IOException e) {
			return null;
		}
	}
	
	public BrowsingContext getContext() {
		return context;
	}
	
	public String getContentType() {
		/*
		 *  FIXME: If null, use MIME sniffing instead.
		 *  See: http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#content-type-sniffing-0
		 */
		
        return conn.getHeaderField("Content-Type");
	}
	
	public URLConnection getUrlConnection() {
		return conn;
	}
}

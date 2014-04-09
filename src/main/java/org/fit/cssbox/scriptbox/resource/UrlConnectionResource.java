package org.fit.cssbox.scriptbox.resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public abstract class UrlConnectionResource extends Resource {
	protected URLConnection conn;
	protected BrowsingContext context;
	protected BufferedInputStream is;

	public UrlConnectionResource(BrowsingContext context, URLConnection conn) {
		super(context);
		
		this.conn = conn;
		try {
			InputStream connIs = conn.getInputStream();
			this.is = new BufferedInputStream(connIs);
		} catch (IOException e) {
		}
	}
	
	public BufferedInputStream getInputStream() {
		return is;
	}
	
	public URLConnection getUrlConnection() {
		return conn;
	}
		
	@Override
	public URL getAddress() {
		return conn.getURL();
	}
	
	@Override
	public String getReferrer() {
		return conn.getURL().toExternalForm();
	}
	
	@Override
	public String getContentType() {
		/*
		 *  FIXME: Check for correctness and use MIME sniffing instead.
		 *  See: http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#content-type-sniffing-0
		 */
		String contentTypeField = conn.getContentType();
		String[] contentTypeValues = (contentTypeField != null)? contentTypeField.split(";") : null;
		String contentType = (contentTypeValues != null && contentTypeValues.length > 0)? contentTypeValues[0] : null;
		
		if (contentType == null) {
			InputStream is = getInputStream();
			try {
				contentType = URLConnection.guessContentTypeFromStream(is);
			} catch (IOException e) {
			}
		}
		
		return contentType;
	}
	
	@Override
	public String getContentEncoding() {
		String contentEncoding = conn.getContentEncoding();
		contentEncoding = (contentEncoding == null)? getCharsetFromContentType() : null;
		
		return contentEncoding;
	}
	
	protected String getCharsetFromContentType() {		
		String contentType = conn.getContentType();	
		String[] contentValues = contentType.split(";");

		for (String value : contentValues) {
		    value = value.trim();

		    if (value.toLowerCase().startsWith("charset=")) {
		    	return value.substring("charset=".length());
		    }
		}

		return "UTF-8";
	}
}

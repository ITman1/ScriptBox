/**
 * UrlConnectionResource.java
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

package org.fit.cssbox.scriptbox.resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

/**
 * Abstract class for describing resources that are fetched using {@link URLConnection}
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class UrlConnectionResource extends Resource {
	protected URLConnection conn;
	protected BufferedInputStream is;

	/**
	 * Constructs new resource for a browsing context with with a given URL connection.
	 * 
	 * @param context Browsing context to which belongs this resource.
	 * @param conn Connection to be used for fetching the resource.
	 */
	public UrlConnectionResource(BrowsingContext context, URLConnection conn) {
		super(context);
		
		this.conn = conn;
	}
	
	@Override
	public BufferedInputStream getInputStream() {
		if (is == null) {
			InputStream connIs;
			try {
				connIs = conn.getInputStream();
				is = new BufferedInputStream(connIs);
			} catch (IOException e) {
			}
		}

		return is;
	}
	
	/**
	 * Returns associated URL connection.
	 * 
	 * @return Associated URL connection.
	 */
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
		 *  See: http://www.w3.org/html/wg/drafts/html/master/infrastructure.html#content-type-sniffing-0
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
	
	/**
	 * Resolves charset from the content type header field. 
	 * @return Resolved charset.
	 */
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

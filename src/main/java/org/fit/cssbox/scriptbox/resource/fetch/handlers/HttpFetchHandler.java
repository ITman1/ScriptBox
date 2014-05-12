/**
 * HttpFetchHandler.java
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

package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ParseException;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.UrlConnectionResource;
import org.fit.cssbox.scriptbox.resource.fetch.FetchHandler;
import org.fit.cssbox.scriptbox.resource.fetch.FetchHandlerPreamble;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;

/**
 * Class representing fetch that handles http: protocols
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
@FetchHandlerPreamble (protocols = {"http"})
public class HttpFetchHandler extends FetchHandler {
	protected enum HttpMethod {
		GET,
		POST,
		PUT
	};
	
	protected class HttpResource extends UrlConnectionResource {
		public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
		public static final String HEADER_LOCATION = "Location";
		public static final String DISPOSITION_ATTACHMENT = "attachment";

		protected HttpURLConnection conn;
		protected boolean isSafeMethod;
		
		public HttpResource(BrowsingContext context, HttpURLConnection conn, boolean isSafeMethod) {
			super(context, conn);
			
			this.conn = conn;
			this.isSafeMethod = isSafeMethod;
		}

		@Override
		public boolean isAttachment() {
			boolean result = false;
			
			String dispositionString = conn.getHeaderField(HEADER_CONTENT_DISPOSITION);
			if (dispositionString != null) {
				try {
					ContentDisposition disposition = new ContentDisposition(dispositionString);
					
					result = disposition.getDisposition().equals(DISPOSITION_ATTACHMENT);
				} catch (ParseException e) {
				}
			}

			return result;
		}
		
		@Override
		public boolean shouldRedirect() {
			int response = 0;
			
			try {
				response = conn.getResponseCode();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return (response >= 300 && response <= 399);
		}

		@Override
		public URL getRedirectUrl() {
			URL newUrl = null;
			if (shouldRedirect()) {
				String loc = conn.getHeaderField(HEADER_LOCATION);
				try {
					if (loc.startsWith("http", 0)) {
						newUrl = new URL(loc);
					} else {
						newUrl = new URL(url, loc);
					}
					
					String urlFragment = url.getRef();
					
					if (urlFragment != null) {
						newUrl = URLUtilsHelper.setComponent(newUrl, UrlComponent.REF, urlFragment);
					}
						
				} catch (MalformedURLException e) {
				}
			}
			
			return newUrl;
		}
		
		@Override
		public boolean isRedirectValid() {
			if (isSafeMethod) {
				return true;
			}
			
			if (method == HttpMethod.POST) {
				return true;
			}
			
			URL newUrl = getRedirectUrl();
			
			if (newUrl == null) {
				return false;
			}
			
			UrlOrigin urlOrigin = new UrlOrigin(url);
			UrlOrigin newUrlOrigin = new UrlOrigin(newUrl);
			
			return urlOrigin.equals(newUrlOrigin);
		}
		
	}
	
	protected HttpResource httpResource;
	protected URLConnection conn;
	protected HttpMethod method;

	public HttpFetchHandler(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		super(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
	}
	
	public HttpFetchHandler(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		super(sourceContext, destinationContext, url);
		
		method = HttpMethod.GET;
	}

	@Override
	public void close() throws IOException {
		if (conn != null) {
			conn.getInputStream().close();
			conn = null;
		}
	}
	
	@Override
	protected Resource getResourceImpl() {
		return httpResource;
	}

	@Override
	protected void fetchImpl() throws IOException {
		conn = url.openConnection();
		
		// FIXME: Replace for non magic text, e.g. constant or provided by user agent
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; ScriptBox/1.x; Linux; U) CSSBox/4.x (like Gecko)");
		conn.setRequestProperty("Accept-Charset", "utf-8");

		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setInstanceFollowRedirects(false);
			
			httpResource = createHttpResource(destinationContext, httpConn, isSafe);
		}
	}
	
	protected HttpResource createHttpResource(BrowsingContext context, HttpURLConnection httpConn, boolean isSafe) {
		return new HttpResource(context, httpConn, isSafe);
	}
}

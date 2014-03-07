package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.mail.internet.ContentDisposition;
import javax.mail.internet.ParseException;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.UrlConnectionResource;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.UrlUtils;
import org.fit.cssbox.scriptbox.url.UrlUtils.UrlComponent;

@FetchPreamble (protocols = {"http"})
public class HttpFetch extends Fetch {
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
		public HttpResource(BrowsingContext context, HttpURLConnection conn) {
			super(context, conn);
			
			this.conn = conn;
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
						newUrl = UrlUtils.setComponent(newUrl, UrlComponent.REF, urlFragment);
					}
						
				} catch (MalformedURLException e) {
				}
			}
			
			return newUrl;
		}
		
		@Override
		public boolean isRedirectValid() {
			URL newUrl = getRedirectUrl();
			
			if (newUrl == null) {
				return false;
			}
			
			UrlOrigin urlOrigin = new UrlOrigin(url);
			UrlOrigin newUrlOrigin = new UrlOrigin(newUrl);
			
			return urlOrigin.equals(newUrlOrigin) || method == HttpMethod.POST;
		}
		
	}
	
	protected HttpResource httpResource;
	protected URLConnection conn;
	protected HttpMethod method;

	public HttpFetch(BrowsingContext context, URL url, boolean synchronous) {
		super(context, url, synchronous);
	}
	
	public HttpFetch(BrowsingContext context, URL url) {
		super(context, url);
		
		method = HttpMethod.GET;
	}

	@Override
	public Resource getResource() {
		return httpResource;
	}

	@Override
	public void fetch() throws IOException {
		conn = url.openConnection();
		
		// FIXME: Replace for non magic text, e.g. constant or provided by user agent
		conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; SwingBox/1.x; Linux; U) CSSBox/4.x (like Gecko)");
		conn.setRequestProperty("Accept-Charset", "utf-8");

		if (conn instanceof HttpURLConnection) {
			HttpURLConnection httpConn = (HttpURLConnection) conn;
			httpConn.setInstanceFollowRedirects(false);
			
			httpResource = createHttpResource(context, httpConn);
		}
	}
	
	protected HttpResource createHttpResource(BrowsingContext context, HttpURLConnection httpConn) {
		return new HttpResource(context, httpConn);
	}
}

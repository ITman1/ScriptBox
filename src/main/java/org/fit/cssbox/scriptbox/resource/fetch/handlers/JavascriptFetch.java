/**
 * JavascriptFetch.java
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

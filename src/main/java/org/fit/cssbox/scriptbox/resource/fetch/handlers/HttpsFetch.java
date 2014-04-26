/**
 * HttpsFetch.java
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

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;

/**
 * Class representing fetch that handles https: protocols
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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

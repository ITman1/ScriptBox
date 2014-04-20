/**
 * FileFetch.java
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
import java.net.URL;
import java.net.URLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.UrlConnectionResource;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;

@FetchPreamble (protocols = {"file"})
public class FileFetch extends Fetch {
	
	private class FileResource extends UrlConnectionResource {
		
		public FileResource(BrowsingContext context, URLConnection conn) {
			super(context, conn);
		}
		
		@Override
		public String getContentType() {
			String contentType = URLConnection.guessContentTypeFromName(url.toExternalForm());
			
			if (contentType == null) {
				contentType = super.getContentType();
			}
			
			return contentType;
		}
	}
	
	protected URLConnection conn;
	protected Resource fileResource;
	
	public FileFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		super(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
	}
	
	public FileFetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		super(sourceContext, destinationContext, url);
	}
	
	@Override
	public void close() throws IOException {
		if (conn != null) {
			conn.getInputStream().close();
			conn = null;
		}
	}
	
	@Override
	protected void fetchImpl() throws IOException {
		conn = url.openConnection();
		fileResource = new FileResource(destinationContext, conn);
	}

	@Override
	protected Resource getResourceImpl() {
		return fileResource;
	}
}

/**
 * Resource.java
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

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;

public abstract class Resource {
	private class WaitForDataThread extends Thread {
		private BufferedInputStream is;
		private boolean dataAvailable;
		
		public WaitForDataThread(BufferedInputStream is) {
			this.is = is;
		}
		
		@Override
		public void run() {
			try {
				is.mark(0);
				int count = is.read();
				is.reset();
				dataAvailable = count != -1;
			} catch (IOException e) {
			}
		}
	};
	
	protected BrowsingContext context;

	public Resource(BrowsingContext context) {
		this.context = context;
	}
	
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	public abstract BufferedInputStream getInputStream();
	public abstract String getContentType();
	public abstract String getContentEncoding();
	
	public boolean isContentValid() {
		String contentType = getContentType();
		InputStream is = getInputStream();
		
		return is != null && contentType != null && !contentType.isEmpty();
	}
	
	public boolean isAttachment() {
		return false;
	}
	
	public ContentHandler getErrorHandler() {
		return null;
	}
	
	public boolean shouldRedirect() {
		return false;
	}
	
	public URL getRedirectUrl() {
		return null;
	}
	
	public boolean isRedirectValid() {
		return false;
	}
	
	public URL getAddress() {
		return null;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#override-url
	 */
	public URL getOverrideAddress() {
		return null;
	}
	
	public String getReferrer() {
		return "";
	}
	
	public boolean waitForBytes(int timeout) {
		BufferedInputStream is = getInputStream();
		
		if (is == null) {
			return false;
		}
		
		WaitForDataThread waitForDataThread = new WaitForDataThread(is);
		
		waitForDataThread.start();
		try {
			waitForDataThread.join(timeout);
		} catch (InterruptedException e) {
			return false;
		}
		
		return waitForDataThread.dataAvailable;

	}
}

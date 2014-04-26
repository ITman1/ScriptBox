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

/**
 * Abstract class for describing all resources that are fetched.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class Resource {
	/*
	 * Thread that waits until there some data to download.
	 */
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
		
		@Override
		public String toString() {
			URL address = getAddress();
			String sourceUrl = (address != null)? address.toExternalForm() : "(no url)";
			return "WaitForData Thread - " + sourceUrl;
		}
	};
	
	protected BrowsingContext context;
	protected boolean aborted;

	/**
	 * Constructs new resource for a browsing context.
	 * 
	 * @param context Browsing context to which belongs this resource.
	 */
	public Resource(BrowsingContext context) {
		this.context = context;
	}
	
	/**
	 * Returns associated browsing context.
	 * 
	 * @return Associated browsing context.
	 */
	public BrowsingContext getBrowsingContext() {
		return context;
	}
	
	/**
	 * Returns input stream with the content.
	 * 
	 * @return Input stream containing the content.
	 */
	public abstract BufferedInputStream getInputStream();
	
	/**
	 * Returns MIME content type.
	 * 
	 * @return MIME content type.
	 */
	public abstract String getContentType();
	
	/**
	 * Returns content encoding.
	 * 
	 * @return Content encoding.
	 */
	public abstract String getContentEncoding();
	
	/**
	 * Returns whether is content valid - e.g. has valid input stream and content type.
	 * 
	 * @return True if is content valid, otherwise false.
	 */
	public boolean isContentValid() {
		String contentType = getContentType();
		InputStream is = getInputStream();
		
		return !aborted && is != null && contentType != null && !contentType.isEmpty();
	}
	
	/**
	 * Aborts this resource, so it is no longer valid.
	 */
	public void abort() {
		aborted = true;
	}
	
	/**
	 * Tests whether should be this content downloaded as a attachment.
	 * @return
	 */
	public boolean isAttachment() {
		return false;
	}
	
	/**
	 * Returns content handler that handles errors if there are any.
	 * 
	 * @return Content handler that handles errors.
	 */
	public ContentHandler getErrorHandler() {
		return null;
	}
	
	/**
	 * Tests whether should be this resource redirected to another one.
	 * 
	 * @return True if should be this resource redirected to another one.
	 */
	public boolean shouldRedirect() {
		return false;
	}
	
	/**
	 * Returns URL where should be this resource redirected.
	 * 
	 * @return URL where should be this resource redirected.
	 */
	public URL getRedirectUrl() {
		return null;
	}
	
	/**
	 * Tests whether is redirecting of this resource valid.
	 * 
	 * @return True if is redirecting of this resource valid, otherwise false.
	 */
	public boolean isRedirectValid() {
		return false;
	}
	
	/**
	 * Returns fetching address.
	 * 
	 * @return Address used for the fetch of this resource.
	 */
	public URL getAddress() {
		return null;
	}
	
	/**
	 * Address that should be used for setting some common aspects instead of fetch address.
	 * 
	 * @return Address which overrides fetch address.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#override-url">Override address</a>
	 */
	public URL getOverrideAddress() {
		return null;
	}
	
	/**
	 * Referrer.
	 * 
	 * @return Referrer.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#referrer-source">A referrer source</a>
	 */
	public String getReferrer() {
		return "";
	}
	
	/**
	 * Waits for a bytes.
	 * 
	 * @param timeout Timeout how long should be waited.
	 * @return True if bytes have been received earlier than timeout reached.
	 */
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

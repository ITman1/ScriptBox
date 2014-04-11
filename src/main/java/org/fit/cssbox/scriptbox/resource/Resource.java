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

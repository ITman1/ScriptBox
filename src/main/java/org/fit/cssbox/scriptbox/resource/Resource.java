package org.fit.cssbox.scriptbox.resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

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
				is.read();
				is.reset();
				dataAvailable = true;
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
	
	public boolean shouldRedirect() {
		return false;
	}
	
	public URL getRedirectUrl() {
		return null;
	}
	
	public boolean isRedirectValid() {
		return false;
	}
	
	public boolean waitForBytes(int timeout) {
		BufferedInputStream is = getInputStream();
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

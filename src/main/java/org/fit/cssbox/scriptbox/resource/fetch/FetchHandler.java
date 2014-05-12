/**
 * FetchHandler.java
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

package org.fit.cssbox.scriptbox.resource.fetch;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.resource.Resource;

/**
 * Abstract class from should derive each specific fetch.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class FetchHandler implements Closeable {
	private class AsyncFetchThread extends Thread {
		@Override
		public void run() {
			try {
				fetchImpl();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				onFetchCompletedPrivate();
			}
		}
		
		@Override
		public String toString() {
			String sourceUrl = (url != null)? url.toExternalForm() : "(no url)";
			return "Fetch Thread - " + sourceUrl;
		}
	};
	
	protected FetchRegistry fetchRegistry;
	protected BrowsingContext destinationContext;
	protected BrowsingContext sourceContext;
	protected URL url;
	protected boolean synchronous;
	protected boolean manualRedirect;
	protected boolean isSafe;
	protected Task onFinishTask;
	protected boolean closed;
	protected boolean finished;
	protected Resource resourceImpl;
	
	/**
	 * Constructs fetch for a given parameters.
	 * 
	 * @param sourceContext Source browsing context which mediates the fetch.
	 * @param destinationContext Destination browsing context where is new resource being fetched.
	 * @param url URL which is being fetched.
	 * @param synchronous If true, then fetching is blocking operation.
	 * @param manualRedirect If is set, then no redirecting will be processed.
	 * @param isSafe Should be set to true, if was invoked by user agent and is secure.
	 * @param onFinishTask Task that should be called after fetch is complete.
	 */
	public FetchHandler(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		this.fetchRegistry = FetchRegistry.getInstance();
		
		this.sourceContext = sourceContext;
		this.destinationContext = destinationContext;
		this.url = url;
		this.synchronous = synchronous;
		this.manualRedirect = manualRedirect;
		this.isSafe = isSafe;
		this.onFinishTask = onFinishTask;
	}
	
	/**
	 * Constructs fetch for a given parameters.
	 * 
	 * @param sourceContext Source browsing context which mediates the fetch.
	 * @param destinationContext Destination browsing context where is new resource being fetched.
	 * @param url URL which is being fetched.
	 */
	public FetchHandler(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		this(sourceContext, destinationContext, url, true, true, false, null);
	}
	
	/**
	 * Tests whether is this fetch valid.
	 * 
	 * @return True if is this fetch valid, otherwise false.
	 */
	public boolean isValid() {
		return true;
	}
	
	/**
	 * Tests whether is this fetch retrieved synchronously.
	 * 
	 * @return True if is this fetch retrieved synchronously, otherwise false.
	 */
	public boolean isSynchronous() {
		return synchronous;
	}
	
	/**
	 * Tests whether was this fetch constructed by secure/safe operation.
	 * 
	 * @return True if is this fetch retrieved synchronously, otherwise false.
	 */
	public boolean isSafe() {
		return isSafe;
	}
	
	/**
	 * Closes this fetch.
	 */
	@Override
	public synchronized void close() throws IOException {
		if (!closed && resourceImpl != null) {
			resourceImpl.abort();
		}
		
		closed = true;
	}
	
	/**
	 * Performs fetch.
	 * 
	 * @throws IOException Exception might be thrown by reading input stream.
	 */
	public synchronized void fetch() throws IOException {
		if (finished) {
			return;
		}
		
		if (synchronous == false) {
			AsyncFetchThread asyncThread = new AsyncFetchThread();
			asyncThread.start();
			return;
		} else {		
			fetchImpl();
			onFetchCompletedPrivate();
		}

	}
	
	/**
	 * Returns constructed resource from this fetch.
	 * 
	 * @return Constructed resource from this fetch.
	 */
	public synchronized Resource getResource() {
		if (finished && !closed) {
			if (resourceImpl == null) {
				resourceImpl = getResourceImpl();
			}

			return resourceImpl;
		} else {
			return null;
		}
	}
	
	/**
	 * Called when fetch was successfully completed.
	 */
	protected void onFetchCompleted() {
	}
	
	/**
	 * Called for performing the fetch.
	 * 
	 * @throws IOException Thrown when IO exception occur.
	 */
	protected abstract void fetchImpl() throws IOException;
	
	/**
	 * Called for retrieving resource after fetch completes.
	 * 
	 * @return New resource for this fetch.
	 */
	protected abstract Resource getResourceImpl();
	
	private synchronized void onFetchCompletedPrivate() {
		finished = true;
		
		if (!manualRedirect) {
			Resource resource = getResourceImpl();
			boolean shouldRedirect = resource.shouldRedirect();

			if (shouldRedirect) {
				boolean isRedirectValid = resource.isRedirectValid();
				if (isRedirectValid) {
					url = resource.getRedirectUrl();
					FetchHandler newFetch = fetchRegistry.getFetch(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
					if (newFetch != null) {
						try {
							newFetch.fetch();
							return;
						} catch (IOException e) {
						}
					}
				}
			}
		}

		if (onFinishTask != null) {
			destinationContext.getEventLoop().queueTask(onFinishTask);
		}
		
		onFetchCompleted();
	}
}

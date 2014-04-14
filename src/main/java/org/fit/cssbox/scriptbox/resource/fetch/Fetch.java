/**
 * Fetch.java
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

public abstract class Fetch implements Closeable {
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
	
	public Fetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous, boolean manualRedirect, boolean isSafe, Task onFinishTask) {
		this.fetchRegistry = FetchRegistry.getInstance();
		
		this.sourceContext = sourceContext;
		this.destinationContext = destinationContext;
		this.url = url;
		this.synchronous = synchronous;
		this.manualRedirect = manualRedirect;
		this.isSafe = isSafe;
		this.onFinishTask = onFinishTask;
	}
	
	public Fetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		this(sourceContext, destinationContext, url, true, true, false, null);
	}
	
	public boolean isValid() {
		return true;
	}
	
	public boolean isSynchronous() {
		return synchronous;
	}
	
	public boolean isSafe() {
		return isSafe;
	}
	
	@Override
	public synchronized void close() throws IOException {
		if (!closed && resourceImpl != null) {
			resourceImpl.abort();
		}
		
		closed = true;
	}
	
	public synchronized void fetch() throws IOException {
		if (finished) {
			return;
		}
		
		if (synchronous == false) {
			Thread asyncThread = new Thread() {
				@Override
				public void run() {
					try {
						fetchImpl();
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						onFetchCompleted();
					}

				}
			};
			asyncThread.start();
			return;
		} else {		
			fetchImpl();
			onFetchCompleted();
		}

	}
	
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
	
	protected synchronized void onFetchCompleted() {
		finished = true;
		
		if (!manualRedirect) {
			Resource resource = getResourceImpl();
			boolean shouldRedirect = resource.shouldRedirect();

			if (shouldRedirect) {
				boolean isRedirectValid = resource.isRedirectValid();
				if (isRedirectValid) {
					url = resource.getRedirectUrl();
					Fetch newFetch = fetchRegistry.getFetch(sourceContext, destinationContext, url, synchronous, manualRedirect, isSafe, onFinishTask);
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
	}
	
	protected abstract void fetchImpl() throws IOException;
	protected abstract Resource getResourceImpl();
}

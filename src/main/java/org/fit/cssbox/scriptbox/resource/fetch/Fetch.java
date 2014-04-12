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
	protected boolean finished;
	
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
	public void close() throws IOException {}
	
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
		return (finished)? getResourceImpl() : null;
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

package org.fit.cssbox.scriptbox.resource.fetch;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;

public abstract class Fetch implements Closeable {
	protected BrowsingContext destinationContext;
	protected BrowsingContext sourceContext;
	protected URL url;
	protected boolean synchronous;
	
	public Fetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url, boolean synchronous) {
		this.sourceContext = sourceContext;
		this.destinationContext = destinationContext;
		this.url = url;
	}
	
	public Fetch(BrowsingContext sourceContext, BrowsingContext destinationContext, URL url) {
		this(sourceContext, destinationContext, url, true);
	}
	
	public boolean isValid() {
		return true;
	}
	
	public boolean isSynchronous() {
		return synchronous;
	}
	
	@Override
	public void close() throws IOException {}
	
	public void fetch() throws IOException {
		fetch(false);
	}
	
	public abstract void fetch(boolean isSafe) throws IOException;
	public abstract Resource getResource();
}

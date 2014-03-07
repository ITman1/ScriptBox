package org.fit.cssbox.scriptbox.resource.fetch;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;

public abstract class Fetch implements Closeable {
	protected BrowsingContext context;
	protected URL url;
	protected boolean synchronous;
	
	public Fetch(BrowsingContext context, URL url, boolean synchronous) {
		this.context = context;
		this.url = url;
	}
	
	public Fetch(BrowsingContext context, URL url) {
		this(context, url, true);
	}
	
	public boolean isValid() {
		return true;
	}
	
	public boolean isSynchronous() {
		return synchronous;
	}
	
	@Override
	public void close() throws IOException {}
	
	public abstract void fetch() throws IOException;
	public abstract Resource getResource();
}

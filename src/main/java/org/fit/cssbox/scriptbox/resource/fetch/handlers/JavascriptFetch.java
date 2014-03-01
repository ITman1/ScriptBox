package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.io.IOException;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;

@FetchPreamble (protocols = {"javascript"})
public class JavascriptFetch extends Fetch {

	public JavascriptFetch(BrowsingContext context, URL url) {
		super(context, url);
	}

	@Override
	public void fetch() throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public Resource getResource() {
		// TODO Auto-generated method stub
		return null;
	}

}

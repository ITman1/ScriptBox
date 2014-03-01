package org.fit.cssbox.scriptbox.resource.fetch.handlers;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.UrlConnectionResource;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchPreamble;

@FetchPreamble (protocols = {"file"})
public class FileFetch extends Fetch {
	
	private class FileResource extends UrlConnectionResource {

		public FileResource(BrowsingContext context, URLConnection conn) {
			super(context, conn);
		}
		
		@Override
		public String getContentType() {
			String contentType = URLConnection.guessContentTypeFromName(url.toExternalForm());
			
			if (contentType == null) {
				contentType = super.getContentType();
			}
			
			return contentType;
		}
	}
	
	Resource fileResource;
	
	public FileFetch(BrowsingContext context, URL url) {
		super(context, url);
	}

	@Override
	public void fetch() throws IOException {
		URLConnection conn = url.openConnection();
		fileResource = new FileResource(context, conn);
	}

	@Override
	public Resource getResource() {
		return fileResource;
	}
}

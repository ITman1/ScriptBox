package org.fit.cssbox.scriptbox.resource.content;

import java.net.URL;

import org.fit.cssbox.scriptbox.resource.Resource;


public class ContentHandlerRegistry {
	static private ContentHandlerRegistry instance;
	
	private ContentHandlerRegistry() {}
	
	public static synchronized ContentHandlerRegistry getInstance() {
		if (instance == null) {
			instance = new ContentHandlerRegistry();
		}
		
		return instance;
	}
	
	public ContentHandler getHandlerForResource(Resource resource) {
		return null;
	}
	
	public boolean existsErrorHandler(URL url) {
		return true;
	}
	
	public ErrorHandler getErrorHandler(URL url) {
		return null;
	}

}

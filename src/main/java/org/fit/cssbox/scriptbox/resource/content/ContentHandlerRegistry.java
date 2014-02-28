package org.fit.cssbox.scriptbox.resource.content;

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
	
	public boolean existsHandlerForScheme(String scheme) {
		return true;
	}
	
	public ContentHandler getHandlerForResource(Resource resource) {
		return null;
	}
	
	public ContentHandler getHandlerForScheme(String scheme) {
		return null;
	}

}

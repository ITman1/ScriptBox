package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.RenderedContentHandler;

public class PlainTextFileHandlerFactory extends ContentHandlerFactory {
	private class PlainTextFileHandler extends RenderedContentHandler {

		public PlainTextFileHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}
		
		@Override
		public void process(Resource resource) {
		}

	}
    private static List<String> mimeTypes;
	
    static {       
        mimeTypes = new ArrayList<String>(1);
        mimeTypes.add("text/plain");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
    
	@Override
	public ContentHandler getContentHandler(NavigationAttempt navigationAttempt) {
		return new PlainTextFileHandler(navigationAttempt);
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}

}
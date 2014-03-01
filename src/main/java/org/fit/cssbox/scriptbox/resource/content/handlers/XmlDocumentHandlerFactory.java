package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.RenderedContentHandler;

public class XmlDocumentHandlerFactory extends ContentHandlerFactory {
	private class XmlDocumentHandler extends RenderedContentHandler {

		public XmlDocumentHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}
		
		@Override
		public void process(Resource resource) {
		}

	}
    private static List<String> mimeTypes;
	
    static {       
        mimeTypes = new ArrayList<String>(4);
        mimeTypes.add("application/xml");
        mimeTypes.add("text/xml");
        mimeTypes.add("image/svg+xml");
        mimeTypes.add("application/xhtml+xml");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
    
	@Override
	public ContentHandler getContentHandler(NavigationAttempt navigationAttempt) {
		return new XmlDocumentHandler(navigationAttempt);
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}
	
	@Override
	public boolean isImplicitlySupported(String mimeType) {
		mimeType = mimeType.trim();
		
		return mimeType.endsWith("+xml");
	}

}

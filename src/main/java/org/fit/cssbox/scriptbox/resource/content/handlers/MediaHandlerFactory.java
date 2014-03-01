package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;

public class MediaHandlerFactory extends ContentHandlerFactory {
	@Override
	public ContentHandler getContentHandler(NavigationAttempt navigationAttempt) {
		return null;
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return new ArrayList<String>();
	}
}

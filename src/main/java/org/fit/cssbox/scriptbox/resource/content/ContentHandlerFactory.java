package org.fit.cssbox.scriptbox.resource.content;

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;

public abstract class ContentHandlerFactory extends MimeContentFactoryBase<ContentHandler> {
	public abstract ContentHandler getContentHandler(NavigationAttempt navigationAttempt);
	
	@Override
	public ContentHandler getContent(Object... args) {
		if (args.length == 1 && args[0] instanceof NavigationAttempt) {
			return getContentHandler((NavigationAttempt)args[0]);
		}
		
		return null;
	}
}

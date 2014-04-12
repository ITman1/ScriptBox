/**
 * ContentHandlerRegistry.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.resource.content;

import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.misc.MimeContentRegistryBase;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.content.handlers.HtmlDocumentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.handlers.MediaHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.handlers.MultipartHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.handlers.PlainTextFileHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.handlers.PluginHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.handlers.XmlDocumentHandlerFactory;


public class ContentHandlerRegistry extends MimeContentRegistryBase<ContentHandlerFactory, ContentHandler> {
	private class DefaultErrorHandler extends ErrorHandler {

		public DefaultErrorHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}
		
	};

	private static ContentHandlerRegistry instance;

	// TODO: Custom error handlers
	@SuppressWarnings("unused")
	private Map<String, Set<Class<? extends ErrorHandler>>> registeredErrorHandlers;

	private ContentHandlerRegistry() {
		registerMimeContentFactory(HtmlDocumentHandlerFactory.class);
		registerMimeContentFactory(XmlDocumentHandlerFactory.class);
		registerMimeContentFactory(PlainTextFileHandlerFactory.class);
		registerMimeContentFactory(MediaHandlerFactory.class);
		registerMimeContentFactory(MultipartHandlerFactory.class);
		registerMimeContentFactory(PluginHandlerFactory.class);
	}
	
	public static synchronized ContentHandlerRegistry getInstance() {
		if (instance == null) {
			instance = new ContentHandlerRegistry();
		}
		
		return instance;
	}

	public ContentHandler getHandlerForNavigationAttempt(NavigationAttempt navigationAttempt) {
		String mimeType = navigationAttempt.getContentType();
		ContentHandlerFactory factory = (mimeType != null)? getFirstMimeContentFactory(mimeType) : null;
		return (factory == null)? null : factory.getContentHandler(navigationAttempt);
	}
	
	/*
	 * TODO: Implement.
	 */
	public boolean existsErrorHandler(URL url) {
		return true;
	}
	
	/*
	 * TODO: Implement.
	 */
	public ErrorHandler getErrorHandler(NavigationAttempt attempt) {
		return new DefaultErrorHandler(attempt);
	}

}

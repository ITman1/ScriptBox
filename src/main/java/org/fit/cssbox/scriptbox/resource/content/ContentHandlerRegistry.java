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

/**
 * Content handler registry which collects all content handler factories that extend {@link ContentHandlerFactory}.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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
	
	/**
	 * Returns instance of this registry.
	 * 
	 * @return Instanceof of this registry.
	 */
	public static synchronized ContentHandlerRegistry getInstance() {
		if (instance == null) {
			instance = new ContentHandlerRegistry();
		}
		
		return instance;
	}

	/**
	 * Returns content handler for a given navigation attempt.
	 * 
	 * @param navigationAttempt Navigation attempt with which will be associated the content handler.
	 * @return Content handler if there existed corresponding factory and if construction was successful, otherwise null.
	 */
	public ContentHandler getHandlerForNavigationAttempt(NavigationAttempt navigationAttempt) {
		String mimeType = navigationAttempt.getContentType();
		ContentHandlerFactory factory = (mimeType != null)? getFirstMimeContentFactory(mimeType) : null;
		return (factory == null)? null : factory.getContentHandler(navigationAttempt);
	}
	
	/*
	 * TODO: Implement.
	 */
	/**
	 * Tests whether there exists error handler for a given url.
	 * 
	 * @param url URL which is unable to fetch and we want to report an error.
	 * @return True if there exists any error handler for passed url, otherwise false.
	 */
	public boolean existsErrorHandler(URL url) {
		return true;
	}
	
	/*
	 * TODO: Implement.
	 */
	/**
	 * Returns error handler for a given navigation attempt.
	 * 
	 * @param navigationAttempt Navigation attempt with which will be associated the error handler.
	 * @return Error handler if there existed any appropriate error handler, otherwise null.
	 */
	public ErrorHandler getErrorHandler(NavigationAttempt attempt) {
		return new DefaultErrorHandler(attempt);
	}

}

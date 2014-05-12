/**
 * MediaHandlerFactory.java
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

package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.List;

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;

/*
 * TODO: Implement.
 */
/**
 * Content handler factory for handlers following the processing model for media.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#read-media">Read media</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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

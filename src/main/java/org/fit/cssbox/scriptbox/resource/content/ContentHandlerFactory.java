/**
 * ContentHandlerFactory.java
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

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;

/**
 * Abstract class for all content handler factories - factories that constructs
 * content handlers for a supported MIME content type of this factory.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
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

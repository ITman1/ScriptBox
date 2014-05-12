/**
 * ContentHandler.java
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

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.resource.Resource;

/**
 * Abstract class for all content handlers that handles resources 
 * of the specific MIME types and performs corresponding handling actions
 * e.g. parsing of the documents, rendering etc.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ContentHandler {
	protected NavigationAttempt navigationAttempt;
	protected NavigationController navigationController;
	protected BrowsingContext context;
	
	/**
	 * Constructs content handler for a given browsing context.
	 * 
	 * @param context Browsing context that constructed this content handler.
	 */
	public ContentHandler(BrowsingContext context) {
		this.context = context;
	}
	
	/**
	 * Constructs content handler for a given navigation attempt.
	 * 
	 * @param navigationAttempt Navigation attempt which invoked this content handler.
	 */
	public ContentHandler(NavigationAttempt navigationAttempt) {
		this.navigationAttempt = navigationAttempt;
		this.navigationController = navigationAttempt.getNavigationController();
		this.context = navigationController.getBrowsingContext();
	}
		
	/**
	 * Processes given resource.
	 * 
	 * @param resource Resource to be processed.
	 */
	public abstract void process(Resource resource);
}

/**
 * ErrorHandler.java
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

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;

/**
 * Abstract class for all error handlers that reports error for a given navigation attempt.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ErrorHandler {
	protected NavigationAttempt navigationAttempt;
	
	/**
	 * Constructs error handler for a given navigation attempt.
	 * 
	 * @param navigationAttempt Navigation attempt which invoked this content handler.
	 */
	public ErrorHandler(NavigationAttempt navigationAttempt) {
		this.navigationAttempt = navigationAttempt;
	}
	
	/**
	 * Handles an error.
	 * 
	 * @param url Location of the resource which was unable to fetch.
	 */
	public void handle(URL url) {
		
	}
}

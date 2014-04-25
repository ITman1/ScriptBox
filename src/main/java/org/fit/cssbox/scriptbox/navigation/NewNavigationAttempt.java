/**
 * NewNavigationAttempt.java
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

package org.fit.cssbox.scriptbox.navigation;

import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

/**
 * Class representing navigation attempt for navigating new resource.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class NewNavigationAttempt extends NavigationAttempt {

	/**
	 * Creates new navigation attempt.
	 * 
	 * @param navigationController Navigation controller which owns this attempt.
	 * @param sourceBrowsingContext Browsing context that initiated this navigation.
	 * @param url URL to be navigated.
	 * @param exceptionEnabled If exceptions should be thrown for this navigation.
	 * @param explicitSelfNavigationOverride If should not be resolved effective destination 
	 *        context and use browsing context of this controller.
	 * @param replacementEnabled If current session entry should be replaced.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#navigate">Navigate algorithm</a>
	 */
	public NewNavigationAttempt(NavigationController navigationController, BrowsingContext sourceBrowsingContext, URL url, boolean exceptionEnabled, boolean explicitSelfNavigationOverride, boolean replacementEnabled) {
		super(navigationController, sourceBrowsingContext, url, exceptionEnabled, explicitSelfNavigationOverride, replacementEnabled);
	}

}

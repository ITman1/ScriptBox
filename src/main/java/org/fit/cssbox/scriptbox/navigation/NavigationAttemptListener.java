/**
 * NavigationAttemptListener.java
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

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

/**
 * Represents interface for creating callback listener above navigation attempt.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface NavigationAttemptListener {
	/**
	 * Called when navigation attempt cancels.
	 * 
	 * @param attempt Attempt that cancelled.
	 */
	public void onCancelled(NavigationAttempt attempt);
	
	/**
	 * Called when navigation attempt matures.
	 * 
	 * @param attempt Attempt that matured.
	 */
	public void onMatured(NavigationAttempt attempt);
	
	/**
	 * Called when navigation attempt completes.
	 * 
	 * @param attempt Attempt that completed.
	 */
	public void onCompleted(NavigationAttempt attempt);
	
	/**
	 * Called when navigation attempt selected new effective destination browsing context.
	 * 
	 * @param attempt Attempt to which is related this callback.
	 * @param context New selected effective browsing context.
	 */
	public void onEffectiveDestinationContextSelected(NavigationAttempt attempt, BrowsingContext context);
}

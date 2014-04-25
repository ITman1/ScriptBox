/**
 * UpdateNavigationAttempt.java
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

import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;

/**
 * Class representing navigation attempt for reloading of the already visited resource.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class UpdateNavigationAttempt extends NavigationAttempt {
	protected SessionHistoryEntry entry;
	
	/**
	 * Constructs new update navigation attempt for updating of the session history entry.
	 * 
	 * @param navigationController Navigation controller which owns this attempt.
	 * @param entry Session history entry to be updated.
	 */
	public UpdateNavigationAttempt(NavigationController navigationController, SessionHistoryEntry entry) {
		super(navigationController, navigationController.getBrowsingContext(), entry.getURL(), false, false, false);

		this.entry = entry;
	}
	
	/**
	 * Returns related session history entry which should be updated.
	 * 
	 * @return Related session history entry which should be updated.
	 */
	public SessionHistoryEntry getSessionHistoryEntry() {
		return entry;
	}

}

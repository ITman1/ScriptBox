/**
 * UserAgent.java
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

package org.fit.cssbox.scriptbox.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.url.URLUtilsHelper;

/**
 * Default class for creating custom browsers. Every browser should redefine this class.
 * 
 * User agent drives construction of the browsing units with nested browsing contexts,
 * provides global settings and access to global resources. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class UserAgent {
	
	static {
		// Registers custom about:, data: and javascript: schemes
		URLUtilsHelper.registerUrlHandlerPackage("org.fit.cssbox.scriptbox.url");
	}
	
	/**
	 * List with all opened browsing units.
	 */
	protected List<BrowsingUnit> browsingUnits;
	
	/**
	 * Constructs new user agent with no browsing unit.
	 */
	public UserAgent() {
		browsingUnits = new ArrayList<BrowsingUnit>();
	}
	
	/*
	 * TODO: This should walk through every browsing context and return set of browsing context which had the same name.
	 *       This is used in choosing the destination browsing context while navigating.
	 */
	/**
	 * Returns set of browsing context with the same name as the passed name.
	 * 
	 * @param name Browsing context name against which to match the result browsing contexts.
	 * @return Set of browsing context with the same name as the passed name
	 */
	public Set<BrowsingContext> getBrowsingContextsByName(String name) {
		return null;
	}
	
	/**
	 * Tests whether is scripting supported by this user agent.
	 * 
	 * @return True if scripting is supported, otherwise false.
	 */
	public boolean scriptsSupported() {
		return true;
	}
	
	/**
	 * Tests whether is scripting enabled for given address.
	 * 
	 * @param page Address for which is test performed.
	 * @return True if scripting is enabled for given address, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#concept-bc-script">Scripting is enabled</a>
	 */
	public boolean scriptsEnabled(URL page) {
		return true;
	}
	
	/**
	 * Tests whether are cookies supported by this user agent.
	 * 
	 * @return True if cookies are supported, otherwise false.
	 */
	public boolean cookiesEnabled() {
		return true;
	}
	
	/**
	 * Opens new browsing unit.
	 * 
	 * @return New opened browsing unit.
	 */
	public BrowsingUnit openBrowsingUnit() {
		BrowsingUnit browsingUnit = createBrowsingUnit();
		
		browsingUnits.add(browsingUnit);
		
		return browsingUnit;
	}
	
	/**
	 * Closes/Destroys passed browsing unit and removes it from the list of opened browsing units.
	 * 
	 * @param browsingUnit Browsing unit to be closed.
	 */
	public void destroyBrowsingUnit(BrowsingUnit browsingUnit) {
		if (browsingUnits.remove(browsingUnit)) {
			browsingUnit.discard();
		}
	}
	
	/**
	 * Closes all browsing units and stops terminates the user agent.
	 */
	public void stop() {
		while (!browsingUnits.isEmpty()) {
			BrowsingUnit browsingUnit = browsingUnits.get(0);
			destroyBrowsingUnit(browsingUnit);
		}
	}
	
	/**
	 * Releases global storage mutex.
	 */
	public void releaseStorageMutex() {
		
	}
	
	/**
	 * Tests whether are prompts enabled for given address.
	 * 
	 * @param page Address for which is test performed.
	 * @return True if prompts are enabled for given address, otherwise false.
	 */
	public boolean promptsEnabled(URL page) {
		return true;
	}
	
	/**
	 * Tests whether are alerts enabled for given address.
	 * 
	 * @param page Address for which is test performed.
	 * @return True if alerts are enabled for given address, otherwise false.
	 */
	public boolean alertsEnabled(URL page) {
		return true;
	}
	
	/**
	 * Creates new browsing unit. It is directly used by {@link #openBrowsingUnit()}.
	 * 
	 * @return New created browsing unit.
	 */
	protected BrowsingUnit createBrowsingUnit() {
		return new BrowsingUnit(this);
	}
}

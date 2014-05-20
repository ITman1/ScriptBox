/**
 * JavaScriptTesterUserAgent.java
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

package org.fit.cssbox.scriptbox.demo.tester;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowserUIPresenter;
import org.fit.cssbox.scriptbox.demo.browser.SimpleUserAgent;

/**
 * Represents main class with the JavaScript tester user agent.
 * This class constructs {@link JavaScriptTesterBrowsingUnit} browsing unit
 * as the main browsing unit and for additional blank browsing units
 * it constructs {@link SimpleBrowserUIPresenter}.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptTesterUserAgent extends SimpleUserAgent {
	protected JavaScriptTesterBrowsingUnit testerBrowsingUnit;
	
	@Override
	protected BrowsingUnit createBrowsingUnit() {
		return testerBrowsingUnit = new JavaScriptTesterBrowsingUnit(this);
	}
	
	/**
	 * Launcher of the application with this user agent.
	 * 
	 * @param args Program arguments.
	 */
	public static void main(String[] args) {
		String lookAndFeelName = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeelName);
		} catch (Exception e) {
		}
		
		JavaScriptTesterUserAgent userAgent = new JavaScriptTesterUserAgent();
		userAgent.openBrowsingUnit();
	}
}

/**
 * SimpleUserAgent.java
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

package org.fit.cssbox.scriptbox.demo.browser;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowserUIPresenter;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserUserAgent;

/**
 * User agent using browsing units with user interface.
 * This user agent constructs {@link SimpleBrowserUIPresenter} for a new browsing
 * units. When there are no browsing units then it exits the current running application.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class SimpleUserAgent extends ScriptBrowserUserAgent {

	@Override
	protected BrowsingUnit createBrowsingUnit() {
		return new SimpleBrowsingUnit(this);
	}
	
	@Override
	protected BrowsingUnit createAuxiliaryBrowsingUnit(BrowsingContext openerBrowsingContext, String name, boolean createdByScript) {
		return new SimpleBrowsingUnit(openerBrowsingContext, name, createdByScript);
	}
	
	@Override
	public void destroyBrowsingUnit(BrowsingUnit browsingUnit) {
		super.destroyBrowsingUnit(browsingUnit);
		
		if (browsingUnits.size() == 0) {
			stop();
			System.exit(0);
		}
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
		
		SimpleUserAgent userAgent = new SimpleUserAgent();
		userAgent.openBrowsingUnit();
	}
}

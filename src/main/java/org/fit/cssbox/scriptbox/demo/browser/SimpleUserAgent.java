package org.fit.cssbox.scriptbox.demo.browser;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowsingUnit;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserUserAgent;

/**
 * User agent using browsing units with user interface.
 * This user agent constructs {@link SimpleBrowsingUnit} for a new browsing
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

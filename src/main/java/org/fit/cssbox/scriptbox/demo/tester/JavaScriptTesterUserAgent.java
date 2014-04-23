package org.fit.cssbox.scriptbox.demo.tester;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleUserAgent;

/**
 * Represents main class with the JavaScript tester user agent.
 * This class constructs {@link JavaScriptTesterBrowsingUnit} browsing unit
 * as the main browsing unit and for additional blank browsing units
 * it constructs {@link SimpleBrowsingUnit}.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptTesterUserAgent extends SimpleUserAgent {
	protected JavaScriptTesterBrowsingUnit testerBrowsingUnit;
	
	@Override
	protected BrowsingUnit createBrowsingUnit() {
		if (testerBrowsingUnit == null) {
			return testerBrowsingUnit = new JavaScriptTesterBrowsingUnit(this);
		} else {
			return new SimpleBrowsingUnit(this);
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
		
		JavaScriptTesterUserAgent userAgent = new JavaScriptTesterUserAgent();
		userAgent.openBrowsingUnit();
	}
}

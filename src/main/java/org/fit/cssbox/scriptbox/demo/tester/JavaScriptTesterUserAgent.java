package org.fit.cssbox.scriptbox.demo.tester;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleUserAgent;

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
	 * Launch the application.
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

package org.fit.cssbox.scriptbox.demo.browser;

import javax.swing.UIManager;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowsingUnit;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserUserAgent;

public class SimpleUserAgent extends ScriptBrowserUserAgent {

	@Override
	protected BrowsingUnit createBrowsingUnit() {
		return new SimpleBrowsingUnit(this);
	}
	
	@Override
	public void destroyBrowsingUnit(BrowsingUnit browsingUnit) {
		super.destroyBrowsingUnit(browsingUnit);
		
		if (_browsingUnits.size() == 0) {
			stop();
			System.exit(0);
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
		
		SimpleUserAgent userAgent = new SimpleUserAgent();
		userAgent.openBrowsingUnit();
	}
}

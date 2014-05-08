package org.fit.cssbox.scriptbox.demo.browser;

public abstract class BrowserUiController {
	/**
	 * Makes UI visible.
	 */
	public abstract void showUI();
	
	/**
	 * Makes UI invisible.
	 */
	public abstract void hideUI();
	
	/**
	 * Updates UI components.
	 */
	public abstract void updateUI();
	
	/**
	 * Destroys UI.
	 */
	public abstract void closeUI();
}

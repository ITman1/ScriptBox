package org.fit.cssbox.scriptbox.demo.browser;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

public interface BrowserUi {
	public JFrame getWindow();
	public JButton getNavigateButton();
	public JTextField getNavigationField();
	public JButton getHistoryBackButton();
	public JButton getHistoryForwardButton();
	public ScriptBrowser getScriptBrowser();
}

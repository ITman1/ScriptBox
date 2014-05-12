package org.fit.cssbox.scriptbox.demo.browser;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

public class SimpleBrowserUi implements BrowserUi {
	private JFrame frame;
	private JTextField navigationField;
	private JButton navigateButton;
	private JButton historyBackButton;
	private JButton historyForwardButton;
	private ScriptBrowser scriptBrowser;

	/**
	 * Create the view components of the browser.
	 */
	public SimpleBrowserUi() {
		initializeComponents();
	}
	
	private void initializeComponents() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(700, 500));
		frame.setBounds(100, 100, 1024, 780);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		JPanel containerPanel = new JPanel();
		containerPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		frame.getContentPane().add(containerPanel, BorderLayout.CENTER);
		containerPanel.setLayout(new BorderLayout(0, 0));

		JPanel navigationPanel = new JPanel();
		containerPanel.add(navigationPanel, BorderLayout.NORTH);
		navigationPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		navigationPanel.setLayout(new BorderLayout(0, 0));

		JLabel navigationLabel = new JLabel("URL:");
		navigationLabel.setPreferredSize(new Dimension(30, 18));
		navigationPanel.add(navigationLabel, BorderLayout.WEST);

		navigationField = new JTextField();
		navigationField.setToolTipText("Please type URL to navigate");
		navigationLabel.setLabelFor(navigationField);
		navigationField
				.setText("http://www.stud.fit.vutbr.cz/~xlosko01/CSSBox/tests/window_properties.html");
		navigationPanel.add(navigationField, BorderLayout.CENTER);
		navigationField.setColumns(10);

		JPanel navigationButtonsPanel = new JPanel();
		navigationPanel.add(navigationButtonsPanel, BorderLayout.EAST);
		navigationButtonsPanel
				.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));

		navigateButton = new JButton("navigate");
		navigationButtonsPanel.add(navigateButton);

		historyBackButton = new JButton("<-");
		navigationButtonsPanel.add(historyBackButton);

		historyForwardButton = new JButton("->");
		navigationButtonsPanel.add(historyForwardButton);
		
		JScrollPane scriptBrowserScrollPane = new JScrollPane();
		containerPanel.add(scriptBrowserScrollPane, BorderLayout.CENTER);

		scriptBrowser = new ScriptBrowser();
		scriptBrowserScrollPane.setViewportView(scriptBrowser);	
	}
	
	public JFrame getWindow() {
		return frame;
	}

	public JButton getNavigateButton() {
		return navigateButton;
	}

	public JTextField getNavigationField() {
		return navigationField;
	}
	
	public JButton getHistoryBackButton() {
		return historyBackButton;
	}

	public JButton getHistoryForwardButton() {
		return historyForwardButton;
	}
	
	public ScriptBrowser getScriptBrowser() {
		return scriptBrowser;
	}
}

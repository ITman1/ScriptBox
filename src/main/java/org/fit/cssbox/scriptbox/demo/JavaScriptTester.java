package org.fit.cssbox.scriptbox.demo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import jsyntaxpane.DefaultSyntaxKit;

import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

public class JavaScriptTester {
	private JFrame frame;
	private JTextField navigationField;
	private JButton navigateButton;
	private JSplitPane contentSplitPane;
	private JEditorPane sourceCodeEditorPane;
	private ScriptBrowser scriptBrowser;
	private JTabbedPane sourceCodeTabbedPane;
	private JButton newSourceCodeButton;
	private JButton openSourceCodeButton;
	private JButton saveSourceCodeButton;
	private JButton closeSourceCodeButton;
	private JButton navigateSourceCodeButton;
	private JButton saveAsSourceCodeButton;
	private ScriptObjectViewer windowObjectViewer;
	private ScriptObjectsWatchList objectsWatchList;
	private JTextField newWatchedVariableField;
	private JScrollPane scriptBrowserScrollPane;
	
	/**
	 * Create the application.
	 */
	public JavaScriptTester() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(700, 400));
		frame.setBounds(100, 100, 1024, 780);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel navigationPanel = new JPanel();
		navigationPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		frame.getContentPane().add(navigationPanel, BorderLayout.NORTH);
		navigationPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel navigationLabel = new JLabel("URL:");
		navigationPanel.add(navigationLabel, BorderLayout.WEST);
		
		navigationField = new JTextField();
		navigationField.setToolTipText("Please type URL to navigate");
		navigationLabel.setLabelFor(navigationField);
		navigationField.setText("http://www.stud.fit.vutbr.cz/~xlosko01/CSSBox/tests/window_properties.html");
		navigationPanel.add(navigationField, BorderLayout.CENTER);
		navigationField.setColumns(10);
		
		JPanel navigationButtonsPanel = new JPanel();
		navigationPanel.add(navigationButtonsPanel, BorderLayout.EAST);
		navigationButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 2, 0));
		
		navigateButton = new JButton("navigate");
		navigationButtonsPanel.add(navigateButton);
		
		JButton historyBackButton = new JButton("<-");
		navigationButtonsPanel.add(historyBackButton);
		
		JButton historyForwardButton = new JButton("->");
		navigationButtonsPanel.add(historyForwardButton);
		
		contentSplitPane = new JSplitPane();
		contentSplitPane.setResizeWeight(1.0);
		contentSplitPane.setDividerSize(3);
		contentSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		frame.getContentPane().add(contentSplitPane, BorderLayout.CENTER);
		
		JSplitPane footerSplitPane = new JSplitPane();
		footerSplitPane.setPreferredSize(new Dimension(179, 180));
		footerSplitPane.setMinimumSize(new Dimension(179, 180));
		footerSplitPane.setResizeWeight(0.9);
		footerSplitPane.setDividerSize(3);
		contentSplitPane.setRightComponent(footerSplitPane);
		
		JPanel consolePanel = new JPanel();
		consolePanel.setPreferredSize(new Dimension(200, 10));
		footerSplitPane.setRightComponent(consolePanel);
		consolePanel.setMinimumSize(new Dimension(200, 10));
		consolePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		consolePanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea consoleArea = new JTextArea();
		consolePanel.add(consoleArea, BorderLayout.CENTER);
		
		JLabel consoleLabel = new JLabel("Console");
		consoleLabel.setLabelFor(consoleArea);
		consolePanel.add(consoleLabel, BorderLayout.NORTH);
		
		JPanel panel_4 = new JPanel();
		panel_4.setMinimumSize(new Dimension(300, 10));
		panel_4.setPreferredSize(new Dimension(300, 10));
		footerSplitPane.setLeftComponent(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JPanel sourceCodePanel = new JPanel();
		panel_4.add(sourceCodePanel, BorderLayout.CENTER);
		sourceCodePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		sourceCodePanel.setLayout(new BorderLayout(0, 0));
		
		JLabel sourceCodeLabel = new JLabel("Source code");
		sourceCodePanel.add(sourceCodeLabel, BorderLayout.NORTH);
		
		sourceCodeTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
		sourceCodePanel.add(sourceCodeTabbedPane, BorderLayout.CENTER);
		
		JScrollPane sourceCodeScrollPane = new JScrollPane();
		sourceCodeTabbedPane.addTab("(rendered)", null, sourceCodeScrollPane, null);
		

		DefaultSyntaxKit.initKit();

		sourceCodeEditorPane = new JEditorPane();
		sourceCodeScrollPane.setViewportView(sourceCodeEditorPane);
		
		sourceCodeEditorPane.setEditable(false);
		sourceCodeEditorPane.setContentType("text/xhtml");
		
		sourceCodeLabel.setLabelFor(sourceCodeEditorPane);
		
		JPanel sourceCodeToolsPanel = new JPanel();
		sourceCodeToolsPanel.setPreferredSize(new Dimension(210, 10));
		sourceCodeToolsPanel.setMinimumSize(new Dimension(210, 10));
		panel_4.add(sourceCodeToolsPanel, BorderLayout.EAST);
		sourceCodeToolsPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		sourceCodeToolsPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel sourceCodeToolOptionsPanel = new JPanel();
		sourceCodeToolsPanel.add(sourceCodeToolOptionsPanel);
		sourceCodeToolOptionsPanel.setLayout(new BoxLayout(sourceCodeToolOptionsPanel, BoxLayout.Y_AXIS));
		
		JPanel panel = new JPanel();
		panel.setMaximumSize(new Dimension(32767, 23));
		sourceCodeToolOptionsPanel.add(panel);
		panel.setLayout(new BorderLayout(0, 0));
		
		JLabel demoLabel = new JLabel("Demo:");
		panel.add(demoLabel, BorderLayout.WEST);
		
		JComboBox demoComboBox = new JComboBox();
		panel.add(demoComboBox);
		demoComboBox.setMaximumSize(new Dimension(32767, 23));
		
		JButton loadDemoButton = new JButton("Load");
		panel.add(loadDemoButton, BorderLayout.EAST);
		loadDemoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JPanel panel_1 = new JPanel();
		panel_1.setMaximumSize(new Dimension(32767, 32));
		sourceCodeToolOptionsPanel.add(panel_1);
		
		newSourceCodeButton = new JButton("New");
		panel_1.add(newSourceCodeButton);
		newSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		openSourceCodeButton = new JButton("Open");
		panel_1.add(openSourceCodeButton);
		openSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		closeSourceCodeButton = new JButton("Close");
		panel_1.add(closeSourceCodeButton);
		
		JPanel panel_2 = new JPanel();
		panel_2.setMaximumSize(new Dimension(32767, 32));
		sourceCodeToolOptionsPanel.add(panel_2);
		
		saveSourceCodeButton = new JButton("Save");
		panel_2.add(saveSourceCodeButton);
		
		saveAsSourceCodeButton = new JButton("Save As");
		panel_2.add(saveAsSourceCodeButton);
		
		navigateSourceCodeButton = new JButton("Navigate");
		navigateSourceCodeButton.setMaximumSize(new Dimension(32767, 32));
		navigateSourceCodeButton.setFont(new Font("Tahoma", Font.BOLD, 14));
		navigateSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		sourceCodeToolOptionsPanel.add(navigateSourceCodeButton);
		
		JLabel sourceCodeToolsLabel = new JLabel("Source code tools");
		sourceCodeToolsPanel.add(sourceCodeToolsLabel, BorderLayout.NORTH);
		
		JSplitPane innerContentSplitPane = new JSplitPane();
		innerContentSplitPane.setDividerSize(3);
		innerContentSplitPane.setResizeWeight(0.9);
		contentSplitPane.setLeftComponent(innerContentSplitPane);
		
		JPanel scriptBrowserPanel = new JPanel();
		innerContentSplitPane.setLeftComponent(scriptBrowserPanel);
		scriptBrowserPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblNewLabel_1 = new JLabel("Displayed page");
		scriptBrowserPanel.add(lblNewLabel_1, BorderLayout.NORTH);
		
		scriptBrowserScrollPane = new JScrollPane();
		scriptBrowserPanel.add(scriptBrowserScrollPane, BorderLayout.CENTER);
		
		scriptBrowser = new ScriptBrowser();
		scriptBrowserScrollPane.setViewportView(scriptBrowser);
		
		JPanel scriptObjectsPanel = new JPanel();
		scriptObjectsPanel.setPreferredSize(new Dimension(200, 10));
		scriptObjectsPanel.setMinimumSize(new Dimension(200, 10));
		innerContentSplitPane.setRightComponent(scriptObjectsPanel);
		scriptObjectsPanel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane scriptObjectsSplitPane = new JSplitPane();
		scriptObjectsSplitPane.setResizeWeight(0.7);
		scriptObjectsSplitPane.setDividerSize(3);
		scriptObjectsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		scriptObjectsPanel.add(scriptObjectsSplitPane, BorderLayout.CENTER);
		
		JPanel objectViewerPanel = new JPanel();
		scriptObjectsSplitPane.setLeftComponent(objectViewerPanel);
		objectViewerPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel objectViewerLabel = new JLabel("Window object");
		objectViewerPanel.add(objectViewerLabel, BorderLayout.NORTH);
		
		JScrollPane windowObjectViewerScrollPane = new JScrollPane();
		objectViewerPanel.add(windowObjectViewerScrollPane);
		
		windowObjectViewer = new ScriptObjectViewer();
		windowObjectViewerScrollPane.setViewportView(windowObjectViewer);
		
		JPanel objectsWatchListPanel = new JPanel();
		scriptObjectsSplitPane.setRightComponent(objectsWatchListPanel);
		objectsWatchListPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel objectsWatchListlabel = new JLabel("Watch list");
		objectsWatchListPanel.add(objectsWatchListlabel, BorderLayout.NORTH);
		
		objectsWatchList = new ScriptObjectsWatchList();
		objectsWatchListPanel.add(objectsWatchList, BorderLayout.CENTER);
		
		newWatchedVariableField = new JTextField();
		objectsWatchListPanel.add(newWatchedVariableField, BorderLayout.SOUTH);
		newWatchedVariableField.setColumns(10);
	}
	
	public JEditorPane getSourceCodeEditorPane() {
		return sourceCodeEditorPane;
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
	
	public ScriptBrowser getScriptBrowser() {
		return scriptBrowser;
	}

	public JTabbedPane getSourceCodeTabbedPane() {
		return sourceCodeTabbedPane;
	}
	public JButton getNewSourceCodeButton() {
		return newSourceCodeButton;
	}
	public JButton getOpenSourceCodeButton() {
		return openSourceCodeButton;
	}
	public JButton getSaveSourceCodeButton() {
		return saveSourceCodeButton;
	}
	public JButton getCloseSourceCodeButton() {
		return closeSourceCodeButton;
	}
	public JButton getNavigateSourceCodeButton() {
		return navigateSourceCodeButton;
	}
	public JButton getSaveAsSourceCodeButton() {
		return saveAsSourceCodeButton;
	}
	public ScriptObjectViewer getWindowObjectViewer() {
		return windowObjectViewer;
	}
	public ScriptObjectsWatchList getObjectsWatchList() {
		return objectsWatchList;
	}
	public JTextField getNewWatchedVariableField() {
		return newWatchedVariableField;
	}
	public JScrollPane getScriptBrowserScrollPane() {
		return scriptBrowserScrollPane;
	}
}

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
import javax.swing.JTextPane;
import javax.swing.Box;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

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
	private JTextPane consolePane;
	private JComboBox demoComboBox;
	
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
		frame.setMinimumSize(new Dimension(700, 500));
		frame.setBounds(100, 100, 1024, 780);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		

		DefaultSyntaxKit.initKit();
		
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
						
						JSplitPane containerSplitPane = new JSplitPane();
						containerSplitPane.setResizeWeight(0.85);
						containerSplitPane.setDividerSize(3);
						containerPanel.add(containerSplitPane, BorderLayout.CENTER);
						
						contentSplitPane = new JSplitPane();
						containerSplitPane.setLeftComponent(contentSplitPane);
						contentSplitPane.setResizeWeight(1.0);
						contentSplitPane.setDividerSize(3);
						contentSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
								
								JPanel scriptBrowserPanel = new JPanel();
								contentSplitPane.setLeftComponent(scriptBrowserPanel);
								scriptBrowserPanel.setLayout(new BorderLayout(0, 0));
								
								JLabel displayedPageLabel = new JLabel("Displayed page");
								displayedPageLabel.setPreferredSize(new Dimension(73, 18));
								scriptBrowserPanel.add(displayedPageLabel, BorderLayout.NORTH);
								
								scriptBrowserScrollPane = new JScrollPane();
								scriptBrowserPanel.add(scriptBrowserScrollPane, BorderLayout.CENTER);
								
								scriptBrowser = new ScriptBrowser();
								scriptBrowserScrollPane.setViewportView(scriptBrowser);
								
								JPanel sourceCodeContainerPanel = new JPanel();
								contentSplitPane.setRightComponent(sourceCodeContainerPanel);
								sourceCodeContainerPanel.setMinimumSize(new Dimension(300, 200));
								sourceCodeContainerPanel.setPreferredSize(new Dimension(300, 200));
								sourceCodeContainerPanel.setLayout(new BorderLayout(0, 0));
								
								JPanel sourceCodePanel = new JPanel();
								sourceCodeContainerPanel.add(sourceCodePanel, BorderLayout.CENTER);
								sourceCodePanel.setBorder(new EmptyBorder(3, 3, 3, 3));
								sourceCodePanel.setLayout(new BorderLayout(0, 0));
								
								JLabel sourceCodeLabel = new JLabel("Source code");
								sourceCodeLabel.setPreferredSize(new Dimension(59, 18));
								sourceCodePanel.add(sourceCodeLabel, BorderLayout.NORTH);
								
								sourceCodeTabbedPane = new JTabbedPane(JTabbedPane.BOTTOM);
								sourceCodePanel.add(sourceCodeTabbedPane, BorderLayout.CENTER);
								
								JScrollPane sourceCodeScrollPane = new JScrollPane();
								sourceCodeTabbedPane.addTab("(rendered)", null, sourceCodeScrollPane, null);
								
										sourceCodeEditorPane = new JEditorPane();
										sourceCodeScrollPane.setViewportView(sourceCodeEditorPane);
										
										sourceCodeEditorPane.setEditable(false);
										sourceCodeEditorPane.setContentType("text/xhtml");
										
										sourceCodeLabel.setLabelFor(sourceCodeEditorPane);
										
										JPanel sourceCodeToolsPanel = new JPanel();
										sourceCodeToolsPanel.setPreferredSize(new Dimension(210, 10));
										sourceCodeToolsPanel.setMinimumSize(new Dimension(210, 10));
										sourceCodeContainerPanel.add(sourceCodeToolsPanel, BorderLayout.EAST);
										sourceCodeToolsPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
										sourceCodeToolsPanel.setLayout(new BorderLayout(0, 0));
										
										JPanel sourceCodeToolOptionsPanel = new JPanel();
										sourceCodeToolsPanel.add(sourceCodeToolOptionsPanel, BorderLayout.CENTER);
										sourceCodeToolOptionsPanel.setLayout(new BoxLayout(sourceCodeToolOptionsPanel, BoxLayout.Y_AXIS));
										
										Component sourceCodeToolOptionsTopGlue = Box.createVerticalGlue();
										sourceCodeToolOptionsPanel.add(sourceCodeToolOptionsTopGlue);
										
										JPanel demoPanel = new JPanel();
										demoPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
										demoPanel.setMaximumSize(new Dimension(32767, 23));
										sourceCodeToolOptionsPanel.add(demoPanel);
										GridBagLayout gbl_demoPanel = new GridBagLayout();
										gbl_demoPanel.columnWidths = new int[]{143, 47, 0};
										gbl_demoPanel.rowHeights = new int[]{23, 0};
										gbl_demoPanel.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
										gbl_demoPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
										demoPanel.setLayout(gbl_demoPanel);
										
										demoComboBox = new JComboBox();
										GridBagConstraints gbc_demoComboBox = new GridBagConstraints();
										gbc_demoComboBox.fill = GridBagConstraints.BOTH;
										gbc_demoComboBox.insets = new Insets(0, 0, 0, 5);
										gbc_demoComboBox.gridx = 0;
										gbc_demoComboBox.gridy = 0;
										demoPanel.add(demoComboBox, gbc_demoComboBox);
										demoComboBox.setMaximumSize(new Dimension(32767, 23));
										
										JButton loadDemoButton = new JButton("Load");
										GridBagConstraints gbc_loadDemoButton = new GridBagConstraints();
										gbc_loadDemoButton.anchor = GridBagConstraints.NORTHWEST;
										gbc_loadDemoButton.gridx = 1;
										gbc_loadDemoButton.gridy = 0;
										demoPanel.add(loadDemoButton, gbc_loadDemoButton);
										loadDemoButton.setAlignmentX(Component.CENTER_ALIGNMENT);
										
										JPanel newOpenCloseButtonsPanel = new JPanel();
										newOpenCloseButtonsPanel.setMaximumSize(new Dimension(32767, 32));
										sourceCodeToolOptionsPanel.add(newOpenCloseButtonsPanel);
										
										newSourceCodeButton = new JButton("New");
										newOpenCloseButtonsPanel.add(newSourceCodeButton);
										newSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
										
										openSourceCodeButton = new JButton("Open");
										newOpenCloseButtonsPanel.add(openSourceCodeButton);
										openSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
										
										closeSourceCodeButton = new JButton("Close");
										newOpenCloseButtonsPanel.add(closeSourceCodeButton);
										
										JPanel saveButtonsPanel = new JPanel();
										saveButtonsPanel.setMaximumSize(new Dimension(32767, 32));
										sourceCodeToolOptionsPanel.add(saveButtonsPanel);
										
										saveSourceCodeButton = new JButton("Save");
										saveButtonsPanel.add(saveSourceCodeButton);
										
										saveAsSourceCodeButton = new JButton("Save As");
										saveButtonsPanel.add(saveAsSourceCodeButton);
										
										navigateSourceCodeButton = new JButton("Navigate");
										navigateSourceCodeButton.setMaximumSize(new Dimension(32767, 32));
										navigateSourceCodeButton.setFont(new Font("Tahoma", Font.BOLD, 14));
										navigateSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
										sourceCodeToolOptionsPanel.add(navigateSourceCodeButton);
										
										Component sourceCodeToolOptionsButtomGlue = Box.createVerticalGlue();
										sourceCodeToolOptionsPanel.add(sourceCodeToolOptionsButtomGlue);
										
										JSplitPane sideBarSplitPane = new JSplitPane();
										containerSplitPane.setRightComponent(sideBarSplitPane);
										sideBarSplitPane.setResizeWeight(1.0);
										sideBarSplitPane.setDividerSize(3);
										sideBarSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
										
										JSplitPane scriptObjectsSplitPane = new JSplitPane();
										scriptObjectsSplitPane.setPreferredSize(new Dimension(179, 450));
										scriptObjectsSplitPane.setMinimumSize(new Dimension(179, 170));
										sideBarSplitPane.setLeftComponent(scriptObjectsSplitPane);
										scriptObjectsSplitPane.setResizeWeight(1.0);
										scriptObjectsSplitPane.setDividerSize(3);
										scriptObjectsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
										
										JPanel objectViewerPanel = new JPanel();
										objectViewerPanel.setMinimumSize(new Dimension(10, 80));
										scriptObjectsSplitPane.setLeftComponent(objectViewerPanel);
										objectViewerPanel.setLayout(new BorderLayout(0, 0));
										
										JLabel objectViewerLabel = new JLabel("Window object");
										objectViewerLabel.setPreferredSize(new Dimension(71, 18));
										objectViewerPanel.add(objectViewerLabel, BorderLayout.NORTH);
										
										JScrollPane windowObjectViewerScrollPane = new JScrollPane();
										objectViewerPanel.add(windowObjectViewerScrollPane);
										
										windowObjectViewer = new ScriptObjectViewer();
										windowObjectViewerScrollPane.setViewportView(windowObjectViewer);
										
										JPanel objectsWatchListPanel = new JPanel();
										objectsWatchListPanel.setPreferredSize(new Dimension(10, 150));
										objectsWatchListPanel.setMinimumSize(new Dimension(10, 80));
										scriptObjectsSplitPane.setRightComponent(objectsWatchListPanel);
										objectsWatchListPanel.setLayout(new BorderLayout(0, 0));
										
										JLabel objectsWatchListlabel = new JLabel("Watch list");
										objectsWatchListlabel.setPreferredSize(new Dimension(47, 18));
										objectsWatchListPanel.add(objectsWatchListlabel, BorderLayout.NORTH);
										
										newWatchedVariableField = new JTextField();
										objectsWatchListPanel.add(newWatchedVariableField, BorderLayout.SOUTH);
										newWatchedVariableField.setColumns(10);
										
										JScrollPane scrollPane = new JScrollPane();
										objectsWatchListPanel.add(scrollPane, BorderLayout.CENTER);
										
										objectsWatchList = new ScriptObjectsWatchList();
										objectsWatchList.setPreferredSize(new Dimension(0, 0));
										objectsWatchList.setMinimumSize(new Dimension(0, 0));
										scrollPane.setViewportView(objectsWatchList);
										
										JPanel consolePanel = new JPanel();
										sideBarSplitPane.setRightComponent(consolePanel);
										consolePanel.setPreferredSize(new Dimension(200, 200));
										consolePanel.setMinimumSize(new Dimension(200, 80));
										consolePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
										consolePanel.setLayout(new BorderLayout(0, 0));
										
										JLabel consoleLabel = new JLabel("Console");
										consoleLabel.setPreferredSize(new Dimension(38, 18));
										consolePanel.add(consoleLabel, BorderLayout.NORTH);
										
										JScrollPane consoleScrollPane = new JScrollPane();
										consolePanel.add(consoleScrollPane, BorderLayout.CENTER);
										
										consolePane = new JTextPane();
										consoleScrollPane.setViewportView(consolePane);
										consolePane.setEditable(false);
										consoleLabel.setLabelFor(consolePane);
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
	public JTextPane getConsolePane() {
		return consolePane;
	}
	public JComboBox getDemoComboBox() {
		return demoComboBox;
	}
}

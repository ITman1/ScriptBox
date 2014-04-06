package org.fit.cssbox.scriptbox.demo;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fit.cssbox.scriptbox.ui.ScriptBrowser;
import java.awt.FlowLayout;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import java.awt.Font;
import javax.swing.border.EmptyBorder;

public class JavaScriptTester {

	ScriptBrowser scriptBrowser;
	private JFrame frame;
	private JTextField navigationField;
	private JButton navigateButton;
	private JTextArea sourceCodeArea;
	
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
		frame.setBounds(100, 100, 638, 427);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{313, 0, 0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 1.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);
		
		JPanel navigationPanel = new JPanel();
		navigationPanel.setBorder(new EmptyBorder(0, 0, 2, 0));
		GridBagConstraints gbc_navigationPanel = new GridBagConstraints();
		gbc_navigationPanel.gridwidth = 3;
		gbc_navigationPanel.insets = new Insets(0, 0, 5, 0);
		gbc_navigationPanel.fill = GridBagConstraints.BOTH;
		gbc_navigationPanel.gridx = 0;
		gbc_navigationPanel.gridy = 0;
		frame.getContentPane().add(navigationPanel, gbc_navigationPanel);
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
		
		JPanel scriptBrowserPanel = new JPanel();
		GridBagConstraints gbc_scriptBrowserPanel = new GridBagConstraints();
		gbc_scriptBrowserPanel.gridwidth = 2;
		gbc_scriptBrowserPanel.insets = new Insets(0, 0, 5, 5);
		gbc_scriptBrowserPanel.fill = GridBagConstraints.BOTH;
		gbc_scriptBrowserPanel.gridx = 0;
		gbc_scriptBrowserPanel.gridy = 1;
		frame.getContentPane().add(scriptBrowserPanel, gbc_scriptBrowserPanel);
		scriptBrowserPanel.setLayout(new BorderLayout(0, 0));
		
		scriptBrowser = new ScriptBrowser();
		scriptBrowserPanel.add(scriptBrowser);
		
		JLabel lblNewLabel_1 = new JLabel("Displayed page");
		scriptBrowserPanel.add(lblNewLabel_1, BorderLayout.NORTH);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(0, 0, 5, 0);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 2;
		gbc_tabbedPane.gridy = 1;
		frame.getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		JPanel sourceCodePanel = new JPanel();
		GridBagConstraints gbc_sourceCodePanel = new GridBagConstraints();
		gbc_sourceCodePanel.insets = new Insets(0, 0, 0, 5);
		gbc_sourceCodePanel.fill = GridBagConstraints.BOTH;
		gbc_sourceCodePanel.gridx = 0;
		gbc_sourceCodePanel.gridy = 2;
		frame.getContentPane().add(sourceCodePanel, gbc_sourceCodePanel);
		sourceCodePanel.setLayout(new BorderLayout(0, 0));
		
		JScrollPane sourceCodeScrollPane = new JScrollPane();
		sourceCodePanel.add(sourceCodeScrollPane, BorderLayout.CENTER);
		
		sourceCodeArea = new JTextArea();
		sourceCodeScrollPane.setViewportView(sourceCodeArea);
		
		JLabel sourceCodeLabel = new JLabel("Source code");
		sourceCodeLabel.setLabelFor(sourceCodeArea);
		sourceCodePanel.add(sourceCodeLabel, BorderLayout.NORTH);
		
		JPanel sourceCodeToolsPanel = new JPanel();
		GridBagConstraints gbc_sourceCodeToolsPanel = new GridBagConstraints();
		gbc_sourceCodeToolsPanel.fill = GridBagConstraints.BOTH;
		gbc_sourceCodeToolsPanel.insets = new Insets(0, 0, 0, 5);
		gbc_sourceCodeToolsPanel.gridx = 1;
		gbc_sourceCodeToolsPanel.gridy = 2;
		frame.getContentPane().add(sourceCodeToolsPanel, gbc_sourceCodeToolsPanel);
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
		
		JButton newSourceCodeButton = new JButton("New");
		panel_1.add(newSourceCodeButton);
		newSourceCodeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton openSourceCode = new JButton("Open");
		panel_1.add(openSourceCode);
		openSourceCode.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JButton saveSourceCodeButton = new JButton("Save");
		panel_1.add(saveSourceCodeButton);
		
		JPanel panel_2 = new JPanel();
		sourceCodeToolOptionsPanel.add(panel_2);
		
		JButton btnNavigate = new JButton("Navigate");
		btnNavigate.setMaximumSize(new Dimension(32767, 32));
		btnNavigate.setFont(new Font("Tahoma", Font.BOLD, 14));
		btnNavigate.setAlignmentX(Component.CENTER_ALIGNMENT);
		sourceCodeToolOptionsPanel.add(btnNavigate);
		
		JLabel sourceCodeToolsLabel = new JLabel("Source code tools");
		sourceCodeToolsPanel.add(sourceCodeToolsLabel, BorderLayout.NORTH);
		
		JPanel consolePanel = new JPanel();
		GridBagConstraints gbc_consolePanel = new GridBagConstraints();
		gbc_consolePanel.fill = GridBagConstraints.BOTH;
		gbc_consolePanel.gridx = 2;
		gbc_consolePanel.gridy = 2;
		frame.getContentPane().add(consolePanel, gbc_consolePanel);
		consolePanel.setLayout(new BorderLayout(0, 0));
		
		JTextArea consoleArea = new JTextArea();
		consolePanel.add(consoleArea, BorderLayout.CENTER);
		
		JLabel consoleLabel = new JLabel("Console");
		consoleLabel.setLabelFor(consoleArea);
		consolePanel.add(consoleLabel, BorderLayout.NORTH);
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
	public JTextArea getSourceCodeArea() {
		return sourceCodeArea;
	}
}

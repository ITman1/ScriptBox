/**
 * JavaScriptTesterUi.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.demo.tester;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;

import jsyntaxpane.DefaultSyntaxKit;

import org.fit.cssbox.scriptbox.demo.browser.BrowserUi;
import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

/**
 * Class with the view of JavaScript tester.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptTesterUi implements BrowserUi {
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
	private JButton objectViewerRefreshButton;
	private JButton objectsWatchListRefreshButton;
	private JButton consoleClearButton;
	private JLabel statusLabel;
	private JButton historyBackButton;
	private JButton historyForwardButton;
	
	/**
	 * Create the view components of the JavaScript tester.
	 */
	public JavaScriptTesterUi() {
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
		navigationLabel.setBorder(new EmptyBorder(0, 3, 0, 13));
		navigationPanel.add(navigationLabel, BorderLayout.WEST);

		navigationField = new JTextField();
		navigationField.setToolTipText("Please type URL to navigate");
		navigationLabel.setLabelFor(navigationField);
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
		displayedPageLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
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
		sourceCodeLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
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
		sourceCodeToolsPanel.setPreferredSize(new Dimension(220, 10));
		sourceCodeToolsPanel.setMinimumSize(new Dimension(220, 10));
		sourceCodeContainerPanel.add(sourceCodeToolsPanel, BorderLayout.EAST);
		sourceCodeToolsPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		sourceCodeToolsPanel.setLayout(new BorderLayout(0, 0));

		JPanel sourceCodeToolOptionsPanel = new JPanel();
		sourceCodeToolsPanel.add(sourceCodeToolOptionsPanel, BorderLayout.CENTER);
		sourceCodeToolOptionsPanel.setLayout(new BoxLayout(sourceCodeToolOptionsPanel, BoxLayout.Y_AXIS));

		Component sourceCodeToolOptionsTopGlue = Box.createVerticalGlue();
		sourceCodeToolOptionsPanel.add(sourceCodeToolOptionsTopGlue);

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

		JScrollPane windowObjectViewerScrollPane = new JScrollPane();
		objectViewerPanel.add(windowObjectViewerScrollPane);

		windowObjectViewer = new ScriptObjectViewer();
		windowObjectViewerScrollPane.setViewportView(windowObjectViewer);
		
		JPanel objectViewerHeaderPanel = new JPanel();
		objectViewerPanel.add(objectViewerHeaderPanel, BorderLayout.NORTH);
		objectViewerHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel objectViewerLabel = new JLabel("Window object");
		objectViewerLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
		objectViewerHeaderPanel.add(objectViewerLabel);
		objectViewerLabel.setPreferredSize(new Dimension(71, 18));
				
		objectViewerRefreshButton = new JButton("refresh");
		objectViewerHeaderPanel.add(objectViewerRefreshButton, BorderLayout.EAST);

		JPanel objectsWatchListPanel = new JPanel();
		objectsWatchListPanel.setPreferredSize(new Dimension(10, 150));
		objectsWatchListPanel.setMinimumSize(new Dimension(10, 80));
		scriptObjectsSplitPane.setRightComponent(objectsWatchListPanel);
		objectsWatchListPanel.setLayout(new BorderLayout(0, 0));

		newWatchedVariableField = new JTextField();
		objectsWatchListPanel.add(newWatchedVariableField, BorderLayout.SOUTH);
		newWatchedVariableField.setColumns(10);

		JScrollPane watchListScrollPane = new JScrollPane();
		objectsWatchListPanel.add(watchListScrollPane, BorderLayout.CENTER);

		objectsWatchList = new ScriptObjectsWatchList();
		/*objectsWatchList.setPreferredSize(new Dimension(0, 0));
		objectsWatchList.setMinimumSize(new Dimension(0, 0));*/
		watchListScrollPane.setViewportView(objectsWatchList);
		
		JPanel objectsWatchListHeaderPanel = new JPanel();
		objectsWatchListPanel.add(objectsWatchListHeaderPanel, BorderLayout.NORTH);
		objectsWatchListHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel objectsWatchListlabel = new JLabel("Watch list");
		objectsWatchListlabel.setBorder(new EmptyBorder(0, 3, 0, 0));
		objectsWatchListHeaderPanel.add(objectsWatchListlabel);
		objectsWatchListlabel.setPreferredSize(new Dimension(47, 18));
				
		objectsWatchListRefreshButton = new JButton("refresh");
		objectsWatchListHeaderPanel.add(objectsWatchListRefreshButton, BorderLayout.EAST);

		JPanel consolePanel = new JPanel();
		sideBarSplitPane.setRightComponent(consolePanel);
		consolePanel.setPreferredSize(new Dimension(200, 200));
		consolePanel.setMinimumSize(new Dimension(200, 80));
		consolePanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		consolePanel.setLayout(new BorderLayout(0, 0));

		JScrollPane consoleScrollPane = new JScrollPane();
		consolePanel.add(consoleScrollPane, BorderLayout.CENTER);

		consolePane = new JTextPane();
		consoleScrollPane.setViewportView(consolePane);
		consolePane.setEditable(false);
		
		JPanel consoleHeaderPanel = new JPanel();
		consolePanel.add(consoleHeaderPanel, BorderLayout.NORTH);
		consoleHeaderPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel consoleLabel = new JLabel("Console");
		consoleLabel.setBorder(new EmptyBorder(0, 3, 0, 0));
		consoleHeaderPanel.add(consoleLabel);
		consoleLabel.setPreferredSize(new Dimension(38, 18));
		consoleLabel.setLabelFor(consolePane);
				
		consoleClearButton = new JButton("clear");
		consoleClearButton.setPreferredSize(new Dimension(67, 23));
		consoleHeaderPanel.add(consoleClearButton, BorderLayout.EAST);
				
		JPanel statusBar = new JPanel();
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		containerPanel.add(statusBar, BorderLayout.SOUTH);
				
		statusLabel = new JLabel("(no content loaded)");
		statusBar.add(statusLabel);
	}

	public JEditorPane getSourceCodeEditorPane() {
		return sourceCodeEditorPane;
	}

	@Override
	public JFrame getWindow() {
		return frame;
	}

	@Override
	public JButton getNavigateButton() {
		return navigateButton;
	}

	@Override
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
	
	public JButton getObjectViewerRefreshButton() {
		return objectViewerRefreshButton;
	}
	
	public JButton getObjectsWatchListRefreshButton() {
		return objectsWatchListRefreshButton;
	}
	
	public JButton getConsoleClearButton() {
		return consoleClearButton;
	}
	
	public JLabel getStatusLabel() {
		return statusLabel;
	}
	
	@Override
	public JButton getHistoryBackButton() {
		return historyBackButton;
	}

	@Override
	public JButton getHistoryForwardButton() {
		return historyForwardButton;
	}
}

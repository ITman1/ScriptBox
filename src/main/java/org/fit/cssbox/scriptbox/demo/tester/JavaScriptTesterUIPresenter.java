/**
 * JavaScriptTesterUiController.java
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

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import jsyntaxpane.DefaultSyntaxKit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowserUIPresenter;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent;

/**
 * Class of the presenter of the view.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptTesterUIPresenter extends SimpleBrowserUIPresenter {
	private static ConsoleInjector consoleInjector;
	private static int NEW_COUNTER = 0;
	
	private JavaScriptTesterUi tester;

	private Map<Integer, File> openedFiles;
	
	private final JFileChooser fileChooser = new JFileChooser();
	

	private JTabbedPane sourceCodeTabbedPane;
	private JTextField newWatchedVariableField;
	private JEditorPane sourceCodeEditorPane;
	private ScriptObjectViewer windowObjectViewer;
	private ScriptObjectsWatchList scriptObjectsWatchList;
	
	private JLabel statusLabel;
	
	private JButton navigateSourceCodeButton;
	private JButton openSourceCodeButton;
	private JButton saveSourceCodeButton;
	private JButton saveAsSourceCodeButton;
	private JButton closeSourceCodeButton;
	private JButton newSourceCodeButton;
	private JButton objectViewerRefreshButton;
	private JButton objectsWatchListRefreshButton;
	private JButton consoleClearButton;
	
	private ChangeListener onTabChangedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            updateUI();
        }
    };
	
	private ActionListener onOpenSourceCodeListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showOpenDialog(tester.getWindow());

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            openSourceCodeTab(file);
	        }
		}
	};
	
	private ActionListener onSaveSourceCodeListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = sourceCodeTabbedPane.getSelectedIndex();
			File file = openedFiles.get(index);
			if (file == null) {
				onSaveAsSourceCodeListener.actionPerformed(e);
			} else {
				saveSourceCodeTab(file);
			}
		}
	};
	
	private ActionListener onSaveAsSourceCodeListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int returnVal = fileChooser.showSaveDialog(tester.getWindow());

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            if (saveSourceCodeTab(file)) {
	            	setCurrentTabTitleFromFile(file);
	            }
	        }
		}
	};
	
	private ActionListener onCloseSourceCodeListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			closeSourceCodeTab();
		}
	};
	
	private ActionListener onNewSourceCodeListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			addSourceCodeTab();
		}
	};
	
	private ActionListener onNavigateSourceCodeButton = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			int index = sourceCodeTabbedPane.getSelectedIndex();
			JEditorPane pane = getSelectedEditorPane();
			String sourceCode = pane.getText();
			
			if (index > 0 || loadedDocument != null) {
				navigateSoureCode(sourceCode);
			}
		}
	};
		
	private ActionListener onObjectViewerRefresh = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			windowObjectViewer.refresh();
		}
	};
	
	private ActionListener onObjectsWatchListRefresh = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			scriptObjectsWatchList.refresh();
		}
	};
	
	private ActionListener onConsoleClear = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			consoleInjector.clearConsole();
		}
	};
	
	private KeyAdapter scriptObjectsWatchListKeyListener = new KeyAdapter() {
		@Override
		public void keyPressed(KeyEvent e) {
			char pressedKey = e.getKeyChar(); 
			TreePath path = scriptObjectsWatchList.getSelectionPath();
			
			if (pressedKey == KeyEvent.VK_DELETE && path != null) {
				Object selectedNode = path.getPathComponent(1);
				
				if (selectedNode instanceof MutableTreeNode) {
					scriptObjectsWatchList.removeVariable((MutableTreeNode)selectedNode);
				}
			}
		}
	};
	
	public JavaScriptTesterUIPresenter(WindowBrowsingContext windowBrowsingContext) {
		super(windowBrowsingContext, new JavaScriptTesterUi());
		
		openedFiles = new HashMap<Integer, File>();
		
		registerEventListeners();
		registerJavaScriptInjectors();
	}
	
	public void showWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame window = tester.getWindow();
					window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(tester.getWindow(),
						    "Unable to run application.",
						    "Internal error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

    protected ActionListener onNewVariableEntered = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String variableName = newWatchedVariableField.getText();
			scriptObjectsWatchList.addVariable(variableName);
		}
	};
		
	@Override
	protected void onDocumentChanged() {
		super.onDocumentChanged();
		
		String sourceCode = loadedDocument.getParserSource();
		sourceCodeEditorPane.setText(sourceCode);

		windowObjectViewer.refresh();
		scriptObjectsWatchList.refresh();
	}
	
	@Override
	public void updateUI() {
		super.updateUI();
		
		navigateSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		closeSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0);
		saveSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		saveAsSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		
		if (navigationResult != null) {
			if (navigationResult == NavigationControllerEvent.EventType.NAVIGATION_CANCELLED) {
				statusLabel.setText("navigation cancelled");
			} else if (navigationResult == NavigationControllerEvent.EventType.NAVIGATION_COMPLETED) {
				statusLabel.setText("navigation completed");
			} else if (navigationResult == NavigationControllerEvent.EventType.NAVIGATION_NEW) {
				statusLabel.setText("(loading)");
			}
		}
	}
	
	@Override
	protected void initializeUiComponents() {
		super.initializeUiComponents();
		
		tester = (JavaScriptTesterUi)ui;
		
		sourceCodeTabbedPane = tester.getSourceCodeTabbedPane();
		newWatchedVariableField = tester.getNewWatchedVariableField();
		sourceCodeEditorPane = tester.getSourceCodeEditorPane();
		windowObjectViewer = tester.getWindowObjectViewer();
		scriptObjectsWatchList = tester.getObjectsWatchList();
		
		statusLabel = tester.getStatusLabel();
		navigateSourceCodeButton = tester.getNavigateSourceCodeButton();
		openSourceCodeButton = tester.getOpenSourceCodeButton();
		saveSourceCodeButton = tester.getSaveSourceCodeButton();
		saveAsSourceCodeButton = tester.getSaveAsSourceCodeButton();
		closeSourceCodeButton = tester.getCloseSourceCodeButton();
		newSourceCodeButton = tester.getNewSourceCodeButton();
		objectViewerRefreshButton = tester.getObjectViewerRefreshButton();
		objectsWatchListRefreshButton = tester.getObjectsWatchListRefreshButton();
		consoleClearButton = tester.getConsoleClearButton();
		
		BrowsingUnit browsingUnit = windowBrowsingContext.getBrowsingUnit();
		windowObjectViewer.setBrowsingUnit(browsingUnit);
		scriptObjectsWatchList.setBrowsingUnit(browsingUnit);
	}
	
	@Override
	protected void unregisterEventListeners() {
		super.unregisterEventListeners();
		
		sourceCodeTabbedPane.removeChangeListener(onTabChangedListener);
		
		navigateSourceCodeButton.removeActionListener(onNavigateSourceCodeButton);
		openSourceCodeButton.removeActionListener(onOpenSourceCodeListener);
		saveSourceCodeButton.removeActionListener(onSaveSourceCodeListener);
		saveAsSourceCodeButton.removeActionListener(onSaveAsSourceCodeListener);
		closeSourceCodeButton.removeActionListener(onCloseSourceCodeListener);
		newSourceCodeButton.removeActionListener(onNewSourceCodeListener);
		objectViewerRefreshButton.removeActionListener(onObjectViewerRefresh);
		objectsWatchListRefreshButton.removeActionListener(onObjectsWatchListRefresh);
		consoleClearButton.removeActionListener(onConsoleClear);
		
		newWatchedVariableField.removeActionListener(onNewVariableEntered);
		scriptObjectsWatchList.removeKeyListener(scriptObjectsWatchListKeyListener);
	}
	
	private void registerEventListeners() {
		sourceCodeTabbedPane.addChangeListener(onTabChangedListener);
		
		navigateSourceCodeButton.addActionListener(onNavigateSourceCodeButton);
		openSourceCodeButton.addActionListener(onOpenSourceCodeListener);
		saveSourceCodeButton.addActionListener(onSaveSourceCodeListener);
		saveAsSourceCodeButton.addActionListener(onSaveAsSourceCodeListener);
		closeSourceCodeButton.addActionListener(onCloseSourceCodeListener);
		newSourceCodeButton.addActionListener(onNewSourceCodeListener);
		objectViewerRefreshButton.addActionListener(onObjectViewerRefresh);
		objectsWatchListRefreshButton.addActionListener(onObjectsWatchListRefresh);
		consoleClearButton.addActionListener(onConsoleClear);
		
		newWatchedVariableField.addActionListener(onNewVariableEntered);
		scriptObjectsWatchList.addKeyListener(scriptObjectsWatchListKeyListener);
	}
	
	private void navigateSoureCode(String sourceCode) {
		File file = null;
		FileWriter writer = null;
		try {
			file = File.createTempFile("html-page-", ".html");
			writer = new FileWriter(file);
			writer.write(sourceCode);
		} catch (IOException e1) {
			file = null;
			e1.printStackTrace();
			JOptionPane.showMessageDialog(tester.getWindow(),
				    "Unable to create temporary file where to navigate.",
				    "Internal error",
				    JOptionPane.ERROR_MESSAGE);
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		URL fileURL = null;
		if (file != null) {
			try {
				fileURL = file.toURI().toURL();
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(tester.getWindow(),
					    "Unable to get URL from file.",
					    "Internal error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	
	
		if (fileURL != null) {
			try {
				BrowsingUnit browsingUnit = windowBrowsingContext.getBrowsingUnit();
				browsingUnit.navigate(fileURL);
			} catch (Exception e1) {
				e1.printStackTrace();
				JOptionPane.showMessageDialog(tester.getWindow(),
					    "Unable to get navigate temporary file with source code.",
					    "Internal error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private boolean saveSourceCodeTab(File file) {
		int paneIndex = sourceCodeTabbedPane.getSelectedIndex();
		JEditorPane pane = getSelectedEditorPane(); 
		
		try {
			FileWriter writer = new FileWriter(file);
			String content = pane.getText();
			writer.write(content);
			
			if (paneIndex > 0) {
				openedFiles.put(paneIndex, file);
			}

			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(tester.getWindow(),
				    "Unable to save file with source code.",
				    "File I/O error",
				    JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}
	
	private void setCurrentTabTitleFromFile(File file) {
		int paneIndex = sourceCodeTabbedPane.getTabCount() - 1;
		String filename = file.getName();
		String paneName = FilenameUtils.removeExtension(filename);
		sourceCodeTabbedPane.setTitleAt(paneIndex, paneName);
	}
	
	private void openSourceCodeTab(File file) {
		JEditorPane pane = addSourceCodeTab();
		int paneIndex = sourceCodeTabbedPane.getTabCount() - 1;
		String sourceCode;
		try {
			sourceCode = FileUtils.readFileToString(file);
			setCurrentTabTitleFromFile(file);
			pane.setText(sourceCode);
			openedFiles.put(paneIndex, file);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(tester.getWindow(),
				    "Unable to open file with source code.",
				    "File I/O error",
				    JOptionPane.ERROR_MESSAGE);
			closeSourceCodeTab();
		}
	}
	
	private JEditorPane addSourceCodeTab() {
		JScrollPane sourceCodeScrollPane = new JScrollPane();
		NEW_COUNTER++;
		sourceCodeTabbedPane.addTab("New " + NEW_COUNTER, null, sourceCodeScrollPane, null);
		sourceCodeTabbedPane.setSelectedIndex(sourceCodeTabbedPane.getTabCount() - 1);
		
		DefaultSyntaxKit.initKit();

        final JEditorPane codeEditor = new JEditorPane();
		sourceCodeScrollPane.setViewportView(codeEditor);
		
        codeEditor.setContentType("text/xhtml");
		
		return codeEditor;
	}

	private void closeSourceCodeTab() {
		int selectedIndex = sourceCodeTabbedPane.getSelectedIndex();
		
		if (selectedIndex > 0) {
			sourceCodeTabbedPane.remove(selectedIndex);
			openedFiles.remove(selectedIndex);
		}
	}
	
	private JEditorPane getSelectedEditorPane() {
		JScrollPane scrollPane = (JScrollPane)sourceCodeTabbedPane.getSelectedComponent();
		JViewport viewport = scrollPane.getViewport(); 
		return (JEditorPane)viewport.getView(); 
	}
	
	// This must be called before initializing UI components which uses browsing unit, 
	// because these components creates script engines and there would not be any inject registered.
	private void registerJavaScriptInjectors() {
		if (consoleInjector == null) {
			consoleInjector = new ConsoleInjector(tester.getConsolePane());
		}
		if (!consoleInjector.isRegistered()) {
			consoleInjector.registerScriptContextInject();
		}
	}
}

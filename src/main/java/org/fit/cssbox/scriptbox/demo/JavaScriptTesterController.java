package org.fit.cssbox.scriptbox.demo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jsyntaxpane.DefaultSyntaxKit;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.history.SessionHistoryEvent;
import org.fit.cssbox.scriptbox.history.SessionHistoryListener;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerListener;
import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

public class JavaScriptTesterController {
	private static int NEW_COUNTER = 0;
	private JavaScriptTester tester;
	private UserAgent userAgent;
	private BrowsingUnit browsingUnit;
	private BrowsingContext windowContext;
	private SessionHistory sessionHistory;
	private NavigationController navigationController;
	private Html5DocumentImpl loadedDocument;
	private NavigationAttempt navigationAttempt;
	private Map<Integer, File> openedFiles;
	
	final JFileChooser fileChooser = new JFileChooser();
	
	private ScriptBrowser scriptBrowser;
	private JTabbedPane sourceCodeTabbedPane;
	private JTextField navigationField;
	private JTextField newWatchedVariableField;
	private JEditorPane sourceCodeEditorPane;
	private ScriptObjectViewer windowObjectViewer;
	private ScriptObjectsWatchList scriptObjectsWatchList;
	
	private JButton navigateButton;
	private JButton navigateSourceCodeButton;
	private JButton openSourceCodeButton;
	private JButton saveSourceCodeButton;
	private JButton saveAsSourceCodeButton;
	private JButton closeSourceCodeButton;
	private JButton newSourceCodeButton;
	
	private NavigationControllerListener navigationControllerListener = new NavigationControllerListener() {
		@Override
		public void onNavigationEvent(final NavigationControllerEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					switch (event.getEventType()) {
					case DESTROYED:
					case NAVIGATION_CANCELLED:
						navigationAttempt = null;
						onNavigationCancelled();
						break;
					case NAVIGATION_COMPLETED:
						navigationAttempt = null;
						onNavigationCompleted();
						break;
					case NAVIGATION_NEW:
						NavigationAttempt attempt = event.getNavigationAttempt();
						onNavigationStarted(attempt);
						break;
				}

					updateUi();
				}
			});
		}
    };
	
	private SessionHistoryListener sessionHistoryListener = new SessionHistoryListener() {
		@Override
		public void onHistoryEvent(final SessionHistoryEvent event) {
			if (event.getEventType() == SessionHistoryEvent.EventType.TRAVERSED) {
				SessionHistoryEntry currentEntry = event.getTarget();
				final String urlString = currentEntry.getURL().toExternalForm();
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						navigationField.setText(urlString);
					}
				});
			}
		}
    };
	
	private ChangeListener onTabChangedListener = new ChangeListener() {
        public void stateChanged(ChangeEvent e) {
            updateUi();
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
			int returnVal = fileChooser.showOpenDialog(tester.getWindow());

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = fileChooser.getSelectedFile();
	            saveSourceCodeTab(file);
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
			URL fileURL = null;
			if (index > 0 || loadedDocument != null) {
				File file = openedFiles.get(index);
				
				if (file == null) {
					try {
						file = File.createTempFile("html-page-", ".html");
					} catch (IOException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(tester.getWindow(),
							    "Unable to create temporary file where to navigate.",
							    "Internal error",
							    JOptionPane.ERROR_MESSAGE);
					} 
				}
				
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
			}
			
			if (fileURL != null) {
				try {
					URI uri = fileURL.toURI();
					File file = new File(uri);
					FileWriter writer = new FileWriter(file);
					String str = pane.getText();
					writer.write(str);
					writer.close();
					browsingUnit.navigate(fileURL);
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		}
	};
	
	private ActionListener onNavigateListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String navigationUrl = navigationField.getText();
			
			if (navigationAttempt != null) {
				navigationController.cancelAllNavigationAttempts();
			} else {
				try {
					browsingUnit.navigate(navigationUrl);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(tester.getWindow(),
						    "Please fix typed URL address.",
						    "Malformed URL",
						    JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	};
	
	public JavaScriptTesterController() {
		tester = new JavaScriptTester();
		userAgent = new JavaScriptTesterUserAgent(tester);
		
		browsingUnit = userAgent.openBrowsingUnit();
		windowContext = browsingUnit.getWindowBrowsingContext();
		sessionHistory = windowContext.getSesstionHistory();
		navigationController = windowContext.getNavigationController();
		openedFiles = new HashMap<Integer, File>();
		
		initializeUiComponents();
		registerEventListeners();
		
		updateUi();
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
	
	private void onNavigationCancelled() {
		loadedDocument = null;
		navigationAttempt = null;
	}
	
	private void onNavigationStarted(NavigationAttempt attempt) {
		loadedDocument = null;
		navigationAttempt = attempt;
	}
	
	private void onNavigationCompleted() {
		loadedDocument = windowContext.getActiveDocument();		
		
		String sourceCode = loadedDocument.getParserSource();
		sourceCodeEditorPane.setText(sourceCode);
		
		windowObjectViewer.refresh();
		scriptBrowser.refresh();
		scriptObjectsWatchList.refresh();
	}
	
	private void updateUi() {
		navigateSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		closeSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0);
		saveSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		saveAsSourceCodeButton.setEnabled(sourceCodeTabbedPane.getSelectedIndex() > 0 || loadedDocument != null);
		
		if (navigationAttempt != null) {
			navigateButton.setText("Cancel");
		} else {
			navigateButton.setText("Navigate");
		}
	}
	
	private void initializeUiComponents() {
		scriptBrowser = tester.getScriptBrowser();
		sourceCodeTabbedPane = tester.getSourceCodeTabbedPane();
		navigationField = tester.getNavigationField();
		newWatchedVariableField = tester.getNewWatchedVariableField();
		sourceCodeEditorPane = tester.getSourceCodeEditorPane();
		windowObjectViewer = tester.getWindowObjectViewer();
		scriptObjectsWatchList = tester.getObjectsWatchList();
		
		navigateButton = tester.getNavigateButton();
		navigateSourceCodeButton = tester.getNavigateSourceCodeButton();
		openSourceCodeButton = tester.getOpenSourceCodeButton();
		saveSourceCodeButton = tester.getSaveSourceCodeButton();
		saveAsSourceCodeButton = tester.getSaveAsSourceCodeButton();
		closeSourceCodeButton = tester.getCloseSourceCodeButton();
		newSourceCodeButton = tester.getNewSourceCodeButton();
		
		windowObjectViewer.setBrowsingUnit(browsingUnit);
		scriptBrowser.setBrowsingUnit(browsingUnit);
		scriptObjectsWatchList.setBrowsingUnit(browsingUnit);
	}
	
	private void registerEventListeners() {
		sourceCodeTabbedPane.addChangeListener(onTabChangedListener);
		
		navigateButton.addActionListener(onNavigateListener);
		navigateSourceCodeButton.addActionListener(onNavigateSourceCodeButton);
		openSourceCodeButton.addActionListener(onOpenSourceCodeListener);
		saveSourceCodeButton.addActionListener(onSaveSourceCodeListener);
		saveAsSourceCodeButton.addActionListener(onSaveAsSourceCodeListener);
		closeSourceCodeButton.addActionListener(onCloseSourceCodeListener);
		newSourceCodeButton.addActionListener(onNewSourceCodeListener);
		
		newWatchedVariableField.addActionListener(onNewVariableEntered);
		
		sessionHistory.addListener(sessionHistoryListener);
		navigationController.addListener(navigationControllerListener);
	}
	
	private void saveSourceCodeTab(File file) {
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
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(tester.getWindow(),
				    "Unable to save file with source code.",
				    "File I/O error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void openSourceCodeTab(File file) {
		JEditorPane pane = addSourceCodeTab();
		int paneIndex = sourceCodeTabbedPane.getTabCount() - 1;
		String sourceCode;
		try {
			sourceCode = FileUtils.readFileToString(file);
			String filename = file.getName();
			String paneName = FilenameUtils.removeExtension(filename);
			sourceCodeTabbedPane.setTitleAt(paneIndex, paneName);
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
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		/*String lookAndFeelName = UIManager.getSystemLookAndFeelClassName();
		try {
			UIManager.setLookAndFeel(lookAndFeelName);
		} catch (Exception e) {
		}*/
		
		JavaScriptTesterController controller = new JavaScriptTesterController();
		controller.showWindow();
	}
}

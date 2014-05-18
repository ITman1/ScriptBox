/**
 * JavaScriptTester.java
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

package org.fit.cssbox.scriptbox.demo.browser;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadyState;
import org.fit.cssbox.scriptbox.history.JointSessionHistory;
import org.fit.cssbox.scriptbox.history.JointSessionHistoryEvent;
import org.fit.cssbox.scriptbox.history.JointSessionHistoryListener;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerListener;
import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

/**
 * Browsing unit with simple user interface which contains navigation
 * field and history traversal buttons.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class SimpleBrowserUiController extends BrowserUiController {
	protected BrowserUi ui;
	
	protected ScriptBrowser scriptBrowser;
	protected JFrame frame;
	protected JTextField navigationField;
	protected JButton navigateButton;
	protected JButton historyBackButton;
	protected JButton historyForwardButton;
	
	protected IFrameContainerBrowsingContext windowBrowsingContext;
	
	protected Html5DocumentImpl loadedDocument;
	protected NavigationController navigationController;
	protected NavigationAttempt navigationAttempt;

	protected SessionHistoryEntry currentEntry;
	protected boolean navigationFieldSetByUser;
	protected NavigationControllerEvent.EventType navigationResult;
	
	private WindowListener frameListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			unregisterEventListeners();
			closeUI();
		};
	};
	
	private NavigationControllerListener navigationControllerListener = new NavigationControllerListener() {
		@Override
		public void onNavigationEvent(final NavigationControllerEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					navigationResult = event.getEventType();
					
					switch (navigationResult) {
					case NAVIGATION_MATURED:
						break;
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

					updateUI();
				}
			});
		}
    };
	
	private JointSessionHistoryListener jointSessionHistoryListener = new JointSessionHistoryListener() {
		@Override
		public void onHistoryEvent(final JointSessionHistoryEvent event) {
			JointSessionHistory jointSessionHistory = getJointSessionHistory();
			SessionHistoryEntry _whereTraversed = null;
			int position = jointSessionHistory.getPosition();
			
			if (position == -1) {
				return;
			}
			
			if (event.getEventType() == JointSessionHistoryEvent.EventType.POSITION_CHANGED) {
				_whereTraversed = jointSessionHistory.getCurrentEntry();
			} else if (event.getEventType() == JointSessionHistoryEvent.EventType.TRAVERSED) {
				_whereTraversed = event.getRelatedTarget();
			} else {
				return;
			}
						
			final SessionHistoryEntry whereTraversed = _whereTraversed;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Html5DocumentImpl newDocument = whereTraversed.getDocument();
					BrowsingContext browsingContext = newDocument.getBrowsingContext();
					
					if (newDocument.getDocumentReadiness() == DocumentReadyState.COMPLETE && browsingContext.isTopLevelBrowsingContext() && newDocument.isActiveDocument() && currentEntry != whereTraversed) {
						onDocumentChanged();
					}
					
					if (browsingContext.isTopLevelBrowsingContext()) {
						currentEntry = (whereTraversed != null)? whereTraversed : currentEntry;
						navigationFieldSetByUser = false;
					}
					
					
					updateUI();
				}
			});
			
		}
    };
	
	private DocumentListener onNavigationFieldChangedListener = new DocumentListener() {
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			navigationFieldSetByUser = true;
		}
		
		@Override
		public void insertUpdate(DocumentEvent e) {
			navigationFieldSetByUser = true;
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			navigationFieldSetByUser = true;
		}
	};
	
	private ActionListener onNavigationFieldActionListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			onNavigateListener.actionPerformed(e);
		}
	};
	
	private ActionListener onNavigateListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			String navigationUrl = navigationField.getText();
			
			boolean hasScheme = navigationUrl.matches("\\w+:.*");
			
			if (!hasScheme) {
				navigationUrl = "http://" + navigationUrl;
			}
			
			if (navigationAttempt != null) {
				navigationController.cancelAllNavigationAttempts();
			} else {
				try {
					BrowsingUnit browsingUnit = windowBrowsingContext.getBrowsingUnit();
					browsingUnit.navigate(navigationUrl);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(ui.getWindow(),
						    "Please fix typed URL address.",
						    "Malformed URL",
						    JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
	};
	
	private ActionListener onHistoryBackListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getJointSessionHistory().traverse(-1);
		}
	};
	
	private ActionListener onHistoryForwardListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			getJointSessionHistory().traverse(1);
		}
	};
	
	public SimpleBrowserUiController(IFrameContainerBrowsingContext windowBrowsingContext) {
		this(windowBrowsingContext, new SimpleBrowserUi());
	}	
	
	public SimpleBrowserUiController(IFrameContainerBrowsingContext windowBrowsingContext, BrowserUi ui) {
		this.windowBrowsingContext = windowBrowsingContext;

		this.ui = ui;
		
		scriptBrowser = ui.getScriptBrowser();
		scriptBrowser.setWindowBrowsingContext(windowBrowsingContext);
		
		navigationController = windowBrowsingContext.getNavigationController();

		initializeUiComponents();
		registerEventListeners();
		
		updateUI();
		
		navigationField.setText("http://itman1.github.io/ScriptBox/demo/index.html");
	}
	
	protected void onDocumentChanged() {
		loadedDocument = windowBrowsingContext.getActiveDocument();	
	}
	
	@Override
	public void showUI() {
		try {
			frame.setVisible(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
				    "Unable to run application.",
				    "Internal error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void hideUI() {
		try {
			frame.setVisible(false);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
				    "Unable to run application.",
				    "Internal error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	@Override
	public void updateUI() {
		if (navigationAttempt != null) {
			navigateButton.setText("Cancel");
		} else {
			navigateButton.setText("Navigate");
		}
				
		int historyPosition = getJointSessionHistory().getPosition();
		int historyLength = getJointSessionHistory().getLength();
		
		historyBackButton.setEnabled(historyPosition != - 1 && historyPosition != 0);
		historyForwardButton.setEnabled(historyPosition != historyLength - 1);
		
		if (currentEntry != null && !navigationFieldSetByUser) {
			final String urlString = currentEntry.getURL().toExternalForm();
			navigationField.setText(urlString);
		}
		
		String title = (currentEntry != null)? currentEntry.getTitle() : null;
		frame.setTitle((title == null)? "" : title);
	}
	
	@Override
	public void closeUI() {
		frame.dispose();
		windowBrowsingContext.close();
	}
	
	protected void initializeUiComponents() {
		scriptBrowser = ui.getScriptBrowser();
		navigationField = ui.getNavigationField();	
		frame = ui.getWindow();
		historyBackButton = ui.getHistoryBackButton();
		historyForwardButton = ui.getHistoryForwardButton();
		navigateButton = ui.getNavigateButton();
	}
	
	private void registerEventListeners() {		
		historyBackButton.addActionListener(onHistoryBackListener);
		historyForwardButton.addActionListener(onHistoryForwardListener);
		navigateButton.addActionListener(onNavigateListener);
		frame.addWindowListener(frameListener);
		
		getJointSessionHistory().addListener(jointSessionHistoryListener);
		navigationController.addListener(navigationControllerListener);
		
		navigationField.getDocument().addDocumentListener(onNavigationFieldChangedListener);
		navigationField.addActionListener(onNavigationFieldActionListener);
	}
	
	/**
	 * Unregisters all registered events listeners.
	 */
	protected void unregisterEventListeners() {		
		historyBackButton.removeActionListener(onHistoryBackListener);
		historyForwardButton.removeActionListener(onHistoryForwardListener);
		navigateButton.removeActionListener(onNavigateListener);
		frame.removeWindowListener(frameListener);
		
		getJointSessionHistory().removeListener(jointSessionHistoryListener);
		navigationController.removeListener(navigationControllerListener);
		
		navigationField.getDocument().removeDocumentListener(onNavigationFieldChangedListener);
		navigationField.removeActionListener(onNavigationFieldActionListener);
	}

	protected JointSessionHistory getJointSessionHistory() {
		BrowsingUnit browsingUnit = windowBrowsingContext.getBrowsingUnit();
		JointSessionHistory jointSessionHistory = browsingUnit.getJointSessionHistory();
		
		return jointSessionHistory;
	}
	
	protected void onNavigationCancelled() {
		loadedDocument = null;
		navigationAttempt = null;
	}
	
	protected void onNavigationStarted(NavigationAttempt attempt) {
		loadedDocument = null;
		navigationAttempt = attempt;
	}
	
	protected void onNavigationCompleted() {	
		onDocumentChanged();
	}
	
	@Override
	public BrowserUi getUI() {
		return ui;
	}
}

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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.MalformedURLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jsyntaxpane.DefaultSyntaxKit;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.history.JointSessionHistoryEvent;
import org.fit.cssbox.scriptbox.history.JointSessionHistoryListener;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerEvent;
import org.fit.cssbox.scriptbox.navigation.NavigationControllerListener;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserBrowsingUnit;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserUserAgent;

public class SimpleBrowsingUnit extends ScriptBrowserBrowsingUnit {
	protected JFrame frame;
	protected JTextField navigationField;
	protected JButton navigateButton;
	protected JButton historyBackButton;
	protected JButton historyForwardButton;
	
	protected NavigationController navigationController;
	protected NavigationAttempt navigationAttempt;

	protected SessionHistoryEntry currentEntry;
	protected boolean navigationFieldSetByUser;

	private WindowListener frameListener = new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
			frame.dispose();
			_userAgent.destroyBrowsingUnit(SimpleBrowsingUnit.this);
		};
	};
	
	private NavigationControllerListener navigationControllerListener = new NavigationControllerListener() {
		@Override
		public void onNavigationEvent(final NavigationControllerEvent event) {
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					switch (event.getEventType()) {
						case NAVIGATION_MATURED:
							break;
						case DESTROYED:
						case NAVIGATION_CANCELLED:
							navigationAttempt = null;
							break;
						case NAVIGATION_COMPLETED:
							navigationAttempt = null;
							updateScriptBox();
							break;
						case NAVIGATION_NEW:
							NavigationAttempt attempt = event.getNavigationAttempt();
							navigationAttempt = attempt;
							break;
					}

					updateUi();
				}
			});
		}
    };
	
	private JointSessionHistoryListener jointSessionHistoryListener = new JointSessionHistoryListener() {
		@Override
		public void onHistoryEvent(final JointSessionHistoryEvent event) {
			SessionHistoryEntry _whereTraversed = null;
			if (event.getEventType() == JointSessionHistoryEvent.EventType.POSITION_CHANGED) {
				_whereTraversed = _jointSessionHistory.getCurrentEntry();
			} else if (event.getEventType() == JointSessionHistoryEvent.EventType.TRAVERSED) {
				_whereTraversed = event.getRelatedTarget();
			} else {
				return;
			}
						
			if (_whereTraversed == null) {
				return;
			}
			
			final SessionHistoryEntry whereTraversed = _whereTraversed;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					Html5DocumentImpl newDocument = whereTraversed.getDocument();
					BrowsingContext browsingContext = newDocument.getBrowsingContext();
					
					if (newDocument.getDocumentReadiness() == DocumentReadiness.COMPLETE && browsingContext.isTopLevelBrowsingContext() && newDocument.isActiveDocument() && currentEntry != whereTraversed) {
						updateScriptBox();
					}
					
					if (browsingContext.isTopLevelBrowsingContext()) {
						currentEntry = whereTraversed;
						navigationFieldSetByUser = false;
					}
					
					
					updateUi();
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
					navigate(navigationUrl);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(frame,
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
			_jointSessionHistory.traverse(-1);
		}
	};
	
	private ActionListener onHistoryForwardListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			_jointSessionHistory.traverse(1);
		}
	};
	
	public SimpleBrowsingUnit(ScriptBrowserUserAgent userAgent) {
		super(userAgent);
		
		initializeComponents();

		navigationController = _windowBrowsingContext.getNavigationController();
		
		registerEventListeners();
		
		updateUi();
		
		showWindow();
	}

	private void initializeComponents() {
		frame = new JFrame();
		frame.setMinimumSize(new Dimension(700, 500));
		frame.setBounds(100, 100, 1024, 780);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

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
		
		containerPanel.add(scriptBrowser, BorderLayout.CENTER);
	}
	
	public void showWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame,
						    "Unable to run application.",
						    "Internal error",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}
	
	private void updateScriptBox() {
		scriptBrowser.refresh();
	}
	
	private void updateUi() {
		if (navigationAttempt != null) {
			navigateButton.setText("Cancel");
		} else {
			navigateButton.setText("Navigate");
		}
		
		int historyPosition = _jointSessionHistory.getPosition();
		int historyLength = _jointSessionHistory.getLength();
		
		historyBackButton.setEnabled(historyPosition != - 1 && historyPosition != 0);
		historyForwardButton.setEnabled(historyPosition != historyLength - 1);
		
		if (currentEntry != null && !navigationFieldSetByUser) {
			final String urlString = currentEntry.getURL().toExternalForm();
			navigationField.setText(urlString);
		}
	}
	
	private void registerEventListeners() {		
		historyBackButton.addActionListener(onHistoryBackListener);
		historyForwardButton.addActionListener(onHistoryForwardListener);
		navigateButton.addActionListener(onNavigateListener);
		frame.addWindowListener(frameListener);
		
		_jointSessionHistory.addListener(jointSessionHistoryListener);
		navigationController.addListener(navigationControllerListener);
		
		navigationField.getDocument().addDocumentListener(onNavigationFieldChangedListener);
		navigationField.addActionListener(onNavigationFieldActionListener);
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
}

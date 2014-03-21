package org.fit.cssbox.scriptbox.demo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

public class JavaScriptTesterController {
	private JavaScriptTester tester;
	private UserAgent userAgent;
	private BrowsingUnit browsingUnit;
	
	private ActionListener onNavigateListener = new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JTextField field = tester.getNavigationField();
			String navigationUrl = field.getText();
			navigatePage(navigationUrl);
		}
	};
	
	public JavaScriptTesterController() {
		userAgent = new UserAgent();
		browsingUnit = userAgent.openBrowsingUnit();
		tester = new JavaScriptTester();
		registerEventListeners();
	}
	
	public void showWindow() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JFrame window = tester.getWindow();
					window.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public synchronized void navigatePage(String page) {
		browsingUnit.navigate(page);
		
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		ScriptBrowser browser = tester.getScriptBrowser();
		browser.setBrowsingUnit(browsingUnit);
		browser.refresh();
	}
	
	private void registerEventListeners() {
		JButton button = tester.getNavigateButton();
		button.addActionListener(onNavigateListener);
	}

	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		JavaScriptTesterController controller = new JavaScriptTesterController();
		controller.showWindow();
	}
}

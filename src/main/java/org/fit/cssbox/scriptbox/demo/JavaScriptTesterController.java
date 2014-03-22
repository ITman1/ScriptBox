package org.fit.cssbox.scriptbox.demo;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.ui.ScriptBrowser;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

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
		BrowsingContext context = browsingUnit.getWindowBrowsingContext();
		browser.setBrowsingUnit(browsingUnit);
		
		browser.refresh();		

		Html5DocumentImpl doc = context.getActiveDocument();
		DOMImplementationRegistry registry;
		try {
			registry = DOMImplementationRegistry.newInstance();
			DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
			LSSerializer serializer = impl.createLSSerializer();
	        LSOutput output = impl.createLSOutput();
	        output.setEncoding("UTF-8");
	        output.setByteStream(System.out);
	        serializer.write(doc, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    

		
		JTextArea sourceCodeArea = tester.getSourceCodeArea();
		String sourceCode = doc.getParserSource();
		sourceCodeArea.setText(sourceCode);
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

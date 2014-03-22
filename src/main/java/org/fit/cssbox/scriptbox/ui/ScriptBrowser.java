package org.fit.cssbox.scriptbox.ui;

import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.util.CSSBoxAnalyzer;

public class ScriptBrowser extends BrowserPane {
	private static final long serialVersionUID = -7720839593115478393L;

	protected BrowsingUnit browsingUnit;
	protected CSSBoxAnalyzer analyzer;
	protected MouseEventsDispatcher mouseDispatcher = new MouseEventsDispatcher();

	public void setBrowsingUnit(BrowsingUnit browsingUnit) {
		this.browsingUnit = browsingUnit;
		this.analyzer = new ScriptAnalyzer(
				browsingUnit.getWindowBrowsingContext());

		setCSSBoxAnalyzer(this.analyzer);
		addMouseListener(mouseDispatcher);
	}

	public void refresh() {
		browsingUnit.queueTask(new Task(TaskSource.DOM_MANIPULATION, browsingUnit.getWindowBrowsingContext()) {
			
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				EditorKit kit = getEditorKit();

				if (kit == null) {
					return;

				}

				URL address = browsingUnit.getWindowBrowsingContext()
						.getActiveDocument().getAddress();

				Document document = kit.createDefaultDocument();
				InputStream in = null;

				try {
					kit.read(in, document, 0);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// set the document to the component
				setDocument(document);

				final String reference = address.getRef();
				// Have to scroll after painted.
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						Rectangle top = new Rectangle(0, 0, 1, 1); // top of pane
						Rectangle bottom = new Rectangle(0, getHeight() - 1, 1, 1);
						if (reference != null) {
							// scroll down and back to reference to get reference
							// the topmost item
							scrollRectToVisible(bottom);
							scrollToReference(reference);
						} else {
							// scroll to the top of the new page
							scrollRectToVisible(top);
						}
					}
				});

				/*
				 * SwingUtilities.invokeLater(new Runnable() {
				 * 
				 * @Override public void run() { firePropertyChange("page", oldPage,
				 * newPage); } });
				 */
			}
		});
		
		

	}
}

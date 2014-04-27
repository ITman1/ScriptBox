/**
 * ScriptBrowser.java
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

package org.fit.cssbox.scriptbox.ui;

import java.awt.Rectangle;
import java.io.InputStream;
import java.net.URL;

import javax.swing.SwingUtilities;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.util.CSSBoxAnalyzer;

public class ScriptBrowser extends BrowserPane {
	private static final long serialVersionUID = -7720839593115478393L;

	protected CSSBoxAnalyzer analyzer;
	protected MouseEventsDispatcher mouseDispatcher = new MouseEventsDispatcher();
	protected ScriptBrowserHyperlinkHandler hyperlinkHandler;
	
	protected UserAgent userAgent;
	protected BrowsingUnit browsingUnit;
	
	protected SessionHistoryEntry visibleSessionHistoryEntry;
	protected Rectangle delayedScrollRect;
	
	public ScriptBrowser() {}
	
	/**
	 * User agent that will be used for opening browsing unit which will be used by this browsing component.
	 * 
	 * @param userAgent User agent.
	 */
	public ScriptBrowser(UserAgent userAgent) {
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		setBrowsingUnit(browsingUnit);
	}
	
	/**
	 * Constructs new browser with an associated browsing unit.
	 * 
	 * @param browsingUnit Browsing unit to be associated with this browser.
	 */
	public ScriptBrowser(BrowsingUnit browsingUnit) {
		setBrowsingUnit(browsingUnit);
	}

	/**
	 * Sets new browsing unit that will be used for retrieving the Document.
	 * 
	 * @param browsingUnit Browsing unit to be associated with this browser.
	 */
	public void setBrowsingUnit(BrowsingUnit browsingUnit) {
		this.browsingUnit = browsingUnit;
		this.userAgent = browsingUnit.getUserAgent();
		
		initialize();
	}
	
	/**
	 * Initializes this component.
	 */
	protected void initialize() {
		this.analyzer = new ScriptAnalyzer();

		if (hyperlinkHandler != null) {
			removeHyperlinkListener(hyperlinkHandler);
		}
		hyperlinkHandler = new ScriptBrowserHyperlinkHandler();
		
		addHyperlinkListener(hyperlinkHandler);
		setCSSBoxAnalyzer(this.analyzer);
		addMouseListener(mouseDispatcher);
		addMouseMotionListener(mouseDispatcher);
	}
	
	/**
	 * Returns associated user agent.
	 * 
	 * @return Associated user agent.
	 */
	public UserAgent getUserAgent() {
		return userAgent;
	}
	
	/**
	 * Returns associated browsing unit.
	 * 
	 * @return Associated browsing unit.
	 */
	public BrowsingUnit getBrowsingUnit() {
		return browsingUnit;
	}
	
	/**
	 * Refreshes the canvas. This should be used when user agent finished the parsing.
	 */
	public void refresh() {
		browsingUnit.queueTask(new Task(TaskSource.DOM_MANIPULATION, browsingUnit.getWindowBrowsingContext()) {
			
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				/* Create and prepare document */
				EditorKit kit = getEditorKit();

				if (kit == null) {
					return;
				}

				Document document = kit.createDefaultDocument();
				
				/* Get information about content to render */
				
				BrowsingContext context = browsingUnit.getWindowBrowsingContext();
				Html5DocumentImpl activeDocument = context.getActiveDocument();
				URL documentAddress = activeDocument.getAddress();

				String contentType = activeDocument.getContentType();
				
				/* Set document properties */
				setContentType(contentType);
				document.putProperty(Document.StreamDescriptionProperty, documentAddress);

				/* Render document */
				InputStream in = new DocumentInputStream(activeDocument);
				try {
					kit.read(in, document, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

				/* Set the document to the component */
				setDocument(document);

				synchronized (ScriptBrowser.this) {
					SessionHistory sessionHistory = context.getSesstionHistory();
					visibleSessionHistoryEntry = sessionHistory.getCurrentEntry();
					
					if (delayedScrollRect != null) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								scrollRectToVisible(delayedScrollRect);
							}
						});
					}
				}
				
				/* TODO?:
				 * SwingUtilities.invokeLater(new Runnable() {
				 * 
				 * @Override public void run() { firePropertyChange("page", oldPage,
				 * newPage); } });
				 */
			}
		});
	}
	
	@Override
	public synchronized void scrollRectToVisible(Rectangle aRect) {
		BrowsingContext context = browsingUnit.getWindowBrowsingContext();
		SessionHistory sessionHistory = context.getSesstionHistory();
		SessionHistoryEntry currentEntry = sessionHistory.getCurrentEntry();
		
		aRect = new Rectangle(aRect);
		aRect.setSize(1, 1);
		
		if (visibleSessionHistoryEntry != currentEntry) {
			delayedScrollRect = aRect;
		} else {
			Rectangle bottom = new Rectangle(0, getHeight() - 1, 1, 1);
			super.scrollRectToVisible(bottom);
			super.scrollRectToVisible(aRect);
			
			delayedScrollRect = null;
		}
	}
}

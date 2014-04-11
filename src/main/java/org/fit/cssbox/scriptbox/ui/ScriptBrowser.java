package org.fit.cssbox.scriptbox.ui;

import java.io.InputStream;
import java.net.URL;

import javax.swing.text.Document;
import javax.swing.text.EditorKit;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.swingbox.BrowserPane;
import org.fit.cssbox.swingbox.util.CSSBoxAnalyzer;

public class ScriptBrowser extends BrowserPane {
	private static final long serialVersionUID = -7720839593115478393L;

	protected CSSBoxAnalyzer analyzer;
	protected MouseEventsDispatcher mouseDispatcher = new MouseEventsDispatcher();
	protected ScriptBrowserHyperlinkHandler hyperlinkHandler;
	
	protected ScriptBrowserUserAgent userAgent;
	protected BrowsingUnit browsingUnit;

	public ScriptBrowser() {
		this.userAgent = new ScriptBrowserUserAgent(this);
		browsingUnit = userAgent.openBrowsingUnit();
		
		this.analyzer = new ScriptAnalyzer(
				browsingUnit.getWindowBrowsingContext());

		if (hyperlinkHandler != null) {
			removeHyperlinkListener(hyperlinkHandler);
		}
		hyperlinkHandler = new ScriptBrowserHyperlinkHandler(browsingUnit);
		
		addHyperlinkListener(hyperlinkHandler);
		setCSSBoxAnalyzer(this.analyzer);
		addMouseListener(mouseDispatcher);
		addMouseMotionListener(mouseDispatcher);
	}
	
	public ScriptBrowserUserAgent getUserAgent() {
		return userAgent;
	}
	
	public BrowsingUnit getBrowsingUnit() {
		return browsingUnit;
	}

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
				InputStream in = null;
				
				/* Get information about content to render */
				
				BrowsingContext context = browsingUnit.getWindowBrowsingContext();
				Html5DocumentImpl activeDocument = context.getActiveDocument();
				URL documentAddress = activeDocument.getAddress();
				String contentType = activeDocument.getContentType();
				
				/* Set document properties */
		        setContentType(contentType);
		        document.putProperty(Document.StreamDescriptionProperty, documentAddress);

		        /* Render document */
				
				try {
					kit.read(in, document, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}

				/* Set the document to the component */
				setDocument(document);

				/* TODO?:
				 * SwingUtilities.invokeLater(new Runnable() {
				 * 
				 * @Override public void run() { firePropertyChange("page", oldPage,
				 * newPage); } });
				 */
			}
		});
	}
	
	// TODO: Remove!!!!!
    public boolean scrollToReferenceWithBoolean(String reference) {
    	scrollToReference(reference);
        return true;
    }
}

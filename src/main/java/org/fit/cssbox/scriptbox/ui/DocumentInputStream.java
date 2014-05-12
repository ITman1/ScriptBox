package org.fit.cssbox.scriptbox.ui;

import java.io.IOException;
import java.io.InputStream;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

/**
 * Empty input stream that is used for propagating the already parsed 
 * Document into {@link ScriptAnalyzer}. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DocumentInputStream extends InputStream {

	protected Html5DocumentImpl document;
	
	public DocumentInputStream(Html5DocumentImpl document) {
		this.document = document;
	}
	
	/**
	 * Returns associated document.
	 * 
	 * @return Associated document.
	 */
	public Html5DocumentImpl getDocument() {
		return document;
	}
	
	@Override
	public int read() throws IOException {
		return -1;
	}

}

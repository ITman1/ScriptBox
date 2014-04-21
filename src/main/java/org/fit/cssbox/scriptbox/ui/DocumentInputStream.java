package org.fit.cssbox.scriptbox.ui;

import java.io.IOException;
import java.io.InputStream;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public class DocumentInputStream extends InputStream {

	protected Html5DocumentImpl document;
	
	public DocumentInputStream(Html5DocumentImpl document) {
		this.document = document;
	}
	
	public Html5DocumentImpl getDocument() {
		return document;
	}
	
	@Override
	public int read() throws IOException {
		return 0;
	}

}

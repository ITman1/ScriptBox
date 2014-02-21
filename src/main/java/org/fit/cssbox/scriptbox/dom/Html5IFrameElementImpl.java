package org.fit.cssbox.scriptbox.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLIFrameElementImpl;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5IFrameElement;

public class Html5IFrameElementImpl extends HTMLIFrameElementImpl implements Html5IFrameElement {

	private static final long serialVersionUID = -2859222900868725576L;

	public Html5IFrameElementImpl(HTMLDocumentImpl owner, String name) {
		super(owner, name);
	}

	@Override
	public boolean getSeamless() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSeamless(boolean seamless) {
		// TODO Auto-generated method stub
	}
	
	public boolean isInSeamlessMode() {
		//TODO: Implement according to http://www.w3.org/html/wg/drafts/html/CR/embedded-content-0.html#in-seamless-mode
		return false;
	}
	
}

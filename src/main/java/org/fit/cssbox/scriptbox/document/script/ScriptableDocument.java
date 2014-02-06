package org.fit.cssbox.scriptbox.document.script;

import java.util.Locale;

import org.apache.html.dom.HTMLDocumentImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ScriptableDocument extends HTMLDocumentImpl {

	private static final long serialVersionUID = 1L;
	private static String SCRIPT_TAG_NAME = "script";
	
	private BrowsingContext _browsingContext;
	
	ScriptableDocument(BrowsingContext browsingContext) {
		_browsingContext = browsingContext;
	}
	
	@Override
	public Element createElement( String tagName ) throws DOMException {
		tagName = tagName.toLowerCase(Locale.ENGLISH);
		
		if ( tagName.equals(SCRIPT_TAG_NAME)) {
			ScriptElement scriptElement = new ScriptElement(this, tagName);
			return scriptElement;
		}
		
		return super.createElement(tagName);
	}
	
	public BrowsingContext getBrowsingContext() {
		return _browsingContext;
	}
	
	/*
	 * A Document is said to be fully active when it is the active document of its 
	 * browsing context, and either its browsing context is a top-level browsing 
	 * context, or it has a parent browsing context and the Document through 
	 * which it is nested is itself fully active.
	 */
	public boolean isFullyActive() {
		boolean fullyActive = true;
		BrowsingContext parentContext = _browsingContext.getCreatorContext();
		
		fullyActive = fullyActive && _browsingContext.getActiveDocument() == this;
		fullyActive = fullyActive && _browsingContext.isTopLevelBrowsingContext();
		
		fullyActive = fullyActive || (parentContext != null && parentContext.getActiveDocument().isFullyActive());
		
		return fullyActive;
	}
}

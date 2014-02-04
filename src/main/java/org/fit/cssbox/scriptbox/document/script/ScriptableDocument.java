package org.fit.cssbox.scriptbox.document.script;

import java.util.Locale;

import org.apache.html.dom.HTMLDocumentImpl;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class ScriptableDocument extends HTMLDocumentImpl {

	private boolean primaryDocumentParser;
	private static final long serialVersionUID = 1L;
	private static String SCRIPT_TAG_NAME = "script";
	
	@Override
	public Element createElement( String tagName ) throws DOMException {
		tagName = tagName.toLowerCase(Locale.ENGLISH);
		
		if ( tagName.equals(SCRIPT_TAG_NAME)) {
			ContextScriptElement scriptElement = new ContextScriptElement(this, tagName);
			scriptElement.setParserInserted(!primaryDocumentParser);
			return scriptElement;
		}
		
		return super.createElement(tagName);
	}
	
	public void setParserOrigin(boolean primaryDocumentParser) {
		this.primaryDocumentParser = primaryDocumentParser;
	}
	
	public boolean getParserOrigin() {
		return primaryDocumentParser;
	}
}

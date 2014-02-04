package org.fit.cssbox.scriptbox.document.script;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLScriptElementImpl;
import org.w3c.dom.Document;

public class ContextScriptElement extends HTMLScriptElementImpl {
	private static String ASYNC_ATTR_NAME = "async";
	
	private static final long serialVersionUID = 1L;
	
	private boolean alreadyStarted;
	private boolean parserInserted;
	private boolean wasParserInserted;
	private boolean forceAsync;
	private boolean readyToBeParserExecuted;
	
	private Document creatorDocument;
	private String scriptMimeType;

	public ContextScriptElement(HTMLDocumentImpl document, String name) {
		super(document, name);
		
		alreadyStarted = false;
		parserInserted = false;
		setWasParserInserted(false);
		forceAsync = true;
		readyToBeParserExecuted = false;
		
		creatorDocument = document;
	}

	public String getAsync() {
		return getAttribute(ASYNC_ATTR_NAME);
	}
	
	public void setAsync(String value) {
		setAttribute(ASYNC_ATTR_NAME, value);
	}
	
	public boolean isAlreadyStarted() {
		return alreadyStarted;
	}

	public void setAlreadyStarted(boolean alreadyStarted) {
		this.alreadyStarted = alreadyStarted;
	}

	public boolean isParserInserted() {
		return parserInserted;
	}

	public void setParserInserted(boolean parserInserted) {
		this.parserInserted = parserInserted;
	}

	public boolean isForceAsync() {
		return forceAsync;
	}

	public void setForceAsync(boolean forceAsync) {
		this.forceAsync = forceAsync;
	}

	public boolean isReadyToBeParserExecuted() {
		return readyToBeParserExecuted;
	}

	public void setReadyToBeParserExecuted(boolean readyToBeParserExecuted) {
		this.readyToBeParserExecuted = readyToBeParserExecuted;
	}

	public boolean isWasParserInserted() {
		return wasParserInserted;
	}

	public void setWasParserInserted(boolean wasParserInserted) {
		this.wasParserInserted = wasParserInserted;
	}

	public String getScriptMimeType() {
		return scriptMimeType;
	}

	public void setScriptMimeType(String scriptMimeType) {
		this.scriptMimeType = scriptMimeType;
	}

	public Document getCreatorDocument() {
		return creatorDocument;
	}
}

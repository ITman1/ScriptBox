package org.fit.cssbox.scriptbox.dom;

import java.io.Reader;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5ScriptElement;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngineManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Html5ScriptElementImpl extends HTMLScriptElementImpl implements Html5ScriptElement {
	private static final long serialVersionUID = 4725269642619675257L;
	private static final String ASYNC_ATTR_NAME = "async";
	private static final String DEFER_ATTR_NAME = "defer";
	private static final String DEFAULT_SCRIPT_MIME_TYPE = "text/javascript";
	
	private boolean _alreadyStarted;
	private boolean _parserInserted;
	private boolean _wasParserInserted;
	private boolean _forceAsync;
	private boolean _readyToBeParserExecuted;
	
	private Document _creatorDocument;
	private String _scriptMimeType;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	public Html5ScriptElementImpl(HTMLDocumentImpl document, String name) {
		super(document, name);
		
		_alreadyStarted = false;
		_parserInserted = false;
		_wasParserInserted = false;
		_forceAsync = true;
		_readyToBeParserExecuted = false;
		
		_creatorDocument = document;
	}

	@Override
	public boolean getAsync() {
		return getAttribute(ASYNC_ATTR_NAME) != null;
	}
	
	@Override
	public void setAsync(boolean value) {
		if (value) {
			setAttribute(ASYNC_ATTR_NAME, "");
		} else {
			removeAttribute(ASYNC_ATTR_NAME);
		}
	}
	
	@Override
	public boolean getDefer() {
		return getAttribute(DEFER_ATTR_NAME) != null;
	}
	
	@Override
	public void setDefer(boolean value) {
		if (value) {
			setAttribute(DEFER_ATTR_NAME, "");
		} else {
			removeAttribute(DEFER_ATTR_NAME);
		}
	}
	
	public boolean prepareScript() {		
		/* Step 1*/
		if (_alreadyStarted) {
			return false;
		}
		
		/* Step 2 */
		if (_parserInserted) {
			_wasParserInserted = true;
			_parserInserted = false;
		}
		
		/* Step 3 */
		if (_wasParserInserted && !getAsync()) {
			_forceAsync = true;
		}
		
		/* Step 4 */
		if (getSrc() == null) {
			boolean isEmpty = true;
			NodeList childNodes = getChildNodes();
			for(int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				
				if (node.getNodeType() == Node.TEXT_NODE) {
					isEmpty = isEmpty && node.getNodeValue().isEmpty();
				} else if (node.getNodeType() != Node.COMMENT_NODE) {
					isEmpty = false;
				}
			}

			if (isEmpty) {
				return false;
			}
		}
		
		/* Step 5 */
		// TODO: Re-check the interpretation of the HTML5 specification
		/*if (!documentContexts.containsKey((scriptElement.getOwnerDocument()))) {
			return false;
		}*/
		
		/* Step 6 */
		if ((getType() != null && getType().isEmpty())
				|| (getType() == null && getLang() != null && getLang().isEmpty())
				|| (getType() == null && getLang() == null)) {
			_scriptMimeType = DEFAULT_SCRIPT_MIME_TYPE;
		} else if (getType() != null) {
			_scriptMimeType = getType().trim();
		} else if (getLang() != null && !getLang().isEmpty()) {
			_scriptMimeType = "text/" + getLang().trim();
		}
		
		/* Step 7 */
		if (!documentScriptEngineManager.isSupported(_scriptMimeType)) {
			return false;
		}
		
		/* Step 8 */
		if (_wasParserInserted) {
			_parserInserted = true;
			_forceAsync = false;
		}
		
		/* Step 9 */
		_alreadyStarted = true;
		
		/* Step 10 */
		if (_parserInserted && _creatorDocument != getOwnerDocument()) {
			return false;
		}
		
		/* Step 11 */
		// TODO: Script disabled feature
			
		/* Step 12 */
		// TODO: Script for and event features
			
		/* Step 13 */
		// TODO: Script encoding feature	
		
		return true;
	}
	
	public boolean executeScript() {
		return true;
	}
	
	public void cancelExecution() {
		_alreadyStarted = true;
	}
	
	public void suspendExecution() {
		_parserInserted = true;
		_forceAsync = false;
	}
	
	public boolean isReadyToBeParserExecuted() {
		return _readyToBeParserExecuted;
	}

}

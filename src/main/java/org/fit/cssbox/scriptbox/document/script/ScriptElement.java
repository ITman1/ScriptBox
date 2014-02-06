package org.fit.cssbox.scriptbox.document.script;

import java.io.Reader;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLScriptElementImpl;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngineManager;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ScriptElement extends HTMLScriptElementImpl {
	private static String ASYNC_ATTR_NAME = "async";
	private static String DEFAULT_SCRIPT_MIME_TYPE = "text/javascript";
	
	private static final long serialVersionUID = 1L;
	
	private boolean _alreadyStarted;
	private boolean _parserInserted;
	private boolean _wasParserInserted;
	private boolean _forceAsync;
	private boolean _readyToBeParserExecuted;
	
	private Document _creatorDocument;
	private String _scriptMimeType;
	private DocumentScriptEngineManager documentScriptEngineManager;
	
	public ScriptElement(HTMLDocumentImpl document, String name) {
		super(document, name);
		
		_alreadyStarted = false;
		_parserInserted = false;
		_wasParserInserted = false;
		_forceAsync = true;
		_readyToBeParserExecuted = false;
		
		_creatorDocument = document;
	}

	public String getAsync() {
		return getAttribute(ASYNC_ATTR_NAME);
	}
	
	public void setAsync(String value) {
		setAttribute(ASYNC_ATTR_NAME, value);
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
		if (_wasParserInserted && getAsync() == null) {
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

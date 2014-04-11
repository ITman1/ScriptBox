package org.fit.cssbox.scriptbox.dom;

import java.io.Reader;
import java.io.StringReader;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLScriptElementImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScript;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocumentParser;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5ScriptElement;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineManager;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Html5ScriptElementImpl extends HTMLScriptElementImpl implements Html5ScriptElement {
	private static final long serialVersionUID = 4725269642619675257L;
	private static final String ASYNC_ATTR_NAME = "async";
	private static final String DEFER_ATTR_NAME = "defer";
	private static final String DEFAULT_SCRIPT_MIME_TYPE = JavaScriptEngine.JAVASCRIPT_LANGUAGE;
	
	private boolean _alreadyStarted;
	private boolean _parserInserted;
	private boolean _wasParserInserted;
	private boolean _forceAsync;
	private boolean _readyToBeParserExecuted;
	
	private Document _creatorDocument;
	private ScriptableDocumentParser _creatorParser;
	private BrowserScriptEngineManager _browserScriptEngineManager;
	private Fetch scriptFetch;
	
	public Html5ScriptElementImpl(HTMLDocumentImpl document, String name) {
		super(document, name);
		
		_browserScriptEngineManager = BrowserScriptEngineManager.getInstance();
		_alreadyStarted = false;
		_parserInserted = false;
		_wasParserInserted = false;
		_forceAsync = true;
		_readyToBeParserExecuted = false;
		
		_creatorDocument = document;
		_creatorParser = (document instanceof Html5DocumentImpl)? ((Html5DocumentImpl)document).getParser() : null;
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
			boolean isEmpty = !hasExecutableScript();

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
		String scriptMimeType = getMimeType();
		
		/* Step 7 */
		if (!_browserScriptEngineManager.isSupported(scriptMimeType)) {
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
		if (_parserInserted && (_creatorDocument != getOwnerDocument() || _creatorParser == null)) {
			return false;
		}
		
		/* Step 11 */
		if (isScriptingDisabled()) {
			return false;
		}
			
		/* Step 12 */
		// TODO: Script for and event features
			
		/* Step 13 */
		// TODO: Script encoding feature	
		
		/* Step 14 */
		// TODO: Element contains src attribute
		
		/* Step 15 */
		boolean hasSrc = getSrc() != null && !getSrc().isEmpty();
		if (hasSrc && getDefer() && _parserInserted && !getAsync()) {
			// TODO: Add to the end of the list of scripts that will execute when the document has finished parsing 
			// once the fetching algorithm has completed must set the element's "ready to be parser-executed" flag
		} else if (hasSrc && _parserInserted && !getAsync()) {
			// TODO: element is the pending parsing-blocking script of the Document of the parser that created the element
			// once the fetching algorithm has completed must set the element's "ready to be parser-executed" flag
		} if (!hasSrc && _parserInserted && _creatorParser.scriptNestingLevel() < 2 && _creatorParser.hasStyleSheetBlockScripts()) {
			// TODO: Not complete - add pending processing script
			_readyToBeParserExecuted = true;
		} else if (hasSrc && !getAsync() && !_forceAsync) {
			// TODO: Implement
		} else if (hasSrc) {
			// TODO: Implement
		} else {
			executeScript();
		}
			
		return true;
	}
	
	public boolean hasExecutableScript() {
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
		
		return !isEmpty;
	}
	
	public String getMimeType() {
		if ((getType() != null && getType().isEmpty())
				|| (getType() == null && getLang() != null && getLang().isEmpty())
				|| (getType() == null && getLang() == null)) {
			return DEFAULT_SCRIPT_MIME_TYPE;
		} else if (getType() != null) {
			return getType().trim();
		} else if (getLang() != null && !getLang().isEmpty()) {
			return "text/" + getLang().trim();
		}
		return "";
	}
	
	public boolean executeScript() {
		if (_parserInserted && (_creatorParser == null || _creatorParser.getDocument() != getOwnerDocument())) {
			return false;
		}
		
		if (scriptFetch == null || scriptFetch.isValid()) {
			/* TODO: Implement http://www.w3.org/html/wg/drafts/html/CR/scripting-1.html#execute-the-script-block */
			// Now it is simplified
			Document document = getOwnerDocument();
			
			if (document instanceof Html5DocumentImpl) {
				Html5DocumentImpl documentImpl = (Html5DocumentImpl)document;
				Window window = documentImpl.getWindow();
				Reader source = getExecutableScript(); 
				WindowScriptSettings settings = window.getScriptSettings();
				String language = getMimeType();
				new WindowScript(source, null, language, settings, false);
			} else {
				return false;
			}
		}
		
		return false;
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
	
	public Reader getExecutableScript() {
		NodeList childNodes = getChildNodes();
		StringBuilder executableScript = new StringBuilder();
		
		for(int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			String nodeValue = node.getNodeValue();
			
			if (node.getNodeType() == Node.TEXT_NODE && nodeValue != null && !nodeValue.isEmpty()) {
				executableScript.append(nodeValue);
			}
		}
		
		return new StringReader(executableScript.toString());
	}
	
	protected boolean isScriptingDisabled() {
		Document document = getOwnerDocument();
		
		if (document instanceof Html5DocumentImpl) {
			Html5DocumentImpl documentImpl = (Html5DocumentImpl)document;
			BrowsingContext context = documentImpl.getBrowsingContext();
			
			return context == null || !context.scriptingEnabled();
		}
		
		return true;
	}
}

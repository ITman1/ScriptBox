/**
 * Html5ScriptElementImpl.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.dom;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLScriptElementImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocumentParser;
import org.fit.cssbox.scriptbox.dom.events.GlobalEventHandlers;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5ScriptElement;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.fetch.Fetch;
import org.fit.cssbox.scriptbox.resource.fetch.FetchRegistry;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineManager;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.window.Window;
import org.fit.cssbox.scriptbox.window.EvalWindowScript;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements script element according to HTML5.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#the-script-element">HTML script element</a>
 */
public class Html5ScriptElementImpl extends HTMLScriptElementImpl implements Html5ScriptElement {
	
	/*
	 * Task which marks scripts as ready to be parser executed.
	 */
	private class ParserInsertedScriptFetchCompletedTask extends Task {

		public ParserInsertedScriptFetchCompletedTask() {
			super(TaskSource.NETWORKING, _creatorDocument);
		}

		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			makeReadyToBeParserExecuted();
		}
		
	}	
	
	/*
	 * Task which tries to execute scripts in order as they were added into the list.
	 */
	private class InOrderASAPScriptFetchCompletedTask extends Task {

		public InOrderASAPScriptFetchCompletedTask() {
			super(TaskSource.NETWORKING, _creatorDocument);
		}

		/*
		 * See: http://www.w3.org/html/wg/drafts/html/master/scripting-1.html#script-processing-src-sync
		 */
		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			_readyToBeASAPExecuted = true;
			
			List<Html5ScriptElementImpl> scripts = _creatorParser.getInOrderASAPScripts();
			Html5ScriptElementImpl firstScript = (!scripts.isEmpty())? scripts.get(0) : null;
			
			if (firstScript != null && firstScript._readyToBeASAPExecuted) {
				execution(firstScript);
			}
		}
		
		protected void execution(Html5ScriptElementImpl script) {
			script.executeScript();
			_creatorParser.removeInOrderASAPScript(script);
			
			List<Html5ScriptElementImpl> scripts = _creatorParser.getInOrderASAPScripts();
			
			if (!scripts.isEmpty()) {
				Html5ScriptElementImpl firstScript = scripts.get(0);
				if (firstScript._readyToBeASAPExecuted) {
					execution(firstScript);
				}
			}
		}
	}	

	/*
	 * Task which executes scripts as soon as they are fetched.
	 */
	private class ASAPScriptFetchCompletedTask extends Task {

		public ASAPScriptFetchCompletedTask() {
			super(TaskSource.NETWORKING, _creatorDocument);
		}

		/*
		 * See: http://www.w3.org/html/wg/drafts/html/master/scripting-1.html#script-processing-src
		 */
		@Override
		public void execute() throws TaskAbortedException, InterruptedException {
			Html5ScriptElementImpl.this.executeScript();
			_creatorParser.removeASAPScript(Html5ScriptElementImpl.this);
		}
	}	

	
	private static final long serialVersionUID = 4725269642619675257L;
	private static final String ASYNC_ATTR_NAME = "async";
	private static final String DEFER_ATTR_NAME = "defer";
	private static final String DEFAULT_SCRIPT_MIME_TYPE = WindowJavaScriptEngine.JAVASCRIPT_LANGUAGE;
	
	private boolean _alreadyStarted;
	private boolean _parserInserted;
	private boolean _wasParserInserted;
	private boolean _forceAsync;
	private boolean _readyToBeParserExecuted;
	private boolean _readyToBeASAPExecuted;
	
	private Html5DocumentImpl _creatorDocument;
	private ScriptableDocumentParser _creatorParser;
	private BrowserScriptEngineManager _browserScriptEngineManager;
	private Fetch scriptFetch;
	private FetchRegistry _fetchRegistry;
	
	/**
	 * Constructs new script element.
	 * 
	 * @param owner Document which owns this element.
	 * @param name Name of the element.
	 */
	public Html5ScriptElementImpl(HTMLDocumentImpl document, String name) {
		super(document, name);
		
		_fetchRegistry = FetchRegistry.getInstance();
		
		_browserScriptEngineManager = BrowserScriptEngineManager.getInstance();
		_alreadyStarted = false;
		_parserInserted = false;
		_wasParserInserted = false;
		_forceAsync = true;
		_readyToBeParserExecuted = false;
		
		_creatorDocument = (document instanceof Html5DocumentImpl)? (Html5DocumentImpl)document : null;
		_creatorParser = (document instanceof Html5DocumentImpl)? ((Html5DocumentImpl)document).getParser() : null;
		_parserInserted = _creatorParser.isParserTask(); //
	}

	/**
	 * Tests whether is this script element parser inserted.
	 * 
	 * @return True if is this script element parser inserted, otherwise false.
	 */
	public boolean isParserInserted() {
		return _parserInserted;
	}
	
	@Override
	public boolean getAsync() {
		return !getAttribute(ASYNC_ATTR_NAME).isEmpty();
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
		return !getAttribute(DEFER_ATTR_NAME).isEmpty();
	}
	
	@Override
	public void setDefer(boolean value) {
		if (value) {
			setAttribute(DEFER_ATTR_NAME, "");
		} else {
			removeAttribute(DEFER_ATTR_NAME);
		}
	}
	
	/**
	 * Runs prepare algorithm which might result in execution of the script.
	 * 
	 * @return True if prepare completed, false on some error.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/scripting-1.html#prepare-a-script">Prepare a script</a>
	 */
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
		boolean async = getAsync();
		if (_wasParserInserted && !async) {
			_forceAsync = true;
		}
		
		/* Step 4 */
		String src = getSrc();
		if (src.isEmpty()) {
			boolean isEmpty = !hasExecutableScript();

			if (isEmpty) {
				return false;
			}
		}
		
		/* Step 5 */
		// TODO: Re-check the interpretation of the HTML5 specification
		if (getOwnerDocument() == null) {
			return false;
		}
		
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
				
		/* Step 14 and 15 */
		boolean hasSrc = !src.isEmpty();
		boolean defer = getDefer();
		if (hasSrc && defer && _parserInserted && !async) {
			fetchResource(src, new ParserInsertedScriptFetchCompletedTask());
			_creatorParser.addOnFinishScript(this);
		} else if (hasSrc && _parserInserted && !async) {
			fetchResource(src, new ParserInsertedScriptFetchCompletedTask());
			_creatorParser.setPendingParsingBlockingScript(this);
		} else if (!hasSrc && _parserInserted && _creatorParser.getScriptNestingLevel() < 2 && _creatorParser.hasStyleSheetBlockScripts()) {
			_creatorParser.setPendingParsingBlockingScript(this);
			makeReadyToBeParserExecuted();
		} else if (hasSrc && !async && !_forceAsync) {
			fetchResource(src, new InOrderASAPScriptFetchCompletedTask());
			_creatorParser.addInOrderASAPScript(this);
		} else if (hasSrc) {
			fetchResource(src, new ASAPScriptFetchCompletedTask());
			_creatorParser.addASAPScript(this);
		} else {
			executeScript();
		}
			
		return true;
	}
	
	/**
	 * Tests whether has this script element inline executable script.
	 * 
	 * @return True if there are some non empty, non-comment nodes inside this element, otherwise false.
	 */
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
	
	/**
	 * Returns mime type of this script element.
	 * @return Mime type of this script element.
	 */
	public String getMimeType() {
		if (getType().isEmpty() && getLang().isEmpty()) {
			return DEFAULT_SCRIPT_MIME_TYPE;
		} else if (!getType().isEmpty()) {
			return getType().trim();
		} else if (!getLang().isEmpty()) {
			return "text/" + getLang().trim();
		}
		return "";
	}
	
	/**
	 * Runs execution algorithm above current script element.
	 * 
	 * @return True if execution ran to completion, false on some error.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/scripting-1.html#execute-the-script-block">Execute the script block</a>
	 */
	public boolean executeScript() {
		if (_parserInserted && (_creatorParser == null || _creatorParser.getDocument() != getOwnerDocument())) {
			return false;
		}
		
		Document _document = getOwnerDocument();
		Html5DocumentImpl document = null;
			
		if (_document instanceof Html5DocumentImpl) {
			document = (Html5DocumentImpl)_document;
		} else {
			return false;
		}
		
		Window window = document.getWindow();
		
		Resource resource = null;
		if (scriptFetch != null) {
			boolean error = !scriptFetch.isValid();
			resource = scriptFetch.getResource();
			error = error || resource == null;
			
			if (error) {

				window.fireSimpleEvent(GlobalEventHandlers.onerror, this);
				return false;
			}
		}

		Reader source = null;
		URL url = null;
		WindowScriptSettings settings = window.getScriptSettings();
		String language = getMimeType();
		
		// 1) Initialize the script block's source 
		// FIXME: http://www.w3.org/html/wg/drafts/html/master/scripting-1.html#the-script-block%27s-source
		if (resource == null) { // We are processing inline script
			source = getScriptBlockSource(); 
		} else {
			url = resource.getAddress();
			source = getScriptBlockSource(resource); 
		}
		
		// 2) Fire a simple event named beforescriptexecute that bubbles and is cancelable at the script element.
		boolean canceled = window.fireSimpleEvent("beforescriptexecute", this, true, true);
		if (canceled) {
			return false;
		}
		
		// 3) If the script is from an external file, then increment the ignore-destructive-writes counter 
		if (resource != null) {
			document.incrementIgnoreDestructiveWritesCounter();
		}
		
		// 4) Create a script
		new EvalWindowScript(source, url, language, settings, false);
		
		// 5) Decrement the ignore-destructive-writes counter 
		if (resource != null) {
			document.decrementIgnoreDestructiveWritesCounter();
		}
		
		// 6) Fire a simple event named afterscriptexecute that bubbles (but is not cancelable) 
		window.fireSimpleEvent("afterscriptexecute", this, true, false);
		
		// 7) If the script is from an external file, fire a simple event named load at the script element.
		if (resource != null) {
			window.fireSimpleEvent("load", this);
		} else {
			window.dispatchSimpleEvent("load", this);
		}
		
		return true;
	}
	
	/**
	 * Sets already started flag, so script will be not executed again and will act as cancelled.
	 */
	public void cancelExecution() {
		_alreadyStarted = true;
	}
	
	/**
	 * Delays execution of this script element.
	 */
	public void suspendExecution() {
		_parserInserted = true;
		_forceAsync = false;
	}
	
	/**
	 * Sets ready to be parser executed flag to true.
	 */
	public void makeReadyToBeParserExecuted() {
		_readyToBeParserExecuted = true;
		
		synchronized (_creatorParser) {
			_creatorParser.notifyAll();
		}
	}
	
	/**
	 * Tests ready to be parser executed flag.
	 * 
	 * @return True if ready to be parser executed flag is true, otherwise false.
	 */
	public boolean isReadyToBeParserExecuted() {
		return _readyToBeParserExecuted;
	}
	
	/**
	 * Returns reader which points to the inline script source.
	 * 
	 * @return Reader which points to the inline script source.
	 */
	public Reader getScriptBlockSource() {
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
	
	/**
	 * Returns reader which points to the resource script source.
	 * 
	 * @return Reader which points to the resource script source.
	 */
	protected Reader getScriptBlockSource(Resource resource) {
		InputStream is = resource.getInputStream();
		InputStreamReader reader = new InputStreamReader(is);
		
		return reader;
	}
	
	/**
	 * Fetches external resource.
	 * 
	 * @param src Source location from which fetch should be established.
	 * @param finishTask Task to be invoked after fetch is completed.
	 */
	protected void fetchResource(String src, Task finishTask) {
		Window window = _creatorDocument.getWindow();
		
		/*
		 * 2) If src is the empty string, queue a task to fire a simple event and abort 
		 */
		if (_creatorDocument == null || src == null || src.isEmpty()) {			
			window.fireSimpleEvent(GlobalEventHandlers.onerror, this);
			return;
		}
		
		/*
		 * 3) 4) Resolve src relative to the element, on error fire error event and abort
		 */
		URL baseUrl = _creatorDocument.getBaseAddress();
		URL url = null;
		try {
			url = new URL(baseUrl, src);
		} catch (MalformedURLException e) {
			window.fireSimpleEvent(GlobalEventHandlers.onerror, this);
			return;
		}
		
		BrowsingContext context = _creatorDocument.getBrowsingContext();
		scriptFetch = _fetchRegistry.getFetch(context, context, url, false, false, true, finishTask);
		
		if (scriptFetch != null) {
			try {
				scriptFetch.fetch();
			} catch (IOException e) {
				window.fireSimpleEvent(GlobalEventHandlers.onerror, this);
			}
		} else {
			window.fireSimpleEvent(GlobalEventHandlers.onerror, this);
		}
	}
	
	/**
	 * Tests whether is scripting disable for current document.
	 * 
	 * @return True if scripting is disable, otherwise false.
	 */
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

/**
 * ScriptDOMParser.java
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

package org.fit.cssbox.scriptbox.document.script;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.cyberneko.html.parsers.DOMParser;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * FIXME: This class should be better re-implemented to follow the specification, or at least extended.
 */
/**
 * Class representing inner implementation of the parser of documents.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#parsing">Parsing</a>
 */
public class ScriptDOMParser extends DOMParser {
	private ScriptableDocumentParser _parser;

	private boolean aborted;
	private Html5DocumentImpl _document;
	private String _charset;
	private XMLLocator _locator;

	public Document testDocumentDelteThis() {
		return fDocument;
	}

	public ScriptDOMParser(ScriptableDocumentParser parser, String charset) {
		_parser = parser;
		_document = parser.getDocument();
		_charset = charset;
	}

	@Override
	public void parse(InputSource inputSource) throws SAXException, IOException {
		reset();
		super.parse(inputSource);
	}

	@Override
	public Document getDocument() {
		return _document;
	}

	@Override
	public void reset() {
		try {
			setProperty("http://cyberneko.org/html/properties/names/elems",
					"lower");
			if (_charset != null) {
				setProperty(
						"http://cyberneko.org/html/properties/default-encoding",
						_charset);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {	
		/*
		 * Use passed document instance and do not create a new one by calling the super method.
		 */
		if (!fDeferNodeExpansion) {
			_locator = locator;
			CoreDocumentImpl documentImpl = null;

			if (_document instanceof CoreDocumentImpl) {
				documentImpl = (CoreDocumentImpl)_document;
				documentImpl.setStrictErrorChecking (false);
				documentImpl.setInputEncoding (encoding);
				if (locator != null) {
					documentImpl.setDocumentURI (locator.getExpandedSystemId ());
				}
			}
				
			fDocument = _document;
			fDocumentImpl = documentImpl;
			fCurrentNode = _document;
		} else {
			throw new UnsupportedOperationException("Parser does not support deferred node expansion.");
		}
	}
	
	@Override
	public void endDocument (Augmentations augs) throws XNIException {

		if (!fDeferNodeExpansion) {
			if (_document != null) {
				if (_locator != null) {
					_document.setInputEncoding (_locator.getEncoding());
				}
				_document.setStrictErrorChecking (true);
			}
			fCurrentNode = null;
		}
		else {
			throw new UnsupportedOperationException("Parser does not support deferred node expansion.");
		}
	}
	
	/*
	 * Implements only some aspects of the HTML5 specification.
	 */
	@Override
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		synchronized (this) {
			if (aborted) {
				throw new LifetimeEndedException();
			}
		}
		
		/**
		 * Creates new element and inserts it into the document. It fulfills some 
		 * demands of the HTML5 specification, but not all!
		 */
		super.startElement(element, attributes, augs);
				
		// FIXME: Steps 1) and 2) are somehow done following the HTML4, fix it to HTML5
		
		if (fCurrentNode instanceof Html5ScriptElementImpl) {
			Html5ScriptElementImpl scriptElement = (Html5ScriptElementImpl)fCurrentNode;
			// 3) Mark the element as being "parser-inserted" and unset the element's "force-async" flag.
			scriptElement.suspendExecution();
			
			// 4) If the parser was originally created for the HTML fragment parsing algorithm, 
			//	then mark the script element as "already started". (fragment case)
			if (_document instanceof DocumentFragment) {
				scriptElement.cancelExecution();
			}
		}
		
		// FIXME: Steps 5), 6), 7), 8), 9) and 10) are somehow done following the HTML4, fix it to HTML5
	}
	
	/*
	 * Implements only some aspects of proper execution of scripts according to the HTML5 specification.
	 */
	@Override
	public void endElement(QName element, Augmentations augs) throws XNIException {
		Node prevNode = fCurrentNode;
		super.endElement(element, augs);
		Node currNode = fCurrentNode;
		
		if (prevNode != currNode) { // We really ended previous element
			if (prevNode instanceof Html5ScriptElementImpl) {
				endHtml5ScriptElement((Html5ScriptElementImpl)prevNode);
			} else if (prevNode instanceof Html5IFrameElementImpl) {
				endHtml5IframeElement((Html5IFrameElementImpl)prevNode);
			}
		}
	}

	
	/* FIXME: Simplified, does not follow the specification. */
	/**
	 * Processes end of the HTML iframe tag.
	 * 
	 * @param iframeElement Parsed IFRAME element.
 	 */
	protected void endHtml5IframeElement (Html5IFrameElementImpl iframeElement) {
		Document document = iframeElement.getOwnerDocument();
		String src = iframeElement.getSrc();
		
		if (src != null && !src.isEmpty() && document instanceof Html5DocumentImpl) {
			Html5DocumentImpl documentImpl = (Html5DocumentImpl)document;
			BrowsingContext context = documentImpl.getBrowsingContext();
			if (context instanceof IFrameContainerBrowsingContext) {
				IFrameContainerBrowsingContext iframeBrowsableContext = (IFrameContainerBrowsingContext) context;
				BrowsingContext iframeContext = iframeBrowsableContext.createIFrameContext(iframeElement);
				
				NavigationController controller = iframeContext.getNavigationController();
				URL newUrl;
				try {
					newUrl = new URL(documentImpl.getBaseAddress(), src);
					controller.navigate(context, newUrl, false, false, true);
				} catch (MalformedURLException e) {
					// TODO: Throw exception?
					e.printStackTrace();
				}

			}
			
		}
	}
	
	/**
	 * Processes end of the HTML script tag.
	 * 
	 * @param scriptElement Parsed script element.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#scriptEndTag">SCRIPT end tag</a>
 	 */
	protected void endHtml5ScriptElement(final Html5ScriptElementImpl scriptElement) {		
		// TODO: 1) Perform a microtask checkpoint.
		// TODO: 2) Provide a stable state.
		// FIXME: 3), 4), 5) and 6) are somehow done following the HTML4, fix it to HTML5
		
		// Because we run in a separate thread, run at least execution in the correct one
		try {
			_document.getEventLoop().queueTaskAndWait(new Task(TaskSource.NETWORKING, _document) {

				@Override
				public void execute() throws TaskAbortedException, InterruptedException {
					// 7) Increment the parser's script nesting level by one.
					incrementScriptNestingLevel();
					
					// 8) Prepare the script.
					scriptElement.prepareScript();
					
					// 9) Decrement the parser's script nesting level by one. 
					//	If the parser's script nesting level is zero, then set the parser pause flag to false.
					decrementScriptNestingLevel();
				}
				
			});
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new UnknownException(e);
		}
		

		
		// TODO: 10) Let the insertion point have the value of the old insertion point. 

		executePendingParsingBlockingScript();
	}
	
	/**
	 * Executes pending-parsing blocking script if there is any ready to be parser executed.
	 */
	protected void executePendingParsingBlockingScript() {
		// 11) If there is a pending parsing-blocking script, then:
		Html5ScriptElementImpl script = null;
		synchronized (_parser) {
			Html5ScriptElementImpl pendingParsingBlockingScript = _parser.getPendingParsingBlockingScript();
			if (pendingParsingBlockingScript != null) {
				// 11.1) Set the parser pause flag to true, and abort the processing of any nested invocations of the tokenizer
				int scriptNestingLevel = _parser.getScriptNestingLevel();
				if (scriptNestingLevel != 0) {
					_parser.setPauseFlag(true);
				} 
				// 11.2) Run these steps:
				else {
					// 11.2.1) Let the script be the pending parsing-blocking script. 
					script = pendingParsingBlockingScript;
					_parser.setPendingParsingBlockingScript(null);
				}
			}
		}
		
		if (script != null) {
			// TODO: 11.2.2) Block the tokenizer for this instance of the HTML parser.
			
			synchronized (_parser) {				
				boolean shouldWait;
				// 11.2.3) If the parser's Document has a style sheet that is blocking scripts 
				//		 or the script's "ready to be parser-executed" flag is not set
				do {
					boolean hasStyleSheetBlockScripts = _parser.hasStyleSheetBlockScripts();
					boolean isReadyToBeParserExecuted = script.isReadyToBeParserExecuted();
					shouldWait = (hasStyleSheetBlockScripts || !isReadyToBeParserExecuted);
					
					// Spin the event loop until the parser's Document has no style sheet that is
					// blocking scripts and the script's "ready to be parser-executed" flag is set
					// Note: We are not spinning because we are running in different thread, 
					// so monitor is used here to solve this.
					if (shouldWait) {
						try {
							_parser.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							throw new UnknownException(e);
						}
					}

				} while (shouldWait);
			}
			
			// TODO: 11.2.4) If this parser has been aborted in the meantime, abort these steps.
			
			// TODO: 11.2.5) Unblock the tokenizer for this instance of the HTML parser
						
			// TODO: 11.2.6) Let the insertion point be just before the next input character.
						
			// Because we run in a separate thread, run at least execution in the correct one
			final Html5ScriptElementImpl scriptElement = script;
			try {
				_document.getEventLoop().queueTaskAndWait(new Task(TaskSource.NETWORKING, _document) {

					@Override
					public void execute() throws TaskAbortedException, InterruptedException {
						// 11.2.7) Increment the parser's script nesting level by one.
						incrementScriptNestingLevel();
						
						// 11.2.8) Execute the script.
						scriptElement.executeScript();
						
						// 11.2.9) Decrement the parser's script nesting level by one.
						decrementScriptNestingLevel();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
				throw new UnknownException(e);
			}
				
			// TODO: 11.2.10) Let the insertion point be undefined again.
					
			// 11.2.11) If there is once again a pending parsing-blocking script, then repeat
			executePendingParsingBlockingScript();
		}
	}
	
	protected void incrementScriptNestingLevel() {
		synchronized (_parser) {
			int scriptNestingLevel = _parser.getScriptNestingLevel();
			scriptNestingLevel++;
			_parser.setScriptNestingLevel(scriptNestingLevel);
		}
	}

	protected void decrementScriptNestingLevel() {
		synchronized (_parser) {
			int scriptNestingLevel = _parser.getScriptNestingLevel();
			scriptNestingLevel--;
			_parser.setScriptNestingLevel(scriptNestingLevel);
			
			if (scriptNestingLevel == 0) {
				_parser.setPauseFlag(false);
			}
		}
	}
	
	@Override
    public synchronized void abort () {
        aborted = true;
    }
}

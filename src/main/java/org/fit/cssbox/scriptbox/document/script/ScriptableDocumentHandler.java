package org.fit.cssbox.scriptbox.document.script;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IframeBrowsable;
import org.fit.cssbox.scriptbox.document.event.EventDocumentHandlerDecorator;
import org.fit.cssbox.scriptbox.document.event.EventProcessingProvider;
import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class ScriptableDocumentHandler extends EventDocumentHandlerDecorator {
	private int scriptNestingLevel;
	private boolean parserPauseFlag;
	private Html5ScriptElementImpl pendingParsingBlockingScript;
	private boolean hasStyleSheetBlockScripts;		   // FIXME: Is never set, info about style sheets is not propagated here
	private Object pauseMonitor;
	private Html5DocumentImpl _document;
	private XMLLocator _locator;
	
	public ScriptableDocumentHandler(XMLDocumentHandler oldHandler, EventProcessingProvider processingInfoProvider, Html5DocumentImpl document) {
		super(oldHandler, processingInfoProvider);
		
		pauseMonitor = new Object();
		_document = document;
	}
	
	@Override
	public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {	
		if (!processingInfoProvider.isDeferNodeExpansion()) {
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
				
			processingInfoProvider.setDocument(_document);
			processingInfoProvider.setDocumentImpl(documentImpl);
			processingInfoProvider.setCurrentNode(_document);
		} else {
			throw new UnsupportedOperationException("Parser does not support deferred node expansion.");
		}
	}
	
	@Override
	public void endDocument (Augmentations augs) throws XNIException {

		if (!processingInfoProvider.isDeferNodeExpansion()) {
			if (_document != null) {
				if (_locator != null) {
					_document.setInputEncoding (_locator.getEncoding());
				}
				_document.setStrictErrorChecking (true);
			}
			processingInfoProvider.setCurrentNode(null);
		}
		else {
			throw new UnsupportedOperationException("Parser does not support deferred node expansion.");
		}

	} // endDocument()
	
	/*
	 * Implements only some aspects of the HTML5 specification.
	 */
	@Override
	public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
		/**
		 * Creates new element and inserts it into the document. It fulfills some 
		 * demands of the HTML5 specification, but not all!
		 */
		super.startElement(element, attributes, augs);
		
		Node currNode = processingInfoProvider.getCurrentNode();
		
		// FIXME: Steps 1) and 2) are somehow done following the HTML4, fix it to HTML5
		
		if (currNode instanceof Html5ScriptElementImpl) {
			Html5ScriptElementImpl scriptElement = (Html5ScriptElementImpl)currNode;
			// 3) Mark the element as being "parser-inserted" and unset the element's "force-async" flag.
			scriptElement.suspendExecution();
			
			// 4) If the parser was originally created for the HTML fragment parsing algorithm, 
			//	then mark the script element as "already started". (fragment case)
			if (processingInfoProvider.isDocumentFragmentParser()) {
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
		Node prevNode = processingInfoProvider.getCurrentNode();
		super.endElement(element, augs);
		Node currNode = processingInfoProvider.getCurrentNode();
		
		if (prevNode != currNode) { // We really ended previous element
			if (prevNode instanceof Html5ScriptElementImpl) {
				endHtml5ScriptElement((Html5ScriptElementImpl)prevNode);
			} else if (prevNode instanceof Html5IFrameElementImpl) {
				endHtml5IframeElement((Html5IFrameElementImpl)prevNode);
			}
		}
	}
	
	@Override
	public void reset() {
		super.reset();
	}
	
	public void pauseParsing() {
		parserPauseFlag = true;
		
		synchronized (pauseMonitor) {
			while (parserPauseFlag) {
				try {
					pauseMonitor.wait();
				} catch (Exception e) {}
			}
		}
	}
	
	public void resumeParsing() {
		synchronized (pauseMonitor) {
			parserPauseFlag = false;
			pauseMonitor.notifyAll();
		}
	}
	
	/* Simplified, does not follow the norm */
	protected void endHtml5IframeElement (Html5IFrameElementImpl iframeElement) {
		Document document = iframeElement.getOwnerDocument();
		String src = iframeElement.getSrc();
		
		if (src != null && !src.isEmpty() && document instanceof Html5DocumentImpl) {
			Html5DocumentImpl documentImpl = (Html5DocumentImpl)document;
			BrowsingContext context = documentImpl.getBrowsingContext();
			if (context instanceof IframeBrowsable) {
				IframeBrowsable iframeBrowsableContext = (IframeBrowsable) context;
				BrowsingContext iframeContext = iframeBrowsableContext.createIFrameContext(iframeElement);
				
				NavigationController controller = iframeContext.getNavigationController();
				URL newUrl;
				try {
					newUrl = new URL(documentImpl.getAddress(), src);
					controller.navigate(context, newUrl, false, false, true);
				} catch (MalformedURLException e) {
					// TODO: Throw exception?
					e.printStackTrace();
				}

			}
			
		}
	}
	
	protected void endHtml5ScriptElement(Html5ScriptElementImpl scriptElement) {		
		// TODO: 1) Perform a microtask checkpoint.
		// TODO: 2) Provide a stable state.
		// FIXME: 3), 4), 5) and 6) are somehow done following the HTML4, fix it to HTML5
		
		// 7) Increment the parser's script nesting level by one.
		incrementScriptNestingLevel();
		
		// 8) Prepare the script.
		scriptElement.prepareScript();
		
		// 9) Decrement the parser's script nesting level by one. 
		//	If the parser's script nesting level is zero, then set the parser pause flag to false.
		decrementScriptNestingLevel();
		
		// TODO: 10) Let the insertion point have the value of the old insertion point. 
		
		// 11) If there is a pending parsing-blocking script, then:
		if (pendingParsingBlockingScript != null) {
			// 11.1) Set the parser pause flag to true, and abort the processing of any nested invocations of the tokenizer
			if (scriptNestingLevel != 0) {
				pauseParsing();
			} 
			// 11.2) Run these steps:
			else {
				while (pendingParsingBlockingScript != null) {
					// 11.2.1) Let the script be the pending parsing-blocking script. 
					scriptElement = pendingParsingBlockingScript;
					
					// TODO: 11.2.2) Block the tokenizer for this instance of the HTML parser.
					
					// 11.2.3) If the parser's Document has a style sheet that is blocking scripts 
					//		 or the script's "ready to be parser-executed" flag is not set
					if (hasStyleSheetBlockScripts || !scriptElement.isReadyToBeParserExecuted()) {
						// TODO: Spin the event loop until the parser's Document has no style sheet that is
						//	   blocking scripts and the script's "ready to be parser-executed" flag is set
					}
					
					// 11.2.4) If this parser has been aborted in the meantime, abort these steps.
					if (processingInfoProvider.isParserAborted()) {
						return;
					}
					
					// TODO: 11.2.5) Unblock the tokenizer for this instance of the HTML parser
					
					// TODO: 11.2.6) Let the insertion point be just before the next input character.
					
					// 11.2.7) Increment the parser's script nesting level by one.
					incrementScriptNestingLevel();
					
					// 11.2.8) Execute the script.
					scriptElement.executeScript();
					
					// 11.2.9) Decrement the parser's script nesting level by one.
					decrementScriptNestingLevel();
					
					// TODO: 11.2.10) Let the insertion point be undefined again.
					
					// 11.2.11) If there is once again a pending parsing-blocking script, then repeat
				}

			}
		}
	}
	
	protected void incrementScriptNestingLevel() {
		scriptNestingLevel++;
	}

	protected void decrementScriptNestingLevel() {
		scriptNestingLevel--;
		if (scriptNestingLevel == 0) {
			resumeParsing();
		}
	}
	
	public int getScriptNestingLevel() {
		return scriptNestingLevel;
	}
	
	public boolean hasStyleSheetBlockScripts() {
		return hasStyleSheetBlockScripts;
	}
}

package org.fit.cssbox.scriptbox.document.script;

import java.io.IOException;

import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/*
 * Limitations: Dedicated only for parsing of one document.
 */
public class ScriptDOMParser extends EventDOMParser {
	
	BrowsingContext _browsingContext;
	ScriptableDocument _document;
	
	ScriptDOMParser(BrowsingContext browsingContext) {
		_browsingContext = browsingContext;
		_document = new ScriptableDocument(_browsingContext);
	}
	
	@Override
	public void parse(InputSource inputSource) throws SAXException, IOException {
		super.parse(inputSource);
		parsingFinalization();
	}
	
	@Override
	public void parse(String systemId) throws SAXException, IOException {
		super.parse(systemId);
		parsingFinalization();
	}
	
	@Override
	public void parse(XMLInputSource inputSource) throws XNIException, IOException {
		super.parse(inputSource);
		parsingFinalization();
	}
	
	@Override
	public Document getDocument() {
		return _document;
	}
	
	@Override
	protected void initParser() {	
		super.initParser();
		
		XMLDocumentHandler handler = processingProvider.getConfiguration().getDocumentHandler();
		XMLDocumentHandler newHandler = new ScriptableDocumentHandler(handler, processingProvider, _document);
	    
		processingProvider.getConfiguration().setDocumentHandler(newHandler);

		try {
			setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.fit.cssbox.scriptbox.document.script.ScriptableDocument");
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	protected void parsingFinalization() {
		/*
		 * TODO: See: http://www.w3.org/html/wg/drafts/html/master/syntax.html#the-end
		 * - implement defer scripts and delayed scripts due remote resources
		 */
	}
}

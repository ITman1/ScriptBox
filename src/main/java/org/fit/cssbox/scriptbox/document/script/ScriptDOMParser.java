package org.fit.cssbox.scriptbox.document.script;

import java.io.IOException;

import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.fit.cssbox.scriptbox.document.event.EventDOMParser;
import org.fit.cssbox.scriptbox.document.event.EventDocumentHandlerDecorator;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/*
 * Limitations: Dedicated only for parsing of one document.
 */
public class ScriptDOMParser extends EventDOMParser {
	
	private Html5DocumentImpl _document;
	private XMLDocumentHandler _superXMLDocumentHandler;

	public ScriptDOMParser(Html5DocumentImpl document) {
		_document = document;
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

		try {
			setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.fit.cssbox.scriptbox.dom.Html5DocumentImpl");
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
	
	@Override
	protected EventDocumentHandlerDecorator instantizeEventDocumentHandlerDecorator() {
		if (_superXMLDocumentHandler == null) {
			_superXMLDocumentHandler = processingProvider.getConfiguration().getDocumentHandler();
		}
		return new ScriptableDocumentHandler(_superXMLDocumentHandler, processingProvider, _document);
	}
}

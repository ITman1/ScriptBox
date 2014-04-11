package org.fit.cssbox.scriptbox.document.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * Limitations: Dedicated only for parsing of one document.
 */
public class ScriptableDocumentParser {
	private ScriptDOMParser _parser;
	private Html5DocumentImpl _document;
	private String _parserSource;
	private String _charset;
	
	public ScriptableDocumentParser() {
		this(null);
	}
	
	public ScriptableDocumentParser(String charset) {
		_charset = charset;
	}
	
	public void parse(Html5DocumentImpl document, InputStream inputStream) throws SAXException, IOException {
		_document = document;
		_parser = new ScriptDOMParser(document, _charset);
		
		int readBytes = 0;
		byte[] buffer = new byte[1024 * 1024];
		ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
		while ((readBytes = inputStream.read(buffer)) >= 0) {
			byteOutputStream.write(buffer, 0, readBytes);
		}
		
		byte[] parserSourceArr = byteOutputStream.toByteArray();

		_parserSource = new String(parserSourceArr);
		
		InputStream parserStream = new ByteArrayInputStream(parserSourceArr);
		InputSource parserSource = new InputSource(parserStream);
		_parser.parse(parserSource);
	}
	
	public String getParserSource() {
		return _parserSource;
	}
	
	public Html5DocumentImpl getDocument() {
		return _document;
	}
	
	public int scriptNestingLevel() {
		return _parser.getScriptNestingLevel();
	}
	
	public boolean hasStyleSheetBlockScripts() {
		return _parser.hasStyleSheetBlockScripts();
	}
	
	public boolean isActive() {
		return false;
	}
	
	public void abort() {
		if (_parser != null) {
			_parser.abort();
		}
	}
}

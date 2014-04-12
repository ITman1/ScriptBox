package org.fit.cssbox.scriptbox.document.script;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
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
	private ParserFinishedTask _onFinishedTask;
	
	private boolean aborted;
	private boolean pauseFlag;
	private int scriptNestingLevel;
	private Html5ScriptElementImpl pendingParsingBlockingScript;
	private boolean hasStyleSheetBlockScripts; // FIXME: Is never set, info
												// about style sheets is not
												// propagated here
	
	private List<Html5ScriptElementImpl> _onFinishScripts;
	private List<Html5ScriptElementImpl> _inOrderASAPScripts;
	private List<Html5ScriptElementImpl> _asapcripts;
	
	public ScriptableDocumentParser() {
		this(null);
	}
	
	public ScriptableDocumentParser(String charset) {
		_charset = charset;
		
		_onFinishScripts = new ArrayList<Html5ScriptElementImpl>();
		_inOrderASAPScripts = new ArrayList<Html5ScriptElementImpl>();
		_asapcripts =  new ArrayList<Html5ScriptElementImpl>();
	}
	
	public synchronized List<Html5ScriptElementImpl> getOnFinishScripts() {
		return Collections.unmodifiableList(_onFinishScripts);
	}
	
	public synchronized List<Html5ScriptElementImpl> getInOrderASAPScripts() {
		return Collections.unmodifiableList(_inOrderASAPScripts);
	}
	
	public synchronized Collection<Html5ScriptElementImpl> getASAPScripts() {
		return Collections.unmodifiableCollection(_asapcripts);
	}
	
	public synchronized void addOnFinishScript(Html5ScriptElementImpl script) {
		_onFinishScripts.add(script);
		notifyAll();
	}
	
	public synchronized void removeOnFinishScript(Html5ScriptElementImpl script) {
		_onFinishScripts.remove(script);
		notifyAll();
	}
	
	public synchronized void addInOrderASAPScript(Html5ScriptElementImpl script) {
		_inOrderASAPScripts.add(script);
		notifyAll();
	}
	
	public synchronized void removeInOrderASAPScript(Html5ScriptElementImpl script) {
		_inOrderASAPScripts.remove(script);
		notifyAll();
	}
	
	public synchronized void addASAPScript(Html5ScriptElementImpl script) {
		_asapcripts.add(script);
		notifyAll();
	}
	
	public synchronized void removeASAPScript(Html5ScriptElementImpl script) {
		_asapcripts.remove(script);
		notifyAll();
	}	
	
	public void parse(Html5DocumentImpl document, InputStream inputStream) throws IOException {
		parse(document, inputStream, null);
	}
	
	public void parse(Html5DocumentImpl document, InputStream inputStream, ParserFinishedTask onFinishedTask) {
		synchronized (this) {
			_document = document;
			_onFinishedTask = onFinishedTask;
		}

		Exception exception = null;
		
		beforeParserStarted();
		
		try {
			synchronized (this) {
				_parser = new ScriptDOMParser(this, _charset);
			}
			
			int readBytes = 0;
			byte[] buffer = new byte[1024 * 1024];
			ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
			while ((readBytes = inputStream.read(buffer)) >= 0) {
				byteOutputStream.write(buffer, 0, readBytes);
			}
			
			byte[] parserSourceArr = byteOutputStream.toByteArray();

			synchronized (this) {
				_parserSource = new String(parserSourceArr);
			}

			
			InputStream parserStream = new ByteArrayInputStream(parserSourceArr);
			InputSource parserSource = new InputSource(parserStream);
			
			runParser(parserSource);
		} catch (Exception e) {
			exception = e;
			afterParserFinished(exception);
		}
	}
	
	public synchronized boolean isAborted() {
		return aborted;
	}
	
	public synchronized boolean getPauseFlag() {
		return pauseFlag;
	}
	
	public synchronized void setPauseFlag(boolean flag) {
		pauseFlag = flag;
	}
	
	public synchronized String getParserSource() {
		return _parserSource;
	}
	
	public synchronized Html5DocumentImpl getDocument() {
		return _document;
	}
	
	public synchronized ParserFinishedTask getOnFinishedTask() {
		return _onFinishedTask;
	}
	
	public synchronized Html5ScriptElementImpl getPendingParsingBlockingScript() {
		return pendingParsingBlockingScript;
	}
	
	public synchronized void setPendingParsingBlockingScript(Html5ScriptElementImpl script) {
		pendingParsingBlockingScript = script;
		notifyAll();
	}
	
	public synchronized void setScriptNestingLevel(int level) {
		scriptNestingLevel = level;
		notifyAll();
	}
	
	public synchronized int getScriptNestingLevel() {
		return scriptNestingLevel;
	}
	
	public synchronized boolean hasStyleSheetBlockScripts() {
		return hasStyleSheetBlockScripts;
	}
	
	public synchronized boolean isActive() {
		return _parser != null;
	}
	
	public synchronized void abort() {
		if (_parser != null) {
			_parser.abort();
		}
		
		aborted = true;
	}
	
	/*
	 * Expected to be called from Event loop thread.
	 */
	protected void beforeParserStarted() {
		_document.setDocumentReadiness(DocumentReadiness.LOADING);
	}
	
	/*
	 * Expected to be called from parser thread.
	 */
	protected void afterParserFinished(final Exception exception) {
		BrowsingContext context = _document.getBrowsingContext();
		context.getEventLoop().queueTask(new TheEndTask(this));
		
		synchronized (this) {
			_parser = null;
		}
	}
	
	protected void runParser(final InputSource parserSource) throws SAXException, IOException {
		Thread asyncThread = new Thread() {
			@Override
			public void run() {
				Exception exception = null;
				try {
					_parser.parse(parserSource);
				} catch (Exception e) {
					e.printStackTrace();
					exception = e;
				} finally {
					afterParserFinished(exception);
				}
			}
		};
		asyncThread.start();
		return;

	}
}

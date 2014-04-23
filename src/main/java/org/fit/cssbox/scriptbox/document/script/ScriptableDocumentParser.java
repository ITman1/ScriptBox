/**
 * ScriptableDocumentParser.java
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.exceptions.LifetimeEndedException;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/*
 * Limitations: Dedicated only for parsing of one document.
 * Parser is not reusable, reentrant and does not support document write, etc.
 * 
 * FIXME: This implementation is only partial and might be incorrect, so it should be 
 *        in the future re-implemented, completed according to the specification.
 *        Interface of this parser should be left intact, because other parts rely on it.
 *        Interface should be only extended, but inner implementation of the ScriptDOMParser
 *        should be re-implemented instead!
 */
/**
 * Class ensuring parsing of the Document object from the input stream.
 * Additionally it allows running scripts above the document.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#html-parser">HTML parser</a>
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#parsing">Parsing</a>
 */
public class ScriptableDocumentParser {
	
	/**
	 * Thread which parses the Document.
	 */
	private class ParserThread extends Thread {
		private InputSource parserSource;
		
		public ParserThread(InputSource parserSource) {
			this.parserSource = parserSource;
		}
		
		@Override
		public void run() {
			Exception exception = null;
			try {
				_parser.parse(parserSource);
			} catch (LifetimeEndedException e) { // we only aborted
				exception = e;
			} catch (Exception e) {
				e.printStackTrace();
				exception = e;
			} finally {
				afterParserFinished(exception);
			}
		}
		
		@Override
		public String toString() {
			URL address = _document.getAddress();
			String sourceUrl = (address != null)? address.toExternalForm() : "(no url)";
			return "Parser Thread - " + sourceUrl;
		}
	}
	
	private ScriptDOMParser _parser;
	private boolean _finished;
	private Html5DocumentImpl _document;
	private String _parserSource;
	private String _charset;
	private ParserFinishedTask _onFinishedTask;
	
	private boolean aborted;
	private boolean pauseFlag;
	private int scriptNestingLevel;
	private Html5ScriptElementImpl pendingParsingBlockingScript;
	/*
	 * FIXME: Is never set, info about style sheets is not propagated here
	 */
	private boolean hasStyleSheetBlockScripts;
	
	private List<Html5ScriptElementImpl> _onFinishScripts;
	private List<Html5ScriptElementImpl> _inOrderASAPScripts;
	private List<Html5ScriptElementImpl> _asapcripts;
	
	/**
	 * Creates parser.
	 */
	public ScriptableDocumentParser() {
		this(null);
	}
	
	/**
	 * Creates parser for input stream with manually given encoding.
	 * 
	 * @param charset Encoding of the input stream.
	 */
	public ScriptableDocumentParser(String charset) {
		_charset = charset;
		
		_onFinishScripts = new ArrayList<Html5ScriptElementImpl>();
		_inOrderASAPScripts = new ArrayList<Html5ScriptElementImpl>();
		_asapcripts =  new ArrayList<Html5ScriptElementImpl>();
	}
	
	/**
	 * Returns list of all scripts which are waiting until 
	 * parser finishes parsing and dispatches load event.
	 * 
	 * @return List of all scripts awaiting for load event.
	 */
	public synchronized List<Html5ScriptElementImpl> getOnFinishScripts() {
		return Collections.unmodifiableList(_onFinishScripts);
	}
	
	/**
	 * Returns list of all scripts which be run immediately as soon as possible,
	 * but which should be executed in order as they were added into the list.
	 * 
	 * @return List of all scripts to be executed immediately and in order they were added into the list.
	 */
	public synchronized List<Html5ScriptElementImpl> getInOrderASAPScripts() {
		return Collections.unmodifiableList(_inOrderASAPScripts);
	}
	
	/**
	 * Returns collection of all scripts which should executed as soon as possible,
	 * without caring how they were added into the list.
	 * 
	 * @return Collection of all scripts which should be executed as soon as possible.
	 */
	public synchronized Collection<Html5ScriptElementImpl> getASAPScripts() {
		return Collections.unmodifiableCollection(_asapcripts);
	}
	
	/**
	 * Adds scripts into list of scripts that will execute when the document has finished parsing.
	 * 
	 * @param script Script to be added into list of scripts that will execute when the document has finished parsing.
	 */
	public synchronized void addOnFinishScript(Html5ScriptElementImpl script) {
		_onFinishScripts.add(script);
		notifyAll();
	}
	
	/**
	 * Removes scripts into list of scripts that will execute when the document has finished parsing.
	 * 
	 * @param script Script to be removed into list of scripts that will execute when the document has finished parsing.
	 */
	public synchronized void removeOnFinishScript(Html5ScriptElementImpl script) {
		_onFinishScripts.remove(script);
		notifyAll();
	}
	
	/**
	 * Adds scripts into list of scripts that will execute in order as soon as possible .
	 * 
	 * @param script Script to be added into list of scripts that will execute in order as soon as possible.
	 */
	public synchronized void addInOrderASAPScript(Html5ScriptElementImpl script) {
		_inOrderASAPScripts.add(script);
		notifyAll();
	}
	
	/**
	 * Removes scripts into list of scripts that will execute in order as soon as possible .
	 * 
	 * @param script Script to be removed into list of scripts that will execute in order as soon as possible.
	 */
	public synchronized void removeInOrderASAPScript(Html5ScriptElementImpl script) {
		_inOrderASAPScripts.remove(script);
		notifyAll();
	}
	
	/**
	 * Adds scripts into list of scripts that will execute as soon as possible .
	 * 
	 * @param script Script to be added into list of scripts that will execute as soon as possible.
	 */
	public synchronized void addASAPScript(Html5ScriptElementImpl script) {
		_asapcripts.add(script);
		notifyAll();
	}
	
	/**
	 * Removes scripts into list of scripts that will execute as soon as possible .
	 * 
	 * @param script Script to be removed into list of scripts that will execute as soon as possible.
	 */
	public synchronized void removeASAPScript(Html5ScriptElementImpl script) {
		_asapcripts.remove(script);
		notifyAll();
	}	
	
	/**
	 * Parses document from the given input stream.
	 * 
	 * @param document Document where to store parsed document.
	 * @param inputStream Input stream containing serialized Document.
	 * @throws IOException IOException
	 */
	public void parse(Html5DocumentImpl document, InputStream inputStream) throws IOException {
		parse(document, inputStream, null);
	}
	
	/**
	 * Parses document from the given input stream and executes task after parsing finished.
	 * 
	 * @param document Document where to store parsed document.
	 * @param inputStream Input stream containing serialized Document.
	 * @param onFinishedTask Task to be executed after parser finishes parsing.
	 */
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
		
	/**
	 * Returns value of the pause flag.
	 * 
	 * @return Boolean value of the pause flag.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#parser-pause-flag">Parser pause flag</a>
	 */
	public synchronized boolean getPauseFlag() {
		return pauseFlag;
	}
	
	/**
	 * Sets value of the pause flag.
	 * 
	 * @param flag New boolean value of the pause flag.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#parser-pause-flag">Parser pause flag</a>
	 */
	public synchronized void setPauseFlag(boolean flag) {
		pauseFlag = flag;
	}
	
	/**
	 * Returns source code of the downloaded resource if there is any.
	 * 
	 * @return Source code of the downloaded resource, otherwise null.
	 */
	public synchronized String getParserSource() {
		return _parserSource;
	}
	
	/**
	 * Returns parsed Document mode.
	 * 
	 * @return Model of the Document.
	 */
	public synchronized Html5DocumentImpl getDocument() {
		return _document;
	}
	
	/**
	 * Returns task to which has been associated this parser a 
	 * which will be executed as parses finishes parsing.
	 * 
	 * @return Task to be executed after parser finishes parsing.
	 */
	public synchronized ParserFinishedTask getOnFinishedTask() {
		return _onFinishedTask;
	}
	
	/**
	 * Returns pending parsing-blocking script which will be executed 
	 * as it gets fetched from the external resource.
	 * 
	 * @return Pending parsing-blocking script.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/scripting-1.html#pending-parsing-blocking-script">Pending parsing-blocking script </a>
	 */
	public synchronized Html5ScriptElementImpl getPendingParsingBlockingScript() {
		return pendingParsingBlockingScript;
	}
	
	/**
	 * Sets pending parsing-blocking script which will be executed 
	 * as it gets fetched from the external resource.
	 * 
	 * @param script Pending parsing-blocking script.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/scripting-1.html#pending-parsing-blocking-script">Pending parsing-blocking script </a>
	 */
	public synchronized void setPendingParsingBlockingScript(Html5ScriptElementImpl script) {
		pendingParsingBlockingScript = script;
		notifyAll();
	}
	
	/**
	 * Sets script nesting level.
	 * 
	 * @param level New script nesting level.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#script-nesting-level">Script nesting level </a>
	 */
	public synchronized void setScriptNestingLevel(int level) {
		scriptNestingLevel = level;
		notifyAll();
	}
	
	/**
	 * Returns script nesting level.
	 * 
	 * @return Script nesting level.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#script-nesting-level">Script nesting level </a>
	 */
	public synchronized int getScriptNestingLevel() {
		return scriptNestingLevel;
	}
	
	/**
	 * Returns true if parser has style sheet that blocks script execution.
	 * 
	 * @return True if parser has style sheet that blocks script execution, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/document-metadata.html#has-a-style-sheet-that-is-blocking-scripts">Style sheet that is blocking scripts</a>
	 */
	public synchronized boolean hasStyleSheetBlockScripts() {
		return hasStyleSheetBlockScripts;
	}
	
	/**
	 * Returns true if parser is still active.
	 * 
	 * @return True if parser is still active, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/dom.html#active-parser">Active parser</a>
	 */
	public synchronized boolean isActive() {
		return _parser != null;
	}
	
	/**
	 * Returns true if parser has stopped parsing.
	 * 
	 * @return True if parser has stopped parsing, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#stop-parsing">Stopped parser</a>
	 */
	public synchronized boolean isStopped() {
		return _finished;
	}
	
	/**
	 * Returns true if parser has been aborted.
	 * 
	 * @return True if parser has been aborted, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#abort-a-parser">Abort a parser</a>
	 */
	public synchronized boolean isAborted() {
		return aborted;
	}
	
	/**
	 * Aborts parser, not matter if it runs or not.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/syntax.html#abort-a-parser">Abort a parser</a>
	 */
	public synchronized void abort() {
		if (!aborted) {
			if (_parser != null) {
				try {
					_parser.abort();
				} catch (Exception e) {
					
				}
			}
			
			aborted = true;
		}
	}
	
	/**
	 * Tests if current running thread is the parser thread.
	 * 
	 * @return True if caller thread is the parser thread, otherwise false.
	 */
	public synchronized boolean isParserTask() {
		return Thread.currentThread() instanceof ParserThread;
	}
	

	/**
	 * Callback method which is called when parser is ready to start after this method returns.
	 */
	protected void beforeParserStarted() {
		// Expected to be called from Event loop thread
		_document.setDocumentReadiness(DocumentReadiness.LOADING);
	}

	/**
	 * Callback method which is called when parser has finished parsing.
	 * 
	 * @param exception Exception if there is any and that caused parser to stop.
	 */
	protected void afterParserFinished(final Exception exception) {
		/*
		 * Expected to be called from parser thread, so it is necessary to queue a new task.
		 */
		BrowsingContext context = _document.getBrowsingContext();
		context.getEventLoop().queueTask(new TheEndTask(this, exception));
		
		synchronized (this) {
			_finished = true;
			_parser = null;
		}
	}
	
	/**
	 * Starts the parser asynchronously.
	 * 
	 * @param parserSource Source which will be used to for reading input data.
	 * @throws SAXException SAXException
	 * @throws IOException IOException
	 */
	protected void runParser(final InputSource parserSource) throws SAXException, IOException {
		ParserThread parserThread = new ParserThread(parserSource);
		parserThread.start();
		return;
	}
}

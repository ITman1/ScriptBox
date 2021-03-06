/**
 * Handler.java
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

package org.fit.cssbox.scriptbox.url.javascript;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLStreamHandler;

import org.apache.commons.io.input.ReaderInputStream;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.exceptions.WrappedException;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.window.Window;
import org.fit.cssbox.scriptbox.window.EvalWindowScript;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;
import org.mozilla.javascript.Undefined;

/**
 * Handler that enables us to use JAVASCRIPT protocol and also provides 
 * URL connection that executes underlying JavaScript source code. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class Handler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new JavaScriptURLConnection(url);
	}
	
	public static class JavaScriptURLConnection extends HttpURLConnection {
		class JavascriptFetchTask extends Task {

			private EvalWindowScript script;
			
			public JavascriptFetchTask(BrowsingContext browsingContext) {
				super(TaskSource.DOM_MANIPULATION, browsingContext);
			}

			/*
			 * According to the step 14), see:
			 * http://www.w3.org/html/wg/drafts/html/master/browsers.html#navigate
			 */
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				Html5DocumentImpl sourceDocument = sourceContext.getActiveDocument();
				Html5DocumentImpl destinationDocument = destinationContext.getActiveDocument();
				Origin<?> sourceOrigin = sourceDocument.getOrigin();
				Origin<?> destinationOrigin = destinationDocument.getOrigin();
				Window destinationWindow = destinationDocument.getWindow();
				WindowScriptSettings destinationScriptSettings = destinationWindow.getScriptSettings();
				
				result = Undefined.instance;
				exception = null;
				
				if (!sourceOrigin.equals(destinationOrigin)) {
					processResults();
					return;
				}
				
				String scriptSource = "";
				scriptSource += url.getPath();
				scriptSource += (url.getQuery() != null)? "?" + url.getQuery() : "";
				scriptSource += (url.getQuery() != null)? "#" + url.getRef() : "";
				try {
					scriptSource = URLDecoder.decode(scriptSource, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					return;
				}
				
				URL address = destinationDocument.getAddress();
				Reader source = new StringReader(scriptSource);
				script = new EvalWindowScript(source, address, WindowJavaScriptEngine.JAVASCRIPT_LANGUAGE, destinationScriptSettings, false);
				
				result = script.getResult();
				exception = script.getException();

				processResults();
			}
			
			private void processResults() {
				BrowsingUnit unit = destinationContext.getBrowsingUnit();
				UserAgent agent = (unit != null)? unit.getUserAgent() : null;
				boolean scriptingEnabled = (agent != null)? agent.scriptsSupported() : true;
				
				if (exception != null || result == null || !scriptingEnabled) {
					result = Undefined.instance;
				}
				
				if (exception != null) {
					Html5DocumentImpl sourceDocument = sourceContext.getActiveDocument();
					sourceDocument.reportScriptError(script);
				}
				
				String outputText = "";
				if (result == Undefined.instance) {
					responseCode = 204;
					outputText = "204 No Content response";
				} else {
					responseCode = 200;
					outputText = result.toString();
				}
				
				StringBuilder resultStringBuilder = new StringBuilder();
				resultStringBuilder.append("<!DOCTYPE HTML>\n");
				resultStringBuilder.append("<html>\n");
				resultStringBuilder.append("\t<head></head>\n");
				resultStringBuilder.append("\t<body>");
				resultStringBuilder.append(outputText);
				resultStringBuilder.append("</body>\n");
				resultStringBuilder.append("</html>\n");

				String resultString = resultStringBuilder.toString();
				Reader reader = new StringReader(resultString);
				is = new ReaderInputStream(reader);
			}
			
		}
		
		private BrowsingContext sourceContext;
		private BrowsingContext destinationContext;
		
		private JavascriptFetchTask fetchTask;
		private Object result;
		private Exception exception;
		private InputStream is;
		
		public JavaScriptURLConnection(URL url) {
			super(url);
		}
		
		public JavaScriptURLConnection(URL url, BrowsingContext sourceContext, BrowsingContext destinationContext) {
			super(url);
			
			this.sourceContext = sourceContext;
			this.destinationContext = destinationContext;
		}
		
		@Override
		public void connect() throws IOException {
			if (sourceContext != null && destinationContext != null) {				
				fetchTask = new JavascriptFetchTask(destinationContext);
				destinationContext.getEventLoop().queueTask(fetchTask);
			}
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			waitFetchTask();
			return is;
		}

		@Override
		public void disconnect() {
		}

		@Override
		public boolean usingProxy() {
			return false;
		}
		
		@Override
		public String getContentType() {
			return "text/html";
		}
		
		@Override
		public Object getContent() throws IOException {
			waitFetchTask();
			return result;
		}
		
		/*
		 * Waits until fetch ends.
		 */
		private void waitFetchTask() {
			Thread currentThread = Thread.currentThread();
			Thread eventThread = (destinationContext != null)? destinationContext.getEventLoop().getEventThread() : null;
			if (fetchTask != null && destinationContext != null && currentThread != eventThread) {
				try {
					fetchTask.join();
				} catch (InterruptedException e) {
					throw new WrappedException(e);
				}
			}
		}
	}
}
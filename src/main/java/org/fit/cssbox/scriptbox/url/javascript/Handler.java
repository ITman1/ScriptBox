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
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScript;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.mozilla.javascript.Undefined;

public class Handler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new JavaScriptURLConnection(url);
	}
	
	public static class JavaScriptURLConnection extends HttpURLConnection {
		class JavascriptFetchTask extends Task {

			public JavascriptFetchTask(BrowsingContext browsingContext) {
				super(TaskSource.DOM_MANIPULATION, browsingContext);
			}

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
				WindowScript script = new WindowScript(source, address, JavaScriptEngine.JAVASCRIPT_LANGUAGE, destinationScriptSettings, true);
				
				result = script.getResult();
				exception = script.getException();

				processResults();
			}
			
			protected void processResults() {
				if (exception != null) {
					result = Undefined.instance;
				}
				
				if (result == Undefined.instance) {
					responseCode = 204;
				} else {
					responseCode = 200;
				}
				
				StringBuilder resultStringBuilder = new StringBuilder();
				resultStringBuilder.append("<!DOCTYPE HTML>\n");
				resultStringBuilder.append("<html>\n");
				resultStringBuilder.append("\t<head></head>\n");
				resultStringBuilder.append("\t<body>");
				resultStringBuilder.append(result.toString());
				resultStringBuilder.append("</body>\n");
				resultStringBuilder.append("</html>\n");

				String resultString = resultStringBuilder.toString();
				Reader reader = new StringReader(resultString);
				is = new ReaderInputStream(reader);
			} 
			
		}
		
		protected BrowsingContext sourceContext;
		protected BrowsingContext destinationContext;
		
		protected JavascriptFetchTask fetchTask;
		protected Object result;
		protected Exception exception;
		protected InputStream is;
		
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
		
		protected void waitFetchTask() {
			Thread currentThread = Thread.currentThread();
			Thread eventThread = (destinationContext != null)? destinationContext.getEventLoop().getEventThread() : null;
			if (fetchTask != null && destinationContext != null && currentThread != eventThread) {
				try {
					fetchTask.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
					throw new UnknownException(e);
				}
			}
		}
	}
}
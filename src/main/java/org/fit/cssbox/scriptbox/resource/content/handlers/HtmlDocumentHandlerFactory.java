/**
 * HtmlDocumentHandlerFactory.java
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

package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.document.script.ParserFinishedTask;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocumentParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Executable;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.exceptions.TaskAbortedException;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.RenderedContentHandler;

public class HtmlDocumentHandlerFactory extends ContentHandlerFactory {
	private class HtmlDocumentHandler extends RenderedContentHandler {

		public HtmlDocumentHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}

		private class ParseDocumentFinishedTask extends ParserFinishedTask {			
			public ParseDocumentFinishedTask(Html5DocumentImpl document) {
				super(TaskSource.NETWORKING, document);
			}

			@Override
			public void execute() throws InterruptedException {
				if (exception != null) {
					navigationAttempt.cancel();
				} else {
					navigationAttempt.complete();
				}
			}
		}
		
		private class ParseDocumentExecutable implements Executable {			

			private Html5DocumentImpl document;
			private ScriptableDocumentParser scripDomParser;
			private Resource resource;
			
			public ParseDocumentExecutable(Html5DocumentImpl document, ScriptableDocumentParser scripDomParser, Resource resource) {
				this.document = document;
				this.scripDomParser = scripDomParser;
				this.resource = resource;
			}
			
			@Override
			public void execute() throws TaskAbortedException, InterruptedException {
				InputStream is = resource.getInputStream();
				scripDomParser.parse(document, is, new ParseDocumentFinishedTask(document));
			}
		}
		
		private class ParseDocumentTask extends Task {			
			private Resource resource;
			
			public ParseDocumentTask(Resource resource) {
				super(TaskSource.NETWORKING, resource.getBrowsingContext());
				
				this.resource = resource;
			}

			@Override
			public void execute() throws InterruptedException, TaskAbortedException {
				String encoding = resource.getContentEncoding();
				URL address = (resource.getOverrideAddress() != null)? resource.getOverrideAddress() : resource.getAddress();
				
				final ScriptableDocumentParser scripDomParser = new ScriptableDocumentParser(encoding);
				Html5DocumentImpl document = createDocument(resource.getBrowsingContext(), address, "text/html", scripDomParser);
				updateSessionHistory(document);
				
				/* We have to spin first, wait until session is updated, otherwise script execution would fail - it has to have active document this*/
				getEventLoop().spin(new ParseDocumentExecutable(document, scripDomParser, resource));
			}
		}
		
		@Override
		public void process(Resource resource) {
			context.getEventLoop().queueTask(new ParseDocumentTask(resource));
		}

	}
	
    private static List<String> mimeTypes;
	
    static {       
        mimeTypes = new ArrayList<String>(1);
        mimeTypes.add("text/html");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
    
	@Override
	public ContentHandler getContentHandler(NavigationAttempt navigationAttempt) {
		return new HtmlDocumentHandler(navigationAttempt);
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}

}



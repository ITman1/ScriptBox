package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.document.script.ScriptDOMParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl.DocumentReadiness;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.RenderedContentHandler;
import org.xml.sax.InputSource;

public class HtmlDocumentHandlerFactory extends ContentHandlerFactory {
	private class HtmlDocumentHandler extends RenderedContentHandler {

		public HtmlDocumentHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}

		private class ParseDocumentFinishedTask extends Task {
			protected Exception exception;
			
			public ParseDocumentFinishedTask(Resource resource, Exception exception) {
				super(TaskSource.NETWORKING, resource.getBrowsingContext());
				
				this.exception = exception;
			}

			@Override
			public void execute() throws InterruptedException {
				if (exception != null) {
					// TODO: Throw/display error
				} else {
					// TODO: See: http://www.w3.org/html/wg/drafts/html/CR/syntax.html#the-end
				}
			}
		}
		
		private class ParseDocumentTask extends Task {
			private Resource resource;
			
			public ParseDocumentTask(Resource resource) {
				super(TaskSource.NETWORKING, resource.getBrowsingContext());
				
				this.resource = resource;
			}

			@Override
			public void execute() throws InterruptedException {
				final Html5DocumentImpl document = createDocument(resource.getBrowsingContext(), resource.getAddress(), "text/html");
				updateSessionHistory(document);
				
				Exception exception = null;
				ScriptDOMParser scripDomParser = new ScriptDOMParser(document);
								
				try {
					document.setDocumentReadiness(DocumentReadiness.LOADING);
					scripDomParser.parse(new InputSource(resource.getInputStream()));
				} catch (Exception e) {
					exception = e;
					e.printStackTrace();
				}
				
				document.setDocumentReadiness(DocumentReadiness.COMPLETE);
				context.getEventLoop().queueTask(new ParseDocumentFinishedTask(resource, exception));
				
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



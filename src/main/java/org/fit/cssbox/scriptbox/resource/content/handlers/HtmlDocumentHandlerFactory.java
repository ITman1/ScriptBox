package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptDOMParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.RenderedContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlDocumentHandlerFactory extends ContentHandlerFactory {
	private class HtmlDocumentHandler extends RenderedContentHandler {

		public HtmlDocumentHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}

		private class ParseDocumentTask extends Task {
			private Resource resource;
			
			public ParseDocumentTask(Resource resource) {
				super(TaskSource.NETWORKING, resource.getBrowsingContext());
				
				this.resource = resource;
			}

			@Override
			public void execute() {
				Html5DocumentImpl document = createDocument(resource.getBrowsingContext(), resource.getAddress());
				
				ScriptDOMParser scripDomParser = new ScriptDOMParser(document);
				try {
					scripDomParser.parse(new InputSource(resource.getInputStream()));
				} catch (SAXException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		@Override
		public void process(Resource resource) {
			BrowsingContext context = resource.getBrowsingContext();
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



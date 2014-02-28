package org.fit.cssbox.scriptbox.resource.handlers;

import java.io.IOException;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptDOMParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.events.TaskSource;
import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class HtmlDocumentHandler extends RenderedResourceHandler {

	public HtmlDocumentHandler(NavigationAttempt navigationAttempt) {
		super(navigationAttempt);
	}

	private class ParseDocumentTask extends Task {
		private Resource resource;
		
		public ParseDocumentTask(Resource resource) {
			super(TaskSource.NETWORKING, resource.getContext());
			
			this.resource = resource;
		}

		@Override
		public void execute() {
			Html5DocumentImpl document = createDocument(resource.getContext(), resource.getUrlConnection().getURL());
			
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
		BrowsingContext context = resource.getContext();
		context.getEventLoop().queueTask(new ParseDocumentTask(resource));
	}

}

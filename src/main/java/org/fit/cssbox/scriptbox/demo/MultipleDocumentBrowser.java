package org.fit.cssbox.scriptbox.demo;

import java.io.IOException;

import org.apache.html.dom.HTMLDocumentImpl;
import org.fit.cssbox.io.DefaultDocumentSource;
import org.fit.cssbox.scriptbox.document.event.EventDOMSource;
import org.fit.cssbox.scriptbox.document.script.ScriptDOMSource;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;
import org.mozilla.javascript.Script;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.xml.sax.SAXException;

public class MultipleDocumentBrowser {

	static protected EventListener domEventListener = new EventListener() {

		@Override
		public void handleEvent(Event event) {
			if (event.getType().equals("DOMNodeRemoved")) {
				return;
			} else if (event.getType().equals("DOMNodeInserted")) {
				return;
			}
		}
	};
	
	public static void main(String[] args) {
		//SwingBrowser.main(args);
		DefaultDocumentSource docSource = null;
		try {
			docSource = new DefaultDocumentSource("http://loskot.ign.cz/dip/simple_script.html");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ScriptDOMSource eventDOMSource = new ScriptDOMSource(docSource, true);
		ScriptableDocument document = null;
		
		eventDOMSource.addDocumentEventListener("DOMNodeRemoved", domEventListener, false);
		eventDOMSource.addDocumentEventListener("DOMNodeInserted", domEventListener, false);
		
		try {
			document = (ScriptableDocument)eventDOMSource.parse();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element paragraph = document.createElement("p");
		Element script = document.getElementById("first");
		script.getParentNode().appendChild(paragraph);

		
		return;
	}

}

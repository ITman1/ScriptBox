package org.fit.cssbox.scriptbox.demo;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;

public class MultipleDocumentBrowser {

	public static void main(String[] args) {
		UserAgent userAgent = new UserAgent();
		BrowsingUnit browsingUnit = userAgent.createBrowsingUnit();
		browsingUnit.navigate("http://cssbox.sourceforge.net/");

	}
	
	/*static protected EventListener domEventListener = new EventListener() {

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
		Html5DocumentImpl document = null;
		
		eventDOMSource.addDocumentEventListener("DOMNodeRemoved", domEventListener, false);
		eventDOMSource.addDocumentEventListener("DOMNodeInserted", domEventListener, false);
		
		try {
			document = (Html5DocumentImpl)eventDOMSource.parse();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element paragraph = document.createElement("p");
		Element script = document.getElementById("first");
		script.getParentNode().appendChild(paragraph);

		
		return;
	}*/

}

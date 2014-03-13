package org.fit.cssbox.scriptbox.demo;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.window.WindowScriptEngineFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class MultipleDocumentBrowser {
	
	public static void main(String[] args) throws Exception  {
		UserAgent userAgent = new UserAgent();
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		//browsingUnit.navigate("http://cssbox.sourceforge.net/");
		browsingUnit.navigate("http://www.stud.fit.vutbr.cz/~xlosko01/scriptbox/print.html");
		
		Object obj = new Object();
		synchronized (obj) {
			try {
				obj.wait(5000); // Just some time until navigation completes
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Html5DocumentImpl doc = browsingUnit.getWindowBrowsingContext().getActiveDocument();
		Window window = doc.getWindow();
		WindowScriptSettings settings = window.getScriptSettings();
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();    
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
		LSSerializer serializer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(System.out);
        serializer.write(doc, output);
        
        
        ScriptEngine engine = new WindowScriptEngineFactory().getBrowserScriptEngine(settings);
        engine.eval("debug('============================================================='); debug(this['foo']);");
        
        userAgent.stop();
        
		return;
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

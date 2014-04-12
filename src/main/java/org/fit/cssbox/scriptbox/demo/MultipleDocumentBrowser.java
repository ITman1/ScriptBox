/**
 * MultipleDocumentBrowser.java
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

package org.fit.cssbox.scriptbox.demo;

import javax.script.ScriptEngine;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.javascript.window.WindowScriptEngineFactory;

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

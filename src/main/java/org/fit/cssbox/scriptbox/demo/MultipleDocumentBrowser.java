package org.fit.cssbox.scriptbox.demo;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.javascript.WindowScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.WindowScriptEngineFactory;
import org.mozilla.javascript.Wrapper;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class MultipleDocumentBrowser {

	public static void main(String[] args) {
		Window window = new Window(null);
		WindowScriptSettings settings = new WindowScriptSettings(window);
        ScriptEngine engine = new WindowScriptEngineFactory().getBrowserScriptEngine(settings);
        
		//ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
		
       /* try {

		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
        // evaluate JavaScript code from String
        //engine.eval("print('Hello, World')");
        
     // JavaScript code in a String
        
        
        String script = "function run() { println('run called'); }";
        
        // evaluate script
        try {
			engine.eval("var variable = java.lang.String('\\nHello, World\\n'); debug(variable); java.lang.System.out.println('test');");
			engine.eval(script);
			engine.eval("debug(variable.isEmpty());");
			engine.eval("debug(com);");
			engine.eval("debug(this);");
			engine.eval("debug(this.prototype);");
			/*Object test = engine.get("test");
			Object com = engine.get("com");
			System.err.println(test);
			System.err.println(com);*/
			engine.eval("variable = 'Hello 2!';");
			Object variable = engine.get("variable");
			System.err.println(variable);
			if (!(variable instanceof String)) {
				System.err.println("VARIABLE NOT STRING");
			}
			
			if (variable instanceof Wrapper) {
				System.err.println(((Wrapper)variable).unwrap());
			}
			engine.put("window", window);
			engine.put("retezec", new String("retezec_hodnota"));
			Object windowJS = engine.get("window");
			System.err.println(windowJS);
			engine.eval("debug(window.test);");
			engine.eval("debug(retezec);");
			engine.eval("debug(retezec.indexOf('h'));");
			engine.eval("debug(this == window);");
			UserAgent userAgent = new UserAgent();
			BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
			engine.put("browsingUnit", browsingUnit);
			engine.eval("debug(browsingUnit.userAgent);");
			engine.eval("hostFunction()");
        	engine.eval("var propValue;" +
			"for(var propName in this) {" +
			"    nldebug(propName + ';');" +
			"}");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        /*Invocable inv = (Invocable) engine;

        // get Runnable interface object from engine. This interface methods
        // are implemented by script functions with the matching name.
        Runnable r = inv.getInterface(Runnable.class);

        // start a new thread that runs the script implemented
        // runnable interface
        Thread th = new Thread(r);
        th.start();*/

	}
	
	/*public static void main(String[] args) throws Exception  {
		UserAgent userAgent = new UserAgent();
		BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
		//browsingUnit.navigate("http://cssbox.sourceforge.net/");
		browsingUnit.navigate("http://www.stud.fit.vutbr.cz/~xlosko01/scriptbox/print.html");
		
		Object obj = new Object();
		synchronized (obj) {
			try {
				obj.wait(3000); // Just some time until navigation completes
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		Html5DocumentImpl doc = browsingUnit.getWindowBrowsingContext().getActiveDocument();
		DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();    
		DOMImplementationLS impl = (DOMImplementationLS) registry.getDOMImplementation("XML 3.0 LS 3.0");
		LSSerializer serializer = impl.createLSSerializer();
        LSOutput output = impl.createLSOutput();
        output.setEncoding("UTF-8");
        output.setByteStream(System.out);
        serializer.write(doc, output);
        
        userAgent.stop();
        
		return;
	}*/
	
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

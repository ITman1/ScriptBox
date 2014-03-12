package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.mozilla.javascript.annotations.JSGetter;

public class Window extends AbstractWindow {

	protected Html5DocumentImpl _document;
	protected WindowScriptSettings _scriptSettings;
	
	public Window(Html5DocumentImpl document) {
		super(document);
		_document = document;
		_scriptSettings = new WindowScriptSettings(this);
		// TODO Auto-generated constructor stub
	}
	
	public Html5DocumentImpl getDocumentImpl() {
		return _document;
	}
	
    @ScriptGetter()
	public WindowScriptSettings getScriptSettings() {
		return _scriptSettings;
	}

	@Override
	protected void close() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void stop() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void focus() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void blur() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected WindowProxy open(String url, String target, String features,
			boolean replace) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected WindowProxy getter(long index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object getter(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @ScriptGetter
    public String getTest() {
        return "WINDOW GETTER";
    }
    
    @ScriptFunction
    public int soucet(int arg1, int arg2) {
    	return arg1 + arg2;
    }
    
    @ScriptFunction
    public int soucet(int arg1, int arg2, int arg3) {
    	return arg1 + arg2 + arg3;
    }
    
    @ScriptFunction
    public boolean objectTest(Window window) {
    	return window == this;
    }
    
    public class ShutterTestClass {
    	@ScriptFunction
    	public void visibleMethod() {
    		return;
    	}
    	
    	public void secretMethod(int a, int b) {
    		return;
    	}
    	
    	public Object getMethod() {
    		return null;
    	}
    	
    }
    
    @ScriptFunction
    public ShutterTestClass testShutter() {
    	return new ShutterTestClass();
    }

}

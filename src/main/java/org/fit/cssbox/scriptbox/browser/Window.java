package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

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

}

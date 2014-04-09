package org.fit.cssbox.scriptbox.history;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public class History {
	
	/*readonly attribute*/
	protected long length;
	
	/*readonly attribute*/ 
	protected Object state;
	
	private BrowsingContext context;
	
	public History(BrowsingContext context) {
		this.context = context;
	}
	
	public void go(long delta) {
		
	}
	
	public void back() {
		
	}
	
	public void forward() {
		
	}
		
	public void pushState(Object data, /*DOMString*/ String title, /*DOMString*/ String url) {
		
	}

	public void pushState(Object data, /*DOMString*/ String title) {
		
	}
	
	public void replaceState(Object data, /*DOMString*/ String title, /*DOMString*/ String url) {
		
	}
	
	public void replaceState(Object data, /*DOMString*/ String title) {
		
	}
	
	@Override
	public String toString() {
		return "[object History]";
	}
}

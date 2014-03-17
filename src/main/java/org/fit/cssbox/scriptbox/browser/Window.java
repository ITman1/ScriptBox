package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectGetter;
import org.fit.cssbox.scriptbox.ui.bars.BarProp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Window implements ObjectGetter {
	final public static String DEFAULT_TARGET =  "_blank";
	final public static String DEFAULT_FEATURES = "";
	final public static boolean DEFAULT_REAPLACE = false;
	
	protected Html5DocumentImpl documentImpl;
	protected WindowScriptSettings scriptSettings;
	protected BrowsingContext context;
	
	/* The current browsing context */
	
	protected WindowProxy window;
	protected WindowProxy self;
	protected Document document;
	protected String name;
	protected Location location;
	protected History history;
	
	protected BarProp locationbar;
	protected BarProp menubar;
	protected BarProp personalbar;
	protected BarProp scrollbars;
	protected BarProp statusbar;
	protected BarProp toolbar;
	
	protected String status;
	protected boolean closed;
	
	/* Other browsing contexts */
	
	protected WindowProxy frames;
	protected long length;
	protected WindowProxy top;
	protected WindowProxy opener;
	protected WindowProxy parent;
	protected Element frameElement;
	
	// TODO: Change visibility to protected, now for test only
	public Window() {
	}
	
	public Window(Html5DocumentImpl document) {
		// Implementation specific and auxiliary properties 
		this.documentImpl = document;
		this.scriptSettings = new WindowScriptSettings(this);
		this.context = document.getBrowsingContext();
		
		this.window = new WindowProxy(this.context);
		this.self = this.window;
		this.document = document;
	}
	
	public Html5DocumentImpl getDocumentImpl() {
		return documentImpl;
	}
	
	public WindowScriptSettings getScriptSettings() {
		return scriptSettings;
	}
			
	@ScriptGetter
	public WindowProxy getWindow() {
		return window;
	}
	
	@ScriptGetter
	public WindowProxy getSelf() {
		return self;
	}

	@ScriptGetter
	public Document getDocument() {
		return document;
	}

	@ScriptGetter
	public String getName() {
		return context.getName();
	}
	
	@ScriptSetter
	public void setName(String name) {
		context.setName(name);
	}

	@ScriptGetter
	public Location getLocation() {
		return context.getLocation();
	}

	@ScriptGetter
	public History getHistory() {
		return context.getHistory();
	}

	@ScriptGetter
	public BarProp getLocationbar() {
		return locationbar;
	}

	@ScriptGetter
	public BarProp getMenubar() {
		return menubar;
	}

	@ScriptGetter
	public BarProp getPersonalbar() {
		return personalbar;
	}

	@ScriptGetter
	public BarProp getScrollbars() {
		return scrollbars;
	}

	@ScriptGetter
	public BarProp getStatusbar() {
		return statusbar;
	}

	@ScriptGetter
	public BarProp getToolbar() {
		return toolbar;
	}

	@ScriptGetter
	public String getStatus() {
		return status;
	}

	@ScriptGetter
	public boolean getClosed() {
		return closed;
	}

	@ScriptGetter
	public WindowProxy getFrames() {
		return frames;
	}

	@ScriptGetter
	public long getLength() {
		return length;
	}

	@ScriptGetter
	public WindowProxy getTop() {
		return top;
	}

	@ScriptGetter
	public WindowProxy getOpener() {
		return opener;
	}

	@ScriptGetter
	public WindowProxy getParent() {
		return parent;
	}

	@ScriptGetter
	public Element getFrameElement() {
		return frameElement;
	}
	
	@ScriptFunction
	public void close() {
		throw new UnsupportedOperationException("Method close() has not been implemented yet!");
	}
	
	@ScriptFunction
	public void stop() {
		throw new UnsupportedOperationException("Method stop() has not been implemented yet!");
	}
	
	@ScriptFunction
	public void focus() {
		throw new UnsupportedOperationException("Method focus() has not been implemented yet!");
	}
	
	@ScriptFunction
	public void blur() {
		throw new UnsupportedOperationException("Method close() has not been implemented yet!");
	}
	
	@ScriptFunction
	public WindowProxy open(String url, String target, String features, boolean replace) {
		throw new UnsupportedOperationException("Method close() has not been implemented yet!");
	}
	
	@ScriptFunction
	public WindowProxy open() {
		return open(Html5DocumentImpl.DEFAULT_URL_ADDRESS, DEFAULT_TARGET, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	@ScriptFunction
	public WindowProxy open(String url) {
		return open(url, DEFAULT_TARGET, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	@ScriptFunction
	public WindowProxy open(String url, String target) {
		return open(url, target, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	@ScriptFunction
	public WindowProxy open(String url, String target, String features) {
		return open(url, target, features, DEFAULT_REAPLACE);
	}

	@ScriptFunction
	@Override
	public Object get(Object arg) {
		if (arg.equals("foo")) {
			return "bar";
		}
		
		return ObjectGetter.UNDEFINED_VALUE;
	}
	
	/*
	protected WindowProxy getter(long index);
	protected Object getter(String name);
	*/
	
/*
[Global]
interface Window : EventTarget {
TODO:
  // the user agent
  readonly attribute Navigator navigator; 
  readonly attribute External external;
  readonly attribute ApplicationCache applicationCache;

  // user prompts
  void alert(optional DOMString message = "");
  boolean confirm(optional DOMString message = "");
  DOMString? prompt(optional DOMString message = "", optional DOMString default = "");
  void print();
  any showModalDialog(DOMString url, optional any argument);


};
*/
}

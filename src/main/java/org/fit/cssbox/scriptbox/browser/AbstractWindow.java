package org.fit.cssbox.scriptbox.browser;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.history.Location;
import org.fit.cssbox.scriptbox.ui.bars.BarProp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class AbstractWindow {
	final public static String DEFAULT_TARGET =  "_blank";
	final public static String DEFAULT_FEATURES = "";
	final public static boolean DEFAULT_REAPLACE = false;
		
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
	
	protected abstract void close();
	protected abstract void stop();
	protected abstract void focus();
	protected abstract void blur();
	
	/* Other browsing contexts */
	protected WindowProxy frames;
	protected long length;
	protected WindowProxy top;
	protected WindowProxy opener;
	protected WindowProxy parent;
	protected Element frameElement;
	
	public AbstractWindow(Html5DocumentImpl document) {
		this.document = document;
	}
	
	protected abstract WindowProxy open(String url, String target, String features, boolean replace);
	protected abstract WindowProxy getter(long index);
	protected abstract Object getter(String name);
	
	protected WindowProxy open() {
		return open(Html5DocumentImpl.DEFAULT_URL_ADDRESS, DEFAULT_TARGET, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	protected WindowProxy open(String url) {
		return open(url, DEFAULT_TARGET, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	protected WindowProxy open(String url, String target) {
		return open(url, target, DEFAULT_FEATURES, DEFAULT_REAPLACE);
	}
	
	protected WindowProxy open(String url, String target, String features) {
		return open(url, target, features, DEFAULT_REAPLACE);
	}
	
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
Window implements GlobalEventHandlers;
Window implements WindowEventHandlers;
 */
}

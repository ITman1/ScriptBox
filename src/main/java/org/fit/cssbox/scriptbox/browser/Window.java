package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.xerces.dom.NodeImpl;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.dom.events.GlobalEventHandlers;
import org.fit.cssbox.scriptbox.dom.events.WindowEventHandlers;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.ui.bars.BarProp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

public class Window implements ObjectGetter, EventTarget, GlobalEventHandlers, WindowEventHandlers {
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
		
		this.window = this.context.getWindowProxy();
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
	public WindowProxy open(String url, String target, String features, boolean replace) {
		BrowsingUnit browsingUnit = context.getBrowsingUnit();
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		URL targetUrl = null;
		
		//If the first argument is the empty string, then the url argument must be interpreted as "about:blank". 
		if (url == null || url.isEmpty()) {
			targetUrl = Html5DocumentImpl.DEFAULT_URL;
		} else {
			//The first argument, url, must be a valid non-empty URL
			try {
				ScriptSettings<?> settings = stack.getEntryScriptSettings();
				URL baseUrl = (settings != null)? settings.getBaseUrl() : null;
				targetUrl = (baseUrl != null)? new URL(baseUrl, url) : new URL(url);
			} catch (MalformedURLException e) {
			}
		}
		
		//It must be a valid browsing context name or keyword.
		if (!context.isValidBrowsingContextName(target)) {
			return null;
		}
		
		//the user agent must first select a browsing context 
		boolean isTargetBlank = context.isBlankBrowsingContext(target);
		BrowsingContext targetContext = context.chooseBrowsingContextByName(target);
		
		//then throw an InvalidAccessError exception 
		if (targetContext == null) {
			throw new DOMException(DOMException.INVALID_ACCESS_ERR, "InvalidAccessError");
		}
		
		/*
		 * TODO?:
		 * If the resolve a URL algorithm failed, then the user agent may either instead navigate to an inline 
		 * error page, with exceptions enabled and using the same replacement behavior and source browsing context 
		 * behavior as described earlier in this paragraph; or treat the url as "about:blank".
		 */
		
		if (targetUrl == null) {
			
		} 
		//if url is not "about:blank", the user agent must navigate 
		else if (!targetUrl.equals(Html5DocumentImpl.DEFAULT_URL)) {
			//If the replace is true or if the browsing context was just created as part of the rules for choosing a browsing context 
			replace = replace || isTargetBlank;
			NavigationController navigationController = targetContext.getNavigationController();
			//The navigation must be done with the responsible browsing context specified by the incumbent settings object 
			ScriptSettings<?> settings = stack.getIncumbentScriptSettings();
			BrowsingContext sourceBrowsingContext = settings.getResposibleBrowsingContext();
			navigationController.navigate(sourceBrowsingContext, targetUrl, true, false, replace);
		} else {
			
		}
		
		//The method must return the WindowProxy object of the browsing context that was navigated, or null
		return targetContext.getWindowProxy();
	}

	@ScriptFunction
	@Override
	public Object get(Object arg) {
		if (arg.equals("foo")) {
			return "bar";
		}
		
		return ObjectGetter.UNDEFINED_VALUE;
	}

	@Override
	public void addEventListener(String type, EventListener listener,
			boolean useCapture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeEventListener(String type, EventListener listener,
			boolean useCapture) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean dispatchEvent(Event evt) {
		// TODO Auto-generated method stub
		return false;
	}
	
	protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
	}

	protected void removeEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
	}

	protected boolean dispatchEvent(NodeImpl node, Event event) {
		return false;
	}

	@Override
	public EventHandler getOnafterprint() {
		return null;
	}

	@Override
	public EventHandler getOnbeforeprint() {
		return null;
	}

	@Override
	public EventHandler getOnhashchange() {
		return null;
	}

	@Override
	public EventHandler getOnmessage() {
		return null;
	}

	@Override
	public EventHandler getOnoffline() {
		return null;
	}

	@Override
	public EventHandler getOnonline() {
		return null;
	}

	@Override
	public EventHandler getOnpagehide() {
		return null;
	}

	@Override
	public EventHandler getOnpageshow() {
		return null;
	}

	@Override
	public EventHandler getOnpopstate() {
		return null;
	}

	@Override
	public EventHandler getOnstorage() {
		return null;
	}

	@Override
	public EventHandler getOnunload() {
		return null;
	}

	@Override
	public void setOnafterprint(EventHandler handler) {		
	}

	@Override
	public void setOnbeforeprint(EventHandler handler) {
	}

	@Override
	public void setOnhashchange(EventHandler handler) {
	}

	@Override
	public void setOnmessage(EventHandler handler) {
	}

	@Override
	public void setOnoffline(EventHandler handler) {
	}

	@Override
	public void setOnonline(EventHandler handler) {
	}

	@Override
	public void setOnpagehide(EventHandler handler) {
	}

	@Override
	public void setOnpageshow(EventHandler handler) {
	}

	@Override
	public void setOnpopstate(EventHandler handler) {
	}

	@Override
	public void setOnstorage(EventHandler handler) {
	}

	@Override
	public void setOnunload(EventHandler handler) {
	}

	@Override
	public EventHandler getOnabort() {
		return null;
	}

	@Override
	public EventHandler getOnblur() {
		return null;
	}

	@Override
	public EventHandler getOncancel() {
		return null;
	}

	@Override
	public EventHandler getOncanplay() {
		return null;
	}

	@Override
	public EventHandler getOncanplaythrough() {
		return null;
	}

	@Override
	public EventHandler getOnchange() {
		return null;
	}

	@Override
	public EventHandler getOnclick() {
		return null;
	}

	@Override
	public EventHandler getOnclose() {
		return null;
	}

	@Override
	public EventHandler getOncuechange() {
		return null;
	}

	@Override
	public EventHandler getOndblclick() {
		return null;
	}

	@Override
	public EventHandler getOndrag() {
		return null;
	}

	@Override
	public EventHandler getOndragend() {
		return null;
	}

	@Override
	public EventHandler getOndragenter() {
		return null;
	}

	@Override
	public EventHandler getOndragexit() {
		return null;
	}

	@Override
	public EventHandler getOndragleave() {
		return null;
	}

	@Override
	public EventHandler getOndragover() {
		return null;
	}

	@Override
	public EventHandler getOndragstart() {
		return null;
	}

	@Override
	public EventHandler getOndrop() {
		return null;
	}

	@Override
	public EventHandler getOndurationchange() {
		return null;
	}

	@Override
	public EventHandler getOnemptied() {
		return null;
	}

	@Override
	public EventHandler getOnended() {
		return null;
	}

	@Override
	public EventHandler getOnfocus() {
		return null;
	}

	@Override
	public EventHandler getOninput() {
		return null;
	}

	@Override
	public EventHandler getOninvalid() {
		return null;
	}

	@Override
	public EventHandler getOnkeydown() {
		return null;
	}

	@Override
	public EventHandler getOnkeypress() {
		return null;
	}

	@Override
	public EventHandler getOnkeyup() {
		return null;
	}

	@Override
	public EventHandler getOnload() {
		return null;
	}

	@Override
	public EventHandler getOnloadeddata() {
		return null;
	}

	@Override
	public EventHandler getOnloadedmetadata() {
		return null;
	}

	@Override
	public EventHandler getOnloadstart() {
		return null;
	}

	@Override
	public EventHandler getOnmousedown() {
		return null;
	}

	@Override
	public EventHandler getOnmouseenter() {
		return null;
	}

	@Override
	public EventHandler getOnmouseleave() {
		return null;
	}

	@Override
	public EventHandler getOnmousemove() {
		return null;
	}

	@Override
	public EventHandler getOnmouseout() {
		return null;
	}

	@Override
	public EventHandler getOnmouseover() {
		return null;
	}

	@Override
	public EventHandler getOnmouseup() {
		return null;
	}

	@Override
	public EventHandler getOnmousewheel() {
		return null;
	}

	@Override
	public EventHandler getOnpause() {
		return null;
	}

	@Override
	public EventHandler getOnplay() {
		return null;
	}

	@Override
	public EventHandler getOnplaying() {
		return null;
	}

	@Override
	public EventHandler getOnprogress() {
		return null;
	}

	@Override
	public EventHandler getOnratechange() {
		return null;
	}

	@Override
	public EventHandler getOnreset() {
		return null;
	}

	@Override
	public EventHandler getOnresize() {
		return null;
	}

	@Override
	public EventHandler getOnscroll() {
		return null;
	}

	@Override
	public EventHandler getOnseeked() {
		return null;
	}

	@Override
	public EventHandler getOnseeking() {
		return null;
	}

	@Override
	public EventHandler getOnselect() {
		return null;
	}

	@Override
	public EventHandler getOnshow() {
		return null;
	}

	@Override
	public EventHandler getOnstalled() {
		return null;
	}

	@Override
	public EventHandler getOnsubmit() {
		return null;
	}

	@Override
	public EventHandler getOnsuspend() {
		return null;
	}

	@Override
	public EventHandler getOntimeupdate() {
		return null;
	}

	@Override
	public EventHandler getOntoggle() {
		return null;
	}

	@Override
	public EventHandler getOnvolumechange() {
		return null;
	}

	@Override
	public EventHandler getOnwaiting() {
		return null;
	}

	@Override
	public void setOnabort(EventHandler handler) {
	}

	@Override
	public void setOnblur(EventHandler handler) {
	}

	@Override
	public void setOncancel(EventHandler handler) {
	}

	@Override
	public void setOncanplay(EventHandler handler) {
	}

	@Override
	public void setOncanplaythrough(EventHandler handler) {
	}

	@Override
	public void setOnchange(EventHandler handler) {
	}

	@Override
	public void setOnclick(EventHandler handler) {
	}

	@Override
	public void setOnclose(EventHandler handler) {
	}

	@Override
	public void setOncuechange(EventHandler handler) {
	}

	@Override
	public void setOndblclick(EventHandler handler) {
	}

	@Override
	public void setOndrag(EventHandler handler) {
	}

	@Override
	public void setOndragend(EventHandler handler) {
	}

	@Override
	public void setOndragenter(EventHandler handler) {
	}

	@Override
	public void setOndragexit(EventHandler handler) {
	}

	@Override
	public void setOndragleave(EventHandler handler) {
	}

	@Override
	public void setOndragover(EventHandler handler) {
	}

	@Override
	public void setOndragstart(EventHandler handler) {
	}

	@Override
	public void setOndrop(EventHandler handler) {
	}

	@Override
	public void setOndurationchange(EventHandler handler) {
	}

	@Override
	public void setOnemptied(EventHandler handler) {
	}

	@Override
	public void setOnended(EventHandler handler) {
	}

	@Override
	public void setOnfocus(EventHandler handler) {
	}

	@Override
	public void setOninput(EventHandler handler) {
	}

	@Override
	public void setOninvalid(EventHandler handler) {
	}

	@Override
	public void setOnkeydown(EventHandler handler) {
	}

	@Override
	public void setOnkeypress(EventHandler handler) {
	}

	@Override
	public void setOnkeyup(EventHandler handler) {
	}

	@Override
	public void setOnload(EventHandler handler) {
	}

	@Override
	public void setOnloadeddata(EventHandler handler) {
	}

	@Override
	public void setOnloadedmetadata(EventHandler handler) {
	}

	@Override
	public void setOnloadstart(EventHandler handler) {
	}

	@Override
	public void setOnmousedown(EventHandler handler) {
	}

	@Override
	public void setOnmouseenter(EventHandler handler) {
	}

	@Override
	public void setOnmouseleave(EventHandler handler) {
	}

	@Override
	public void setOnmousemove(EventHandler handler) {
	}

	@Override
	public void setOnmouseout(EventHandler handler) {
	}

	@Override
	public void setOnmouseover(EventHandler handler) {
	}

	@Override
	public void setOnmouseup(EventHandler handler) {
	}

	@Override
	public void setOnmousewheel(EventHandler handler) {
	}

	@Override
	public void setOnpause(EventHandler handler) {
	}

	@Override
	public void setOnplay(EventHandler handler) {
	}

	@Override
	public void setOnplaying(EventHandler handler) {
	}

	@Override
	public void setOnprogress(EventHandler handler) {
	}

	@Override
	public void setOnratechange(EventHandler handler) {
	}

	@Override
	public void setOnreset(EventHandler handler) {
	}

	@Override
	public void setOnresize(EventHandler handler) {
	}

	@Override
	public void setOnscroll(EventHandler handler) {
	}

	@Override
	public void setOnseeked(EventHandler handler) {
	}

	@Override
	public void setOnseeking(EventHandler handler) {
	}

	@Override
	public void setOnselect(EventHandler handler) {
	}

	@Override
	public void setOnshow(EventHandler handler) {
	}

	@Override
	public void setOnstalled(EventHandler handler) {
	}

	@Override
	public void setOnsubmit(EventHandler handler) {
	}

	@Override
	public void setOnsuspend(EventHandler handler) {
	}

	@Override
	public void setOntimeupdate(EventHandler handler) {
	}

	@Override
	public void setOntoggle(EventHandler handler) {
	}

	@Override
	public void setOnvolumechange(EventHandler handler) {
	}

	@Override
	public void setOnwaiting(EventHandler handler) {
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

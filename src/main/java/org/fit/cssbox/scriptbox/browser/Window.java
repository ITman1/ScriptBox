/**
 * Window.java
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

package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.DispatcherTask;
import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.dom.events.EventHandlerEventListener;
import org.fit.cssbox.scriptbox.dom.events.EventListenerEntry;
import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.dom.events.GlobalEventHandlers;
import org.fit.cssbox.scriptbox.dom.events.WindowEventHandlers;
import org.fit.cssbox.scriptbox.dom.events.script.TrustedEvent;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.ScriptSettingsStack;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public class Window implements ObjectGetter, EventTarget, GlobalEventHandlers, WindowEventHandlers, AbstractView {
	final public static String DEFAULT_TARGET =  "_blank";
	final public static String DEFAULT_FEATURES = "";
	final public static boolean DEFAULT_REAPLACE = false;
	
	protected Html5DocumentImpl documentImpl;
	protected BrowsingContext context;
	protected UserAgent userAgent;
	
	// FIXME: When it gets fire there should be copy for case that we remove listener while processing event
	protected Map<String, Vector<EventListenerEntry>> listeners;
	protected WindowScriptSettings scriptSettings;
			
	// Global event handler listeners
	protected EventHandlerEventListener onabortHandlerListener;
	protected EventHandlerEventListener onblurHandlerListener;
	protected EventHandlerEventListener oncancelHandlerListener;
	protected EventHandlerEventListener oncanplayHandlerListener;
	protected EventHandlerEventListener oncanplaythroughHandlerListener;
	protected EventHandlerEventListener onchangeHandlerListener;
	protected EventHandlerEventListener onclickHandlerListener;
	protected EventHandlerEventListener oncloseHandlerListener;
	protected EventHandlerEventListener oncuechangeHandlerListener;
	protected EventHandlerEventListener ondblclickHandlerListener;
	protected EventHandlerEventListener ondragHandlerListener;
	protected EventHandlerEventListener ondragendHandlerListener;
	protected EventHandlerEventListener ondragenterHandlerListener;
	protected EventHandlerEventListener ondragexitHandlerListener;
	protected EventHandlerEventListener ondragleaveHandlerListener;
	protected EventHandlerEventListener ondragoverHandlerListener;
	protected EventHandlerEventListener ondragstartHandlerListener;
	protected EventHandlerEventListener ondropHandlerListener;
	protected EventHandlerEventListener ondurationchangeHandlerListener;
	protected EventHandlerEventListener onemptiedHandlerListener;
	protected EventHandlerEventListener onendedHandlerListener;
	protected EventHandlerEventListener onfocusHandlerListener;
	protected EventHandlerEventListener oninputHandlerListener;
	protected EventHandlerEventListener oninvalidHandlerListener;
	protected EventHandlerEventListener onkeydownHandlerListener;
	protected EventHandlerEventListener onkeypressHandlerListener;
	protected EventHandlerEventListener onkeyupHandlerListener;
	protected EventHandlerEventListener onloadHandlerListener;
	protected EventHandlerEventListener onloadeddataHandlerListener;
	protected EventHandlerEventListener onloadedmetadataHandlerListener;
	protected EventHandlerEventListener onloadstartHandlerListener;
	protected EventHandlerEventListener onmousedownHandlerListener;
	protected EventHandlerEventListener onmouseenterHandlerListener;
	protected EventHandlerEventListener onmouseleaveHandlerListener;
	protected EventHandlerEventListener onmousemoveHandlerListener;
	protected EventHandlerEventListener onmouseoutHandlerListener;
	protected EventHandlerEventListener onmouseoverHandlerListener;
	protected EventHandlerEventListener onmouseupHandlerListener;
	protected EventHandlerEventListener onmousewheelHandlerListener;
	protected EventHandlerEventListener onpauseHandlerListener;
	protected EventHandlerEventListener onplayHandlerListener;
	protected EventHandlerEventListener onplayingHandlerListener;
	protected EventHandlerEventListener onprogressHandlerListener;
	protected EventHandlerEventListener onratechangeHandlerListener;
	protected EventHandlerEventListener onresetHandlerListener;
	protected EventHandlerEventListener onresizeHandlerListener;
	protected EventHandlerEventListener onscrollHandlerListener;
	protected EventHandlerEventListener onseekedHandlerListener;
	protected EventHandlerEventListener onseekingHandlerListener;
	protected EventHandlerEventListener onselectHandlerListener;
	protected EventHandlerEventListener onshowHandlerListener;
	protected EventHandlerEventListener onstalledHandlerListener;
	protected EventHandlerEventListener onsubmitHandlerListener;
	protected EventHandlerEventListener onsuspendHandlerListener;
	protected EventHandlerEventListener ontimeupdateHandlerListener;
	protected EventHandlerEventListener ontoggleHandlerListener;
	protected EventHandlerEventListener onvolumechangeHandlerListener;
	protected EventHandlerEventListener onwaitingHandlerListener;
	
	// Window handler listeners
	protected EventHandlerEventListener onafterprintHandlerListener;
	protected EventHandlerEventListener onbeforeprintHandlerListener;
	protected EventHandlerEventListener onhashchangeHandlerListener;
	protected EventHandlerEventListener onmessageHandlerListener;
	protected EventHandlerEventListener onofflineHandlerListener;
	protected EventHandlerEventListener ononlineHandlerListener;
	protected EventHandlerEventListener onpagehideHandlerListener;
	protected EventHandlerEventListener onpageshowHandlerListener;
	protected EventHandlerEventListener onpopstateHandlerListener;
	protected EventHandlerEventListener onstorageHandlerListener;
	protected EventHandlerEventListener onunloadHandlerListener;
	
	// TODO: Change visibility to protected, now for test only
	public Window() {
	}
	
	public Window(Html5DocumentImpl document) {
		setDocumentImpl(document);
		
		// Implementation specific and auxiliary properties 
		this.listeners = new HashMap<String, Vector<EventListenerEntry>>();
		this.scriptSettings = new WindowScriptSettings(this);
		
		// Global event handler listeners
		this.onabortHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onabort);
		this.onblurHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onblur);
		this.oncancelHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oncancel);
		this.oncanplayHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oncanplay);
		this.oncanplaythroughHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oncanplaythrough);
		this.onchangeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onchange);
		this.onclickHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onclick);
		this.oncloseHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onclose);
		this.oncuechangeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oncuechange);
		this.ondblclickHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondblclick);
		this.ondragHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondrag);
		this.ondragendHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragend);
		this.ondragenterHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragenter);
		this.ondragexitHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragexit);
		this.ondragleaveHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragleave);
		this.ondragoverHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragover);
		this.ondragstartHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondragstart);
		this.ondropHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondrop);
		this.ondurationchangeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ondurationchange);
		this.onemptiedHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onemptied);
		this.onendedHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onended);
		this.onfocusHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onfocus);
		this.oninputHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oninput);
		this.oninvalidHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.oninvalid);
		this.onkeydownHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onkeydown);
		this.onkeypressHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onkeypress);
		this.onkeyupHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onkeyup);
		this.onloadHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onload);
		this.onloadeddataHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onloadeddata);
		this.onloadedmetadataHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onloadedmetadata);
		this.onloadstartHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onloadstart);
		this.onmousedownHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmousedown);
		this.onmouseenterHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmouseenter);
		this.onmouseleaveHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmouseleave);
		this.onmousemoveHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmousemove);
		this.onmouseoutHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmouseout);
		this.onmouseoverHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmouseover);
		this.onmouseupHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmouseup);
		this.onmousewheelHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onmousewheel);
		this.onpauseHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onpause);
		this.onplayHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onplay);
		this.onplayingHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onplaying);
		this.onprogressHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onprogress);
		this.onratechangeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onratechange);
		this.onresetHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onreset);
		this.onresizeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onresize);
		this.onscrollHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onscroll);
		this.onseekedHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onseeked);
		this.onseekingHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onseeking);
		this.onselectHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onselect);
		this.onshowHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onshow);
		this.onstalledHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onstalled);
		this.onsubmitHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onsubmit);
		this.onsuspendHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onsuspend);
		this.ontimeupdateHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ontimeupdate);
		this.ontoggleHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.ontoggle);
		this.onvolumechangeHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onvolumechange);
		this.onwaitingHandlerListener = new EventHandlerEventListener(this, GlobalEventHandlers.onwaiting);
		
		// Window handler listeners
		this.onafterprintHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onafterprint);
		this.onbeforeprintHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onbeforeprint);
		this.onhashchangeHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onhashchange);
		this.onmessageHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onmessage);
		this.onofflineHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onoffline);
		this.ononlineHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.ononline);
		this.onpagehideHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onpagehide);
		this.onpageshowHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onpageshow);
		this.onpopstateHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onpopstate);
		this.onstorageHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onstorage);
		this.onunloadHandlerListener = new EventHandlerEventListener(this, WindowEventHandlers.onunload);
	}
	
	private List<IFrameBrowsingContext> getNestedFrames() {
		if (context instanceof IFrameContainerBrowsingContext) {
			IFrameContainerBrowsingContext iframeContainerContext = (IFrameContainerBrowsingContext)context;
			List<IFrameBrowsingContext> iframes = iframeContainerContext.getDocumentIframes(documentImpl);
			
			return iframes;
		}
		
		return IFrameContainerBrowsingContext.EMPTY_IFRAMES;
	}
	
	@Override
	public String toString() {
		return "[object Window]";
	}
	
	public Html5DocumentImpl getDocumentImpl() {
		return documentImpl;
	}
	
	public void setDocumentImpl(Html5DocumentImpl documentImpl) {
		this.documentImpl = documentImpl;
		this.context = documentImpl.getBrowsingContext();
		this.userAgent = context.getBrowsingUnit().getUserAgent();
	}
	
	public WindowScriptSettings getScriptSettings() {
		return scriptSettings;
	}
			
	public void dispatchEvent(Event event, org.w3c.dom.events.EventTarget target) {
		Task dispatcherTask = new DispatcherTask(documentImpl, target, event);
		context.getEventLoop().queueTask(dispatcherTask);
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#fire-a-simple-event
	 */
	public boolean fireSimpleEvent(String eventName, org.w3c.dom.events.EventTarget target) {
		return fireSimpleEvent(eventName, target, false, false);
	}
	
	public boolean fireSimpleEvent(String eventName, org.w3c.dom.events.EventTarget target, boolean bubbles, boolean cancelable) {
		EventImpl event = new EventImpl();
		event.initEvent(eventName, bubbles, cancelable);
		
		target.dispatchEvent(event);
		
		return event.stopPropagation;
	}
	
	public void dispatchSimpleEvent(String eventName, org.w3c.dom.events.EventTarget target) {
		dispatchSimpleEvent(eventName, target, false, false);
	}
	
	public void dispatchSimpleEvent(String eventName, org.w3c.dom.events.EventTarget target, boolean bubbles, boolean cancelable) {
		EventImpl event = new EventImpl();
		event.initEvent(eventName, bubbles, cancelable);
		
		target.dispatchEvent(event);
		
		dispatchEvent(event, target);
	}
	
	@ScriptGetter
	public WindowProxy getWindow() {
		return context.getWindowProxy();
	}
	
	@ScriptGetter
	public WindowProxy getSelf() {
		return context.getWindowProxy();
	}

	@ScriptGetter
	public DocumentView getDocument() {
		return documentImpl;
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
		return documentImpl.getHistory();
	}

	@ScriptGetter
	public BarProp getLocationbar() {
		return userAgent.getLocationbar();
	}

	@ScriptGetter
	public BarProp getMenubar() {
		return userAgent.getMenubar();
	}

	@ScriptGetter
	public BarProp getPersonalbar() {
		return userAgent.getPersonalbar();
	}

	@ScriptGetter
	public ScrollBarsProp getScrollbars() {
		return userAgent.getScrollbars();
	}

	@ScriptGetter
	public BarProp getStatusbar() {
		return userAgent.getStatusbar();
	}

	@ScriptGetter
	public BarProp getToolbar() {
		return userAgent.getToolbar();
	}

	@ScriptGetter
	public String getStatus() {
		// TODO: Implement.
		return null;
	}

	@ScriptGetter
	public boolean getClosed() {
		return context.isDiscarded();
	}

	@ScriptGetter
	public WindowProxy getFrames() {
		return context.getWindowProxy();
	}

	@ScriptGetter
	public long getLength() {
		/*
		 * if that Window's browsing context shares the same event loop as the responsible document specified
		 *  by the entry settings object accessing the IDL attribute; otherwise, it must return zero
		 */
		EventLoop windowEventLoop = context.getEventLoop();
		EventLoop resposinbleEventLoop = scriptSettings.getResponsibleDocument().getEventLoop();
		
		if (windowEventLoop.equals(resposinbleEventLoop)) {
			/* the number of child browsing contexts that are nested through 
			 * elements that are in the Document that is the active document of that Window object
			 */
			if (context instanceof IFrameContainerBrowsingContext) {
				IFrameContainerBrowsingContext iframeContainerContext = (IFrameContainerBrowsingContext)context;
				List<IFrameBrowsingContext> iframes = iframeContainerContext.getDocumentIframes(documentImpl);
				
				return iframes.size();
			}
		}
			
		return 0;
	}

	@ScriptGetter
	public WindowProxy getTop() {
		return context.getTopLevelContext().getWindowProxy();
	}

	@ScriptGetter
	public WindowProxy getOpener() {
		if (context instanceof AuxiliaryBrowsingContext) {
			return ((AuxiliaryBrowsingContext)context).getOpenerContext().getWindowProxy();
		}
		return null;
	}
	
	@ScriptGetter
	public WindowProxy getParent() {
		BrowsingContext parentContext = context.getParentContext();
		return (parentContext != null)? parentContext.getWindowProxy() : null;
	}

	@ScriptGetter
	public Element getFrameElement() {
		/* If d is not a Document in a nested browsing context, return null and abort these steps. */
		if (!context.isNestedBrowsingContext()) {
			return null;
		}
		
		/* If the browsing context container's Document does not have the same effective script 
		 * origin as the effective script origin specified by the entry settings object, then throw 
		 * a SecurityError exception and abort these steps. */
		BrowsingContext containerContext = context.getParentContext();
		Html5DocumentImpl containerDocument = containerContext.getActiveDocument();
		Origin<?> containerOrigin = containerDocument.getEffectiveScriptOrigin();
		Origin<?> scriptOrigin = scriptSettings.getEffectiveScriptOrigin();
		
		if (!containerOrigin.equals(scriptOrigin)) {
			throw new DOMException(DOMException.SECURITY_ERR, "SecurityError");
		}
		
		return context.getContainer();
	}
	
	@ScriptGetter
	public Navigator getNavigator() {
		// TODO Implement
		return null;
	}
	
	@ScriptGetter
	public External getExternal() {
		// TODO Implement
		return null;
	}
	
	@ScriptGetter
	public ApplicationCache getApplicationCache() {
		// TODO Implement
		return null;
	}
	
	@ScriptFunction
	public void close() {
		throw new UnsupportedOperationException("Method close() has not been implemented yet!");
	}
	
	@ScriptFunction
	public void stop() {
		if (!documentImpl.isUnloadRunning()) {
			NavigationController controller = context.getNavigationController();
			controller.cancelAllNavigationAttempts();
		}
		
		Html5DocumentImpl activeDocument = context.getActiveDocument();
		activeDocument.abort();
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

	// FIXME?: Everybody who gets proxy can scroll, is that correct? Should not be here check against origins or script setting stack?
	@ScriptFunction
	public void scroll(int xCoord, int yCoord) {
		ScrollBarsProp scrollBars = userAgent.getScrollbars();
		
		if (scrollBars != null) {
			scrollBars.scroll(xCoord, yCoord);
		}
	}
	
	@Override
	public Collection<Object> getKeys() {
		List<Object> keys = new ArrayList<Object>();
		List<IFrameBrowsingContext> frames = getNestedFrames();
		
		for (int i = 0; i < frames.size(); i++) {
			keys.add(i);
		}
		
		return keys;
	}
	
	@ScriptFunction
	@Override
	public Object get(Object arg) {
		long length = getLength();
		if (arg instanceof Integer && length > 0) {
			int index = (Integer)arg;
			List<IFrameBrowsingContext> frames = getNestedFrames();
			
			if (frames.size() > index) {
				return frames.get(index).getWindowProxy();
			}
		} else if (arg instanceof String) {
			
		}
		
		return ObjectGetter.UNDEFINED_VALUE;
	}

	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener) {
		addEventListener(type, listener, false);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener) {
		removeEventListener(type, listener, false);
	}
	
	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		Vector<EventListenerEntry> listenerEntries = null;
		if (listeners.containsKey(type)) {
			listenerEntries = listeners.get(type);
		} else {
			listenerEntries = new Vector<EventListenerEntry>();
			listeners.put(type, listenerEntries);
		}
		
		// Remove before adding
		removeEventListener(type, listener, useCapture);
		
		// Add new one
		EventListenerEntry newEntry = new EventListenerEntry(listener, useCapture);
		listenerEntries.add(newEntry);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		if (listeners.containsKey(type)) {
			Vector<EventListenerEntry> listenerEntries = listeners.get(type);
			EventListenerEntry entryToRemove = new EventListenerEntry(listener, useCapture);
			listenerEntries.remove(entryToRemove);
		}
	}

	@ScriptFunction
	@Override
	public boolean dispatchEvent(Event event) {
		if (!(event instanceof EventImpl)) {
			return false;
		}

		EventImpl evt = (EventImpl)event;

		if (!evt.initialized || evt.type == null || evt.type.length() == 0) {
			throw new DOMException(DOMException.INVALID_STATE_ERR, "InvalidStateError");
		}

		String evtType = evt.getType();
		if (!listeners.containsKey(evtType)) {
			return evt.preventDefault;
		}

		if (event instanceof TrustedEvent) {
			TrustedEvent trusted = (TrustedEvent)event ;
			EventTarget targetOverride = trusted.getTargetOverride();
			evt.target = (targetOverride != null)? targetOverride : this;
		} else {
			evt.target = this;
		}
		
		evt.stopPropagation = false;
		evt.preventDefault = false;
				
		// Window has no CAPTURING_PHASE - it is top!

		evt.eventPhase = Event.AT_TARGET;
		evt.currentTarget = this;
		Vector<EventListenerEntry> listenerEntries = listeners.get(evtType);
		if (listenerEntries != null && !listenerEntries.isEmpty()) {
			@SuppressWarnings("unchecked")
			Vector<EventListenerEntry> clonedListenerEntries = (Vector<EventListenerEntry>) listenerEntries.clone();
			for (EventListenerEntry listenerEntry : clonedListenerEntries) {
				if (evt.stopPropagation) {
					break;
				}
				if (!listenerEntry.useCapture && listenerEntry.listener != null) {
					listenerEntry.listener.handleEvent(evt);
				}
			}
		}
		
		// Window has no BUBBLE - it is top!
		// TODO?: DEFAULT PHASE

		return evt.preventDefault == false;
	}

	// Does not touches event object, everything is delagated to document
	public boolean dispatchEventFromDocument(EventImpl event) {
		String eventType = event.type;
		if (!listeners.containsKey(eventType)) {
			return event.preventDefault;
		}

		Vector<EventListenerEntry> listenerEntries = listeners.get(eventType);
		if (listenerEntries != null) {

			@SuppressWarnings("unchecked")
			Vector<EventListenerEntry> clonedListenerEntries = (Vector<EventListenerEntry>) listenerEntries.clone();
			
			/* Capturing phase */
			if (event.eventPhase == Event.CAPTURING_PHASE) {
				for (EventListenerEntry listenerEntry : clonedListenerEntries) {
					if (event.stopPropagation) {
						break;
					}
					if (listenerEntry.useCapture && listenerEntry.listener != null) {
						listenerEntry.listener.handleEvent(event);
					}
				}
			}
			
			/* There is no target phase, because target is in document */
			// Nothing
			
			/* Bubbling phase*/
			if (event.eventPhase == Event.BUBBLING_PHASE) {
				for (EventListenerEntry listenerEntry : clonedListenerEntries) {
					if (event.stopPropagation) {
						break;
					}
					if (!listenerEntry.useCapture && listenerEntry.listener != null) {
						listenerEntry.listener.handleEvent(event);
					}
				}
			}
		}

		return event.preventDefault == false;
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOnafterprint() {
		return onafterprintHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnbeforeprint() {
		return onbeforeprintHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnhashchange() {
		return onhashchangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmessage() {
		return onmessageHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnoffline() {
		return onofflineHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnonline() {
		return ononlineHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpagehide() {
		return onpagehideHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpageshow() {
		return onpageshowHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpopstate() {
		return onpopstateHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstorage() {
		return onstorageHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnunload() {
		return onunloadHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnabort() {
		return onabortHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnblur() {
		return onblurHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncancel() {
		return oncancelHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplay() {
		return oncanplayHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplaythrough() {
		return oncanplaythroughHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnchange() {
		return onchangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclick() {
		return onclickHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclose() {
		return oncloseHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncuechange() {
		return oncuechangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndblclick() {
		return ondblclickHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrag() {
		return ondragHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragend() {
		return ondragendHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragenter() {
		return ondragenterHandlerListener.getEventHandler();
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOndragexit() {
		return ondragexitHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragleave() {
		return ondragleaveHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragover() {
		return ondragoverHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragstart() {
		return ondragstartHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrop() {
		return ondropHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndurationchange() {
		return ondurationchangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnemptied() {
		return onemptiedHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnended() {
		return onemptiedHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnfocus() {
		return onfocusHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninput() {
		return oninputHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninvalid() {
		return oninvalidHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeydown() {
		return onkeydownHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeypress() {
		return onkeypressHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeyup() {
		return onkeyupHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnload() {
		return onloadHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadeddata() {
		return onloadeddataHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadedmetadata() {
		return onloadedmetadataHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadstart() {
		return onloadstartHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousedown() {
		return onmousedownHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseenter() {
		return onmouseenterHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseleave() {
		return onmouseleaveHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousemove() {
		return onmousemoveHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseout() {
		return onmouseoutHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseover() {
		return onmouseoverHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseup() {
		return onmouseupHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousewheel() {
		return onmousewheelHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpause() {
		return onpauseHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplay() {
		return onplayHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplaying() {
		return onplayingHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnprogress() {
		return onprogressHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnratechange() {
		return onratechangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnreset() {
		return onresetHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnresize() {
		return onresizeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnscroll() {
		return onscrollHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeked() {
		return onseekedHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeking() {
		return onseekingHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnselect() {
		return onselectHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnshow() {
		return onshowHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstalled() {
		return onstalledHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsubmit() {
		return onsubmitHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsuspend() {
		return onsuspendHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntimeupdate() {
		return ontimeupdateHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntoggle() {
		return ontoggleHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnvolumechange() {
		return onvolumechangeHandlerListener.getEventHandler();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnwaiting() {
		return onwaitingHandlerListener.getEventHandler();
	}

	@ScriptSetter
	@Override
	public void setOnafterprint(EventHandler handler) {
		onafterprintHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnbeforeprint(EventHandler handler) {
		onbeforeprintHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnhashchange(EventHandler handler) {
		onhashchangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmessage(EventHandler handler) {
		onmessageHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnoffline(EventHandler handler) {
		onofflineHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnonline(EventHandler handler) {
		ononlineHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnpagehide(EventHandler handler) {
		onpagehideHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnpageshow(EventHandler handler) {
		onpageshowHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnpopstate(EventHandler handler) {
		onpopstateHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnstorage(EventHandler handler) {
		onstorageHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnunload(EventHandler handler) {
		onunloadHandlerListener.setEventHandler(handler);
	}
	
	@ScriptSetter
	@Override
	public void setOnabort(EventHandler handler) {
		onabortHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnblur(EventHandler handler) {
		onblurHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOncancel(EventHandler handler) {
		oncancelHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOncanplay(EventHandler handler) {
		oncanplayHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOncanplaythrough(EventHandler handler) {
		oncanplaythroughHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnchange(EventHandler handler) {
		onchangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnclick(EventHandler handler) {
		onclickHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnclose(EventHandler handler) {
		oncloseHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOncuechange(EventHandler handler) {
		oncuechangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndblclick(EventHandler handler) {
		ondblclickHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndrag(EventHandler handler) {
		ondragHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragend(EventHandler handler) {
		ondragendHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragenter(EventHandler handler) {
		ondragenterHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragexit(EventHandler handler) {
		ondragexitHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragleave(EventHandler handler) {
		ondragleaveHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragover(EventHandler handler) {
		ondragoverHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndragstart(EventHandler handler) {
		ondragstartHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndrop(EventHandler handler) {
		ondropHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOndurationchange(EventHandler handler) {
		ondurationchangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnemptied(EventHandler handler) {
		onemptiedHandlerListener.setEventHandler(handler);
	}

	@Override
	public void setOnended(EventHandler handler) {
		onendedHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnfocus(EventHandler handler) {
		onfocusHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOninput(EventHandler handler) {
		oninputHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOninvalid(EventHandler handler) {
		oninvalidHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnkeydown(EventHandler handler) {
		onkeydownHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnkeypress(EventHandler handler) {
		onkeypressHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnkeyup(EventHandler handler) {
		onkeyupHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnload(EventHandler handler) {
		onloadHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnloadeddata(EventHandler handler) {
		onloadeddataHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnloadedmetadata(EventHandler handler) {
		onloadedmetadataHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnloadstart(EventHandler handler) {
		onloadstartHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmousedown(EventHandler handler) {
		onmousedownHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmouseenter(EventHandler handler) {
		onmouseenterHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmouseleave(EventHandler handler) {
		onmouseleaveHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmousemove(EventHandler handler) {
		onmousemoveHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmouseout(EventHandler handler) {
		onmouseoutHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmouseover(EventHandler handler) {
		onmouseoverHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmouseup(EventHandler handler) {
		onmouseupHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnmousewheel(EventHandler handler) {
		onmousewheelHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnpause(EventHandler handler) {
		onpauseHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnplay(EventHandler handler) {
		onplayHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnplaying(EventHandler handler) {
		onplayingHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnprogress(EventHandler handler) {
		onprogressHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnratechange(EventHandler handler) {
		onratechangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnreset(EventHandler handler) {
		onresetHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnresize(EventHandler handler) {
		onresizeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnscroll(EventHandler handler) {
		onscrollHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnseeked(EventHandler handler) {
		onseekedHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnseeking(EventHandler handler) {
		onseekingHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnselect(EventHandler handler) {
		onselectHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnshow(EventHandler handler) {
		onshowHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnstalled(EventHandler handler) {
		onstalledHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnsubmit(EventHandler handler) {
		onsubmitHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnsuspend(EventHandler handler) {
		onsuspendHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOntimeupdate(EventHandler handler) {
		ontimeupdateHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOntoggle(EventHandler handler) {
		ontoggleHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnvolumechange(EventHandler handler) {
		onvolumechangeHandlerListener.setEventHandler(handler);
	}

	@ScriptSetter
	@Override
	public void setOnwaiting(EventHandler handler) {
		onwaitingHandlerListener.setEventHandler(handler);
	}
}

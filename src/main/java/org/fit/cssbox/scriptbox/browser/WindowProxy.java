package org.fit.cssbox.scriptbox.browser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.dom.events.EventListenerEntry;
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

public class WindowProxy extends Window {
	BrowsingContext proxyContext;
	
	public WindowProxy(BrowsingContext context) {
		this.proxyContext = context;
	}
	
	public Window proxiedWindow() {
		return proxyContext.getActiveDocument().getWindow();
	}
	
	@Override
	public boolean equals(Object obj) {
		return proxiedWindow().equals(obj);
	}
	
	@Override
	public int hashCode() {
		return proxiedWindow().hashCode();
	}
	
	@ScriptGetter
	public WindowProxy getWindow() {
		return this;
	}
	
	@ScriptGetter
	public WindowProxy getSelf() {
		return this;
	}

	@ScriptGetter
	public Document getDocument() {
		return proxiedWindow().document;
	}

	@ScriptGetter
	public String getName() {
		return proxyContext.getName();
	}
	
	@ScriptSetter
	public void setName(String name) {
		proxyContext.setName(name);
	}

	@ScriptGetter
	public Location getLocation() {
		return proxyContext.getLocation();
	}

	@ScriptGetter
	public History getHistory() {
		return proxyContext.getHistory();
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
		return null;//proxyContext.get;
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

		evt.target = this;
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

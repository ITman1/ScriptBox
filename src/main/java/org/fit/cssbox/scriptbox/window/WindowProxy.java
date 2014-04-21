/**
 * WindowProxy.java
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

package org.fit.cssbox.scriptbox.window;

import java.util.Collection;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.views.DocumentView;

public class WindowProxy extends Window {
	BrowsingContext proxyContext;
	
	public WindowProxy(BrowsingContext context) {
		this.proxyContext = context;
	}
	
	public Window getProxiedWindow() {
		return proxyContext.getActiveDocument().getWindow();
	}
	
	@Override
	public boolean dispatchEventFromDocument(EventImpl event) {
		throw new UnsupportedOperationException("dispatchEventFromDocument() from Window proxy is not supported! Use direct access instead!");
	}
	
	@Override
	public Html5DocumentImpl getDocumentImpl() {
		throw new UnsupportedOperationException("getDocumentImpl() from Window proxy is not supported! Use direct access instead!");
	}
	
	@Override
	public void setDocumentImpl(Html5DocumentImpl documentImpl) {
		throw new UnsupportedOperationException("setDocumentImpl() from Window proxy is not supported! Use direct access instead!");
	}
	
	@Override
	public WindowScriptSettings getScriptSettings() {
		throw new UnsupportedOperationException("getScriptSettings() from Window proxy is not supported! Use direct access instead!");
	}
	
	@Override
	public void dispatchEvent(Event event, org.w3c.dom.events.EventTarget target) {
		throw new UnsupportedOperationException("dispatchEvent() from Window proxy is not supported! Use direct access instead!");
	}
	
	@Override
	public boolean equals(Object obj) {
		return getProxiedWindow().equals(obj);
	}
	
	@Override
	public int hashCode() {
		return getProxiedWindow().hashCode();
	}
	
	@ScriptGetter
	@Override
	public WindowProxy getWindow() {
		return this;
	}
	
	@ScriptGetter
	@Override
	public WindowProxy getSelf() {
		return this;
	}

	@ScriptGetter
	@Override
	public DocumentView getDocument() {
		return getProxiedWindow().getDocument();
	}

	@ScriptGetter
	@Override
	public String getName() {
		return getProxiedWindow().getName();
	}
	
	@ScriptSetter
	@Override
	public void setName(String name) {
		getProxiedWindow().setName(name);
	}

	@ScriptSetter
	@Override
	public void setLocation(String url) {
		super.setLocation(url);
	}
	
	@ScriptGetter
	@Override
	public Location getLocation() {
		return getProxiedWindow().getLocation();
	}

	@ScriptGetter
	@Override
	public History getHistory() {
		return getProxiedWindow().getHistory();
	}

	@ScriptGetter
	@Override
	public BarProp getLocationbar() {
		return getProxiedWindow().getLocationbar();
	}

	@ScriptGetter
	@Override
	public BarProp getMenubar() {
		return getProxiedWindow().getMenubar();
	}

	@ScriptGetter
	@Override
	public BarProp getPersonalbar() {
		return getProxiedWindow().getPersonalbar();
	}

	@ScriptGetter
	@Override
	public ScrollBarsProp getScrollbars() {
		return getProxiedWindow().getScrollbars();
	}

	@ScriptGetter
	@Override
	public BarProp getStatusbar() {
		return getProxiedWindow().getStatusbar();
	}

	@ScriptGetter
	@Override
	public BarProp getToolbar() {
		return getProxiedWindow().getToolbar();
	}

	@ScriptGetter
	@Override
	public String getStatus() {
		return getProxiedWindow().getStatus();
	}

	@ScriptGetter
	@Override
	public boolean getClosed() {
		return getProxiedWindow().getClosed();
	}

	@ScriptGetter
	@Override
	public WindowProxy getFrames() {
		return getProxiedWindow().getFrames();
	}

	@ScriptGetter
	@Override
	public long getLength() {
		return getProxiedWindow().getLength();
	}

	@ScriptGetter
	@Override
	public WindowProxy getTop() {
		return getProxiedWindow().getTop();
	}

	@ScriptGetter
	@Override
	public WindowProxy getOpener() {
		return getProxiedWindow().getOpener();
	}

	@ScriptGetter
	@Override
	public WindowProxy getParent() {
		return getProxiedWindow().getParent();
	}

	@ScriptGetter
	@Override
	public Element getFrameElement() {
		return getProxiedWindow().getFrameElement();
	}
	
	@ScriptFunction
	@Override
	public void alert(String message) {
		getProxiedWindow().alert(message);
	}
	
	@ScriptFunction
	@Override
	public void alert() {
		getProxiedWindow().alert();
	}
	
	@ScriptFunction
	@Override
	public boolean confirm(String message) {
		return getProxiedWindow().confirm(message);
	}
	
	@ScriptFunction
	@Override
	public boolean confirm() {
		return getProxiedWindow().confirm();
	}
	
	@ScriptFunction
	@Override
	public String prompt(String message, String defaultChoice) {
		return getProxiedWindow().prompt(message, defaultChoice);
	}
	
	@ScriptFunction
	@Override
	public String prompt(String message) {
		return getProxiedWindow().prompt(message);
	}
	
	@ScriptFunction
	@Override
	public String prompt() {
		return getProxiedWindow().prompt();
	}
	
	@ScriptFunction
	@Override
	public void close() {
		getProxiedWindow().close();
	}
	
	@ScriptFunction
	@Override
	public void stop() {
		getProxiedWindow().stop();
	}
	
	@ScriptFunction
	@Override
	public void focus() {
		getProxiedWindow().focus();
	}
	
	@ScriptFunction
	@Override
	public void blur() {
		getProxiedWindow().blur();
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open() {
		return getProxiedWindow().open();
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url) {
		return getProxiedWindow().open(url);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target) {
		return getProxiedWindow().open(url, target);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target, String features) {
		return getProxiedWindow().open(url, target, features);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target, String features, boolean replace) {
		return getProxiedWindow().open(url, target, features, replace);
	}

	@ScriptFunction
	@Override
	public void scroll(int xCoord, int yCoord) {
		getProxiedWindow().scroll(xCoord, yCoord);
	}
	
	@Override
	public Collection<Object> getKeys() {
		return getProxiedWindow().getKeys();
	}
	
	@ScriptFunction
	@Override
	public Object get(Object arg) {
		return getProxiedWindow().get(arg);
	}

	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener) {
		addEventListener(type, listener, false);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener) {
		getProxiedWindow().removeEventListener(type, listener, false);
	}
	
	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		getProxiedWindow().addEventListener(type, listener, useCapture);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		getProxiedWindow().removeEventListener(type, listener, useCapture);
	}

	@ScriptFunction
	@Override
	public boolean dispatchEvent(Event event) {
		return getProxiedWindow().dispatchEvent(event);
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOnafterprint() {
		return getProxiedWindow().getOnafterprint();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnbeforeprint() {
		return getProxiedWindow().getOnbeforeprint();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnhashchange() {
		return getProxiedWindow().getOnhashchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmessage() {
		return getProxiedWindow().getOnmessage();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnoffline() {
		return getProxiedWindow().getOnoffline();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnonline() {
		return getProxiedWindow().getOnonline();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpagehide() {
		return getProxiedWindow().getOnpagehide();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpageshow() {
		return getProxiedWindow().getOnpageshow();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpopstate() {
		return getProxiedWindow().getOnpopstate();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstorage() {
		return getProxiedWindow().getOnstorage();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnunload() {
		return getProxiedWindow().getOnunload();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnabort() {
		return getProxiedWindow().getOnabort();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnblur() {
		return getProxiedWindow().getOnblur();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncancel() {
		return getProxiedWindow().getOncancel();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplay() {
		return getProxiedWindow().getOncanplay();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplaythrough() {
		return getProxiedWindow().getOncanplaythrough();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnchange() {
		return getProxiedWindow().getOnchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclick() {
		return getProxiedWindow().getOnclick();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclose() {
		return getProxiedWindow().getOnclose();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncuechange() {
		return getProxiedWindow().getOncuechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndblclick() {
		return getProxiedWindow().getOndblclick();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrag() {
		return getProxiedWindow().getOndrag();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragend() {
		return getProxiedWindow().getOndragend();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragenter() {
		return getProxiedWindow().getOndragenter();
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOndragexit() {
		return getProxiedWindow().getOndragexit();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragleave() {
		return getProxiedWindow().getOndragleave();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragover() {
		return getProxiedWindow().getOndragover();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragstart() {
		return getProxiedWindow().getOndragstart();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrop() {
		return getProxiedWindow().getOndrop();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndurationchange() {
		return getProxiedWindow().getOndurationchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnemptied() {
		return getProxiedWindow().getOnemptied();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnended() {
		return getProxiedWindow().getOnemptied();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnfocus() {
		return getProxiedWindow().getOnfocus();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninput() {
		return getProxiedWindow().getOninput();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninvalid() {
		return getProxiedWindow().getOninvalid();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeydown() {
		return getProxiedWindow().getOnkeydown();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeypress() {
		return getProxiedWindow().getOnkeypress();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeyup() {
		return getProxiedWindow().getOnkeyup();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnload() {
		return getProxiedWindow().getOnload();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadeddata() {
		return getProxiedWindow().getOnloadeddata();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadedmetadata() {
		return getProxiedWindow().getOnloadedmetadata();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadstart() {
		return getProxiedWindow().getOnloadstart();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousedown() {
		return getProxiedWindow().getOnmousedown();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseenter() {
		return getProxiedWindow().getOnmouseenter();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseleave() {
		return getProxiedWindow().getOnmouseleave();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousemove() {
		return getProxiedWindow().getOnmousemove();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseout() {
		return getProxiedWindow().getOnmouseout();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseover() {
		return getProxiedWindow().getOnmouseover();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseup() {
		return getProxiedWindow().getOnmouseup();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousewheel() {
		return getProxiedWindow().getOnmousewheel();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpause() {
		return getProxiedWindow().getOnpause();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplay() {
		return getProxiedWindow().getOnplay();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplaying() {
		return getProxiedWindow().getOnplaying();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnprogress() {
		return getProxiedWindow().getOnprogress();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnratechange() {
		return getProxiedWindow().getOnratechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnreset() {
		return getProxiedWindow().getOnreset();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnresize() {
		return getProxiedWindow().getOnresize();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnscroll() {
		return getProxiedWindow().getOnscroll();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeked() {
		return getProxiedWindow().getOnseeked();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeking() {
		return getProxiedWindow().getOnseeking();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnselect() {
		return getProxiedWindow().getOnselect();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnshow() {
		return getProxiedWindow().getOnshow();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstalled() {
		return getProxiedWindow().getOnstalled();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsubmit() {
		return getProxiedWindow().getOnsubmit();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsuspend() {
		return getProxiedWindow().getOnsuspend();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntimeupdate() {
		return getProxiedWindow().getOntimeupdate();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntoggle() {
		return getProxiedWindow().getOntoggle();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnvolumechange() {
		return getProxiedWindow().getOnvolumechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnwaiting() {
		return getProxiedWindow().getOnwaiting();
	}

	@ScriptSetter
	@Override
	public void setOnafterprint(EventHandler handler) {
		getProxiedWindow().setOnafterprint(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnbeforeprint(EventHandler handler) {
		getProxiedWindow().setOnbeforeprint(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnhashchange(EventHandler handler) {
		getProxiedWindow().setOnhashchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmessage(EventHandler handler) {
		getProxiedWindow().setOnmessage(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnoffline(EventHandler handler) {
		getProxiedWindow().setOnoffline(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnonline(EventHandler handler) {
		getProxiedWindow().setOnonline(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpagehide(EventHandler handler) {
		getProxiedWindow().setOnpagehide(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpageshow(EventHandler handler) {
		getProxiedWindow().setOnpageshow(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpopstate(EventHandler handler) {
		getProxiedWindow().setOnpopstate(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnstorage(EventHandler handler) {
		getProxiedWindow().setOnstorage(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnunload(EventHandler handler) {
		getProxiedWindow().setOnunload(handler);;
	}
	
	@ScriptSetter
	@Override
	public void setOnabort(EventHandler handler) {
		getProxiedWindow().setOnabort(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnblur(EventHandler handler) {
		getProxiedWindow().setOnblur(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncancel(EventHandler handler) {
		getProxiedWindow().setOncancel(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncanplay(EventHandler handler) {
		getProxiedWindow().setOncanplay(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncanplaythrough(EventHandler handler) {
		getProxiedWindow().setOncanplaythrough(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnchange(EventHandler handler) {
		getProxiedWindow().setOnchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnclick(EventHandler handler) {
		getProxiedWindow().setOnclick(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnclose(EventHandler handler) {
		getProxiedWindow().setOnclose(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncuechange(EventHandler handler) {
		getProxiedWindow().setOncuechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndblclick(EventHandler handler) {
		getProxiedWindow().setOndblclick(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndrag(EventHandler handler) {
		getProxiedWindow().setOndrag(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragend(EventHandler handler) {
		getProxiedWindow().setOndragend(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragenter(EventHandler handler) {
		getProxiedWindow().setOndragenter(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragexit(EventHandler handler) {
		getProxiedWindow().setOndragexit(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragleave(EventHandler handler) {
		getProxiedWindow().setOndragleave(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragover(EventHandler handler) {
		getProxiedWindow().setOndragover(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragstart(EventHandler handler) {
		getProxiedWindow().setOndragstart(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndrop(EventHandler handler) {
		getProxiedWindow().setOndrop(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndurationchange(EventHandler handler) {
		getProxiedWindow().setOndurationchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnemptied(EventHandler handler) {
		getProxiedWindow().setOnemptied(handler);;
	}

	@Override
	public void setOnended(EventHandler handler) {
		getProxiedWindow().setOnended(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnfocus(EventHandler handler) {
		getProxiedWindow().setOnfocus(handler);;
	}

	@ScriptSetter
	@Override
	public void setOninput(EventHandler handler) {
		getProxiedWindow().setOninput(handler);;
	}

	@ScriptSetter
	@Override
	public void setOninvalid(EventHandler handler) {
		getProxiedWindow().setOninvalid(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeydown(EventHandler handler) {
		getProxiedWindow().setOnkeydown(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeypress(EventHandler handler) {
		getProxiedWindow().setOnkeypress(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeyup(EventHandler handler) {
		getProxiedWindow().setOnkeyup(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnload(EventHandler handler) {
		getProxiedWindow().setOnload(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadeddata(EventHandler handler) {
		getProxiedWindow().setOnloadeddata(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadedmetadata(EventHandler handler) {
		getProxiedWindow().setOnloadedmetadata(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadstart(EventHandler handler) {
		getProxiedWindow().setOnloadstart(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousedown(EventHandler handler) {
		getProxiedWindow().setOnmousedown(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseenter(EventHandler handler) {
		getProxiedWindow().setOnmouseenter(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseleave(EventHandler handler) {
		getProxiedWindow().setOnmouseleave(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousemove(EventHandler handler) {
		getProxiedWindow().setOnmousemove(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseout(EventHandler handler) {
		getProxiedWindow().setOnmouseout(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseover(EventHandler handler) {
		getProxiedWindow().setOnmouseover(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseup(EventHandler handler) {
		getProxiedWindow().setOnmouseup(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousewheel(EventHandler handler) {
		getProxiedWindow().setOnmousewheel(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpause(EventHandler handler) {
		getProxiedWindow().setOnpause(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnplay(EventHandler handler) {
		getProxiedWindow().setOnplay(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnplaying(EventHandler handler) {
		getProxiedWindow().setOnplaying(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnprogress(EventHandler handler) {
		getProxiedWindow().setOnprogress(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnratechange(EventHandler handler) {
		getProxiedWindow().setOnratechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnreset(EventHandler handler) {
		getProxiedWindow().setOnreset(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnresize(EventHandler handler) {
		getProxiedWindow().setOnresize(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnscroll(EventHandler handler) {
		getProxiedWindow().setOnscroll(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnseeked(EventHandler handler) {
		getProxiedWindow().setOnseeked(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnseeking(EventHandler handler) {
		getProxiedWindow().setOnseeking(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnselect(EventHandler handler) {
		getProxiedWindow().setOnselect(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnshow(EventHandler handler) {
		getProxiedWindow().setOnshow(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnstalled(EventHandler handler) {
		getProxiedWindow().setOnstalled(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnsubmit(EventHandler handler) {
		getProxiedWindow().setOnsubmit(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnsuspend(EventHandler handler) {
		getProxiedWindow().setOnsuspend(handler);;
	}

	@ScriptSetter
	@Override
	public void setOntimeupdate(EventHandler handler) {
		getProxiedWindow().setOntimeupdate(handler);;
	}

	@ScriptSetter
	@Override
	public void setOntoggle(EventHandler handler) {
		getProxiedWindow().setOntoggle(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnvolumechange(EventHandler handler) {
		getProxiedWindow().setOnvolumechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnwaiting(EventHandler handler) {
		getProxiedWindow().setOnwaiting(handler);;
	}
}

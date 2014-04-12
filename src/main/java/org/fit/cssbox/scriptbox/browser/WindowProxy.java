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

package org.fit.cssbox.scriptbox.browser;

import java.util.Collection;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.views.DocumentView;

public class WindowProxy extends Window {
	BrowsingContext proxyContext;
	
	public WindowProxy(BrowsingContext context) {
		this.proxyContext = context;
	}
	
	public Window proxiedWindow() {
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
		return proxiedWindow().equals(obj);
	}
	
	@Override
	public int hashCode() {
		return proxiedWindow().hashCode();
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
		return proxiedWindow().getDocument();
	}

	@ScriptGetter
	@Override
	public String getName() {
		return proxiedWindow().getName();
	}
	
	@ScriptSetter
	@Override
	public void setName(String name) {
		proxiedWindow().setName(name);
	}

	@ScriptGetter
	@Override
	public Location getLocation() {
		return proxiedWindow().getLocation();
	}

	@ScriptGetter
	@Override
	public History getHistory() {
		return proxiedWindow().getHistory();
	}

	@ScriptGetter
	@Override
	public BarProp getLocationbar() {
		return proxiedWindow().getLocationbar();
	}

	@ScriptGetter
	@Override
	public BarProp getMenubar() {
		return proxiedWindow().getMenubar();
	}

	@ScriptGetter
	@Override
	public BarProp getPersonalbar() {
		return proxiedWindow().getPersonalbar();
	}

	@ScriptGetter
	@Override
	public BarProp getScrollbars() {
		return proxiedWindow().getScrollbars();
	}

	@ScriptGetter
	@Override
	public BarProp getStatusbar() {
		return proxiedWindow().getStatusbar();
	}

	@ScriptGetter
	@Override
	public BarProp getToolbar() {
		return proxiedWindow().getToolbar();
	}

	@ScriptGetter
	@Override
	public String getStatus() {
		return proxiedWindow().getStatus();
	}

	@ScriptGetter
	@Override
	public boolean getClosed() {
		return proxiedWindow().getClosed();
	}

	@ScriptGetter
	@Override
	public WindowProxy getFrames() {
		return proxiedWindow().getFrames();
	}

	@ScriptGetter
	@Override
	public long getLength() {
		return proxiedWindow().getLength();
	}

	@ScriptGetter
	@Override
	public WindowProxy getTop() {
		return proxiedWindow().getTop();
	}

	@ScriptGetter
	@Override
	public WindowProxy getOpener() {
		return proxiedWindow().getOpener();
	}

	@ScriptGetter
	@Override
	public WindowProxy getParent() {
		return proxiedWindow().getParent();
	}

	@ScriptGetter
	@Override
	public Element getFrameElement() {
		return proxiedWindow().getFrameElement();
	}
	
	@ScriptFunction
	@Override
	public void close() {
		proxiedWindow().close();
	}
	
	@ScriptFunction
	@Override
	public void stop() {
		proxiedWindow().stop();
	}
	
	@ScriptFunction
	@Override
	public void focus() {
		proxiedWindow().focus();
	}
	
	@ScriptFunction
	@Override
	public void blur() {
		proxiedWindow().blur();
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open() {
		return proxiedWindow().open();
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url) {
		return proxiedWindow().open(url);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target) {
		return proxiedWindow().open(url, target);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target, String features) {
		return proxiedWindow().open(url, target, features);
	}
	
	@ScriptFunction
	@Override
	public WindowProxy open(String url, String target, String features, boolean replace) {
		return proxiedWindow().open(url, target, features, replace);
	}

	@ScriptFunction
	@Override
	public void scroll(int xCoord, int yCoord) {
		proxiedWindow().scroll(xCoord, yCoord);
	}
	
	@Override
	public Collection<Object> getKeys() {
		return proxiedWindow().getKeys();
	}
	
	@ScriptFunction
	@Override
	public Object get(Object arg) {
		return proxiedWindow().get(arg);
	}

	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener) {
		addEventListener(type, listener, false);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener) {
		proxiedWindow().removeEventListener(type, listener, false);
	}
	
	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		proxiedWindow().addEventListener(type, listener, useCapture);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		proxiedWindow().removeEventListener(type, listener, useCapture);
	}

	@ScriptFunction
	@Override
	public boolean dispatchEvent(Event event) {
		return proxiedWindow().dispatchEvent(event);
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOnafterprint() {
		return proxiedWindow().getOnafterprint();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnbeforeprint() {
		return proxiedWindow().getOnbeforeprint();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnhashchange() {
		return proxiedWindow().getOnhashchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmessage() {
		return proxiedWindow().getOnmessage();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnoffline() {
		return proxiedWindow().getOnoffline();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnonline() {
		return proxiedWindow().getOnonline();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpagehide() {
		return proxiedWindow().getOnpagehide();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpageshow() {
		return proxiedWindow().getOnpageshow();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpopstate() {
		return proxiedWindow().getOnpopstate();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstorage() {
		return proxiedWindow().getOnstorage();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnunload() {
		return proxiedWindow().getOnunload();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnabort() {
		return proxiedWindow().getOnabort();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnblur() {
		return proxiedWindow().getOnblur();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncancel() {
		return proxiedWindow().getOncancel();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplay() {
		return proxiedWindow().getOncanplay();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncanplaythrough() {
		return proxiedWindow().getOncanplaythrough();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnchange() {
		return proxiedWindow().getOnchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclick() {
		return proxiedWindow().getOnclick();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnclose() {
		return proxiedWindow().getOnclose();
	}

	@ScriptGetter
	@Override
	public EventHandler getOncuechange() {
		return proxiedWindow().getOncuechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndblclick() {
		return proxiedWindow().getOndblclick();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrag() {
		return proxiedWindow().getOndrag();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragend() {
		return proxiedWindow().getOndragend();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragenter() {
		return proxiedWindow().getOndragenter();
	}
	
	@ScriptGetter
	@Override
	public EventHandler getOndragexit() {
		return proxiedWindow().getOndragexit();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragleave() {
		return proxiedWindow().getOndragleave();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragover() {
		return proxiedWindow().getOndragover();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndragstart() {
		return proxiedWindow().getOndragstart();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndrop() {
		return proxiedWindow().getOndrop();
	}

	@ScriptGetter
	@Override
	public EventHandler getOndurationchange() {
		return proxiedWindow().getOndurationchange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnemptied() {
		return proxiedWindow().getOnemptied();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnended() {
		return proxiedWindow().getOnemptied();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnfocus() {
		return proxiedWindow().getOnfocus();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninput() {
		return proxiedWindow().getOninput();
	}

	@ScriptGetter
	@Override
	public EventHandler getOninvalid() {
		return proxiedWindow().getOninvalid();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeydown() {
		return proxiedWindow().getOnkeydown();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeypress() {
		return proxiedWindow().getOnkeypress();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnkeyup() {
		return proxiedWindow().getOnkeyup();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnload() {
		return proxiedWindow().getOnload();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadeddata() {
		return proxiedWindow().getOnloadeddata();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadedmetadata() {
		return proxiedWindow().getOnloadedmetadata();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnloadstart() {
		return proxiedWindow().getOnloadstart();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousedown() {
		return proxiedWindow().getOnmousedown();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseenter() {
		return proxiedWindow().getOnmouseenter();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseleave() {
		return proxiedWindow().getOnmouseleave();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousemove() {
		return proxiedWindow().getOnmousemove();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseout() {
		return proxiedWindow().getOnmouseout();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseover() {
		return proxiedWindow().getOnmouseover();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmouseup() {
		return proxiedWindow().getOnmouseup();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnmousewheel() {
		return proxiedWindow().getOnmousewheel();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnpause() {
		return proxiedWindow().getOnpause();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplay() {
		return proxiedWindow().getOnplay();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnplaying() {
		return proxiedWindow().getOnplaying();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnprogress() {
		return proxiedWindow().getOnprogress();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnratechange() {
		return proxiedWindow().getOnratechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnreset() {
		return proxiedWindow().getOnreset();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnresize() {
		return proxiedWindow().getOnresize();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnscroll() {
		return proxiedWindow().getOnscroll();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeked() {
		return proxiedWindow().getOnseeked();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnseeking() {
		return proxiedWindow().getOnseeking();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnselect() {
		return proxiedWindow().getOnselect();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnshow() {
		return proxiedWindow().getOnshow();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnstalled() {
		return proxiedWindow().getOnstalled();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsubmit() {
		return proxiedWindow().getOnsubmit();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnsuspend() {
		return proxiedWindow().getOnsuspend();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntimeupdate() {
		return proxiedWindow().getOntimeupdate();
	}

	@ScriptGetter
	@Override
	public EventHandler getOntoggle() {
		return proxiedWindow().getOntoggle();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnvolumechange() {
		return proxiedWindow().getOnvolumechange();
	}

	@ScriptGetter
	@Override
	public EventHandler getOnwaiting() {
		return proxiedWindow().getOnwaiting();
	}

	@ScriptSetter
	@Override
	public void setOnafterprint(EventHandler handler) {
		proxiedWindow().setOnafterprint(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnbeforeprint(EventHandler handler) {
		proxiedWindow().setOnbeforeprint(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnhashchange(EventHandler handler) {
		proxiedWindow().setOnhashchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmessage(EventHandler handler) {
		proxiedWindow().setOnmessage(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnoffline(EventHandler handler) {
		proxiedWindow().setOnoffline(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnonline(EventHandler handler) {
		proxiedWindow().setOnonline(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpagehide(EventHandler handler) {
		proxiedWindow().setOnpagehide(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpageshow(EventHandler handler) {
		proxiedWindow().setOnpageshow(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpopstate(EventHandler handler) {
		proxiedWindow().setOnpopstate(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnstorage(EventHandler handler) {
		proxiedWindow().setOnstorage(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnunload(EventHandler handler) {
		proxiedWindow().setOnunload(handler);;
	}
	
	@ScriptSetter
	@Override
	public void setOnabort(EventHandler handler) {
		proxiedWindow().setOnabort(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnblur(EventHandler handler) {
		proxiedWindow().setOnblur(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncancel(EventHandler handler) {
		proxiedWindow().setOncancel(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncanplay(EventHandler handler) {
		proxiedWindow().setOncanplay(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncanplaythrough(EventHandler handler) {
		proxiedWindow().setOncanplaythrough(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnchange(EventHandler handler) {
		proxiedWindow().setOnchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnclick(EventHandler handler) {
		proxiedWindow().setOnclick(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnclose(EventHandler handler) {
		proxiedWindow().setOnclose(handler);;
	}

	@ScriptSetter
	@Override
	public void setOncuechange(EventHandler handler) {
		proxiedWindow().setOncuechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndblclick(EventHandler handler) {
		proxiedWindow().setOndblclick(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndrag(EventHandler handler) {
		proxiedWindow().setOndrag(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragend(EventHandler handler) {
		proxiedWindow().setOndragend(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragenter(EventHandler handler) {
		proxiedWindow().setOndragenter(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragexit(EventHandler handler) {
		proxiedWindow().setOndragexit(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragleave(EventHandler handler) {
		proxiedWindow().setOndragleave(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragover(EventHandler handler) {
		proxiedWindow().setOndragover(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndragstart(EventHandler handler) {
		proxiedWindow().setOndragstart(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndrop(EventHandler handler) {
		proxiedWindow().setOndrop(handler);;
	}

	@ScriptSetter
	@Override
	public void setOndurationchange(EventHandler handler) {
		proxiedWindow().setOndurationchange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnemptied(EventHandler handler) {
		proxiedWindow().setOnemptied(handler);;
	}

	@Override
	public void setOnended(EventHandler handler) {
		proxiedWindow().setOnended(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnfocus(EventHandler handler) {
		proxiedWindow().setOnfocus(handler);;
	}

	@ScriptSetter
	@Override
	public void setOninput(EventHandler handler) {
		proxiedWindow().setOninput(handler);;
	}

	@ScriptSetter
	@Override
	public void setOninvalid(EventHandler handler) {
		proxiedWindow().setOninvalid(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeydown(EventHandler handler) {
		proxiedWindow().setOnkeydown(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeypress(EventHandler handler) {
		proxiedWindow().setOnkeypress(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnkeyup(EventHandler handler) {
		proxiedWindow().setOnkeyup(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnload(EventHandler handler) {
		proxiedWindow().setOnload(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadeddata(EventHandler handler) {
		proxiedWindow().setOnloadeddata(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadedmetadata(EventHandler handler) {
		proxiedWindow().setOnloadedmetadata(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnloadstart(EventHandler handler) {
		proxiedWindow().setOnloadstart(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousedown(EventHandler handler) {
		proxiedWindow().setOnmousedown(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseenter(EventHandler handler) {
		proxiedWindow().setOnmouseenter(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseleave(EventHandler handler) {
		proxiedWindow().setOnmouseleave(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousemove(EventHandler handler) {
		proxiedWindow().setOnmousemove(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseout(EventHandler handler) {
		proxiedWindow().setOnmouseout(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseover(EventHandler handler) {
		proxiedWindow().setOnmouseover(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmouseup(EventHandler handler) {
		proxiedWindow().setOnmouseup(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnmousewheel(EventHandler handler) {
		proxiedWindow().setOnmousewheel(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnpause(EventHandler handler) {
		proxiedWindow().setOnpause(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnplay(EventHandler handler) {
		proxiedWindow().setOnplay(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnplaying(EventHandler handler) {
		proxiedWindow().setOnplaying(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnprogress(EventHandler handler) {
		proxiedWindow().setOnprogress(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnratechange(EventHandler handler) {
		proxiedWindow().setOnratechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnreset(EventHandler handler) {
		proxiedWindow().setOnreset(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnresize(EventHandler handler) {
		proxiedWindow().setOnresize(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnscroll(EventHandler handler) {
		proxiedWindow().setOnscroll(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnseeked(EventHandler handler) {
		proxiedWindow().setOnseeked(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnseeking(EventHandler handler) {
		proxiedWindow().setOnseeking(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnselect(EventHandler handler) {
		proxiedWindow().setOnselect(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnshow(EventHandler handler) {
		proxiedWindow().setOnshow(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnstalled(EventHandler handler) {
		proxiedWindow().setOnstalled(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnsubmit(EventHandler handler) {
		proxiedWindow().setOnsubmit(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnsuspend(EventHandler handler) {
		proxiedWindow().setOnsuspend(handler);;
	}

	@ScriptSetter
	@Override
	public void setOntimeupdate(EventHandler handler) {
		proxiedWindow().setOntimeupdate(handler);;
	}

	@ScriptSetter
	@Override
	public void setOntoggle(EventHandler handler) {
		proxiedWindow().setOntoggle(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnvolumechange(EventHandler handler) {
		proxiedWindow().setOnvolumechange(handler);;
	}

	@ScriptSetter
	@Override
	public void setOnwaiting(EventHandler handler) {
		proxiedWindow().setOnwaiting(handler);;
	}
}

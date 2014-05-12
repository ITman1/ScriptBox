/**
 * GlobalEventHandlers.java
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

package org.fit.cssbox.scriptbox.dom.events;

/**
 * Interface for defining global event handler for objects which support this.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#globaleventhandlers">GlobalEventHandlers </a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface GlobalEventHandlers {
	public final static String onabort = "abort";
	public final static String onblur = "blur";
	public final static String oncancel = "cancel";
	public final static String oncanplay = "canplay";
	public final static String oncanplaythrough = "canplaythrough";
	public final static String onchange = "change";
	public final static String onclick = "click";
	public final static String onclose = "close";
	public final static String oncuechange = "cuechange";
	public final static String ondblclick = "dblclick";
	public final static String ondrag = "drag";
	public final static String ondragend = "dragend";
	public final static String ondragenter = "dragenter";
	public final static String ondragexit = "dragexit";
	public final static String ondragleave = "dragleave";
	public final static String ondragover = "dragover";
	public final static String ondragstart = "dragstart";
	public final static String ondrop = "drop";
	public final static String ondurationchange = "durationchange";
	public final static String onemptied = "emptied";
	public final static String onended = "ended";
	public final static String onerror = "error";
	public final static String onfocus = "focus";
	public final static String oninput = "input";
	public final static String oninvalid = "invalid";
	public final static String onkeydown = "keydown";
	public final static String onkeypress = "keypress";
	public final static String onkeyup = "keyup";
	public final static String onload = "load";
	public final static String onloadeddata = "loadeddata";
	public final static String onloadedmetadata = "loadedmetadata";
	public final static String onloadstart = "loadstart";
	public final static String onmousedown = "mousedown";
	public final static String onmouseenter = "mouseenter";
	public final static String onmouseleave = "mouseleave";
	public final static String onmousemove = "mousemove";
	public final static String onmouseout = "mouseout";
	public final static String onmouseover = "mouseover";
	public final static String onmouseup = "mouseup";
	public final static String onmousewheel = "mousewheel";
	public final static String onpause = "pause";
	public final static String onplay = "play";
	public final static String onplaying = "playing";
	public final static String onprogress = "progress";
	public final static String onratechange = "ratechange";
	public final static String onreset = "reset";
	public final static String onresize = "resize";
	public final static String onscroll = "scroll";
	public final static String onseeked = "seeked";
	public final static String onseeking = "seeking";
	public final static String onselect = "select";
	public final static String onshow = "show";
	public final static String onstalled = "stalled";
	public final static String onsubmit = "submit";
	public final static String onsuspend = "suspend";
	public final static String ontimeupdate = "timeupdate";
	public final static String ontoggle = "toggle";
	public final static String onvolumechange = "volumechange";
	public final static String onwaiting = "waiting";
	
	public EventHandler getOnabort();
	public EventHandler getOnblur();
	public EventHandler getOncancel();
	public EventHandler getOncanplay();
	public EventHandler getOncanplaythrough();
	public EventHandler getOnchange();
	public EventHandler getOnclick();
	public EventHandler getOnclose();
	public EventHandler getOncuechange();
	public EventHandler getOndblclick();
	public EventHandler getOndrag();
	public EventHandler getOndragend();
	public EventHandler getOndragenter();
	public EventHandler getOndragexit();
	public EventHandler getOndragleave();
	public EventHandler getOndragover();
	public EventHandler getOndragstart();
	public EventHandler getOndrop();
	public EventHandler getOndurationchange();
	public EventHandler getOnemptied();
	public EventHandler getOnended();
	public OnErrorEventHandler getOnerror();
	public EventHandler getOnfocus();
	public EventHandler getOninput();
	public EventHandler getOninvalid();
	public EventHandler getOnkeydown();
	public EventHandler getOnkeypress();
	public EventHandler getOnkeyup();
	public EventHandler getOnload();
	public EventHandler getOnloadeddata();
	public EventHandler getOnloadedmetadata();
	public EventHandler getOnloadstart();
	public EventHandler getOnmousedown();
	public EventHandler getOnmouseenter();
	public EventHandler getOnmouseleave();
	public EventHandler getOnmousemove();
	public EventHandler getOnmouseout();
	public EventHandler getOnmouseover();
	public EventHandler getOnmouseup();
	public EventHandler getOnmousewheel();
	public EventHandler getOnpause();
	public EventHandler getOnplay();
	public EventHandler getOnplaying();
	public EventHandler getOnprogress();
	public EventHandler getOnratechange();
	public EventHandler getOnreset();
	public EventHandler getOnresize();
	public EventHandler getOnscroll();
	public EventHandler getOnseeked();
	public EventHandler getOnseeking();
	public EventHandler getOnselect();
	public EventHandler getOnshow();
	public EventHandler getOnstalled();
	public EventHandler getOnsubmit();
	public EventHandler getOnsuspend();
	public EventHandler getOntimeupdate();
	public EventHandler getOntoggle();
	public EventHandler getOnvolumechange();
	public EventHandler getOnwaiting();
	
	public void setOnabort(EventHandler handler);
	public void setOnblur(EventHandler handler);
	public void setOncancel(EventHandler handler);
	public void setOncanplay(EventHandler handler);
	public void setOncanplaythrough(EventHandler handler);
	public void setOnchange(EventHandler handler);
	public void setOnclick(EventHandler handler);
	public void setOnclose(EventHandler handler);
	public void setOncuechange(EventHandler handler);
	public void setOndblclick(EventHandler handler);
	public void setOndrag(EventHandler handler);
	public void setOndragend(EventHandler handler);
	public void setOndragenter(EventHandler handler);
	public void setOndragexit(EventHandler handler);
	public void setOndragleave(EventHandler handler);
	public void setOndragover(EventHandler handler);
	public void setOndragstart(EventHandler handler);
	public void setOndrop(EventHandler handler);
	public void setOndurationchange(EventHandler handler);
	public void setOnemptied(EventHandler handler);
	public void setOnended(EventHandler handler);
	public void setOnerror(OnErrorEventHandler onerror);
	public void setOnfocus(EventHandler handler);
	public void setOninput(EventHandler handler);
	public void setOninvalid(EventHandler handler);
	public void setOnkeydown(EventHandler handler);
	public void setOnkeypress(EventHandler handler);
	public void setOnkeyup(EventHandler handler);
	public void setOnload(EventHandler handler);
	public void setOnloadeddata(EventHandler handler);
	public void setOnloadedmetadata(EventHandler handler);
	public void setOnloadstart(EventHandler handler);
	public void setOnmousedown(EventHandler handler);
	public void setOnmouseenter(EventHandler handler);
	public void setOnmouseleave(EventHandler handler);
	public void setOnmousemove(EventHandler handler);
	public void setOnmouseout(EventHandler handler);
	public void setOnmouseover(EventHandler handler);
	public void setOnmouseup(EventHandler handler);
	public void setOnmousewheel(EventHandler handler);
	public void setOnpause(EventHandler handler);
	public void setOnplay(EventHandler handler);
	public void setOnplaying(EventHandler handler);
	public void setOnprogress(EventHandler handler);
	public void setOnratechange(EventHandler handler);
	public void setOnreset(EventHandler handler);
	public void setOnresize(EventHandler handler);
	public void setOnscroll(EventHandler handler);
	public void setOnseeked(EventHandler handler);
	public void setOnseeking(EventHandler handler);
	public void setOnselect(EventHandler handler);
	public void setOnshow(EventHandler handler);
	public void setOnstalled(EventHandler handler);
	public void setOnsubmit(EventHandler handler);
	public void setOnsuspend(EventHandler handler);
	public void setOntimeupdate(EventHandler handler);
	public void setOntoggle(EventHandler handler);
	public void setOnvolumechange(EventHandler handler);
	public void setOnwaiting(EventHandler handler);
}

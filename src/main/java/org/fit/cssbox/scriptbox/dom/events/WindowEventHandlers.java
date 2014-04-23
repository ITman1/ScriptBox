/**
 * WindowEventHandlers.java
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
 * Interface for defining window event handler for objects which support this.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#windoweventhandlers">WindowEventHandlers</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface WindowEventHandlers {
    public final static String onafterprint = "afterprint";
    public final static String onbeforeprint = "beforeprint";
    public final static String onbeforeunload = "beforeunload";
    public final static String onhashchange = "hashchange";
    public final static String onmessage = "message";
    public final static String onoffline = "offline";
    public final static String ononline = "online";
    public final static String onpagehide = "pagehide";
    public final static String onpageshow = "pageshow";
    public final static String onpopstate = "popstate";
    public final static String onstorage = "storage";
    public final static String onunload = "unload";
	
    public EventHandler getOnafterprint();
    public EventHandler getOnbeforeprint();
    //TODO:public void OnBeforeUnloadEventHandler onbeforeunload;
    public EventHandler getOnhashchange();
    public EventHandler getOnmessage();
    public EventHandler getOnoffline();
    public EventHandler getOnonline();
    public EventHandler getOnpagehide();
    public EventHandler getOnpageshow();
    public EventHandler getOnpopstate();
    public EventHandler getOnstorage();
    public EventHandler getOnunload();
    
    public void setOnafterprint(EventHandler handler);
    public void setOnbeforeprint(EventHandler handler);
    //TODO:public void OnBeforeUnloadEventHandler onbeforeunload;
    public void setOnhashchange(EventHandler handler);
    public void setOnmessage(EventHandler handler);
    public void setOnoffline(EventHandler handler);
    public void setOnonline(EventHandler handler);
    public void setOnpagehide(EventHandler handler);
    public void setOnpageshow(EventHandler handler);
    public void setOnpopstate(EventHandler handler);
    public void setOnstorage(EventHandler handler);
    public void setOnunload(EventHandler handler);
}

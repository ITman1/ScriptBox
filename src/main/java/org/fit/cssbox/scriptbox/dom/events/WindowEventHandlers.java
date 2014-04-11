package org.fit.cssbox.scriptbox.dom.events;

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

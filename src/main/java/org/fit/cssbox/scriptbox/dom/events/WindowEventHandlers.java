package org.fit.cssbox.scriptbox.dom.events;

public interface WindowEventHandlers {
    public final static String onafterprint_msg = "onafterprint";
    public final static String onbeforeprint_msg = "onbeforeprint";
    public final static String onbeforeunload_msg = "onbeforeunload";
    public final static String onhashchange_msg = "onhashchange";
    public final static String onmessage_msg = "onmessage";
    public final static String onoffline_msg = "onoffline";
    public final static String ononline_msg = "ononline";
    public final static String onpagehide_msg = "onpagehide";
    public final static String onpageshow_msg = "onpageshow";
    public final static String onpopstate_msg = "onpopstate";
    public final static String onstorage_msg = "onstorage";
    public final static String onunload_msg = "onunload";
	
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

package org.fit.cssbox.scriptbox.dom.events;

public interface WindowEventHandlers {
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

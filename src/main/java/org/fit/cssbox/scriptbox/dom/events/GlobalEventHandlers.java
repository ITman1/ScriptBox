package org.fit.cssbox.scriptbox.dom.events;

import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;

public interface GlobalEventHandlers {
	@ScriptGetter
	public EventHandler getOnabort();
	@ScriptGetter
	public EventHandler getOnblur();
	@ScriptGetter
	public EventHandler getOncancel();
	@ScriptGetter
	public EventHandler getOncanplay();
	@ScriptGetter 
	public EventHandler getOncanplaythrough();
	@ScriptGetter 
	public EventHandler getOnchange();
	@ScriptGetter
	public EventHandler getOnclick();
	@ScriptGetter
	public EventHandler getOnclose();
	@ScriptGetter
	public EventHandler getOncuechange();
	@ScriptGetter
	public EventHandler getOndblclick();
	@ScriptGetter
	public EventHandler getOndrag();
	@ScriptGetter
	public EventHandler getOndragend();
	@ScriptGetter
	public EventHandler getOndragenter();
	@ScriptGetter
	public EventHandler getOndragexit();
	@ScriptGetter
	public EventHandler getOndragleave();
	@ScriptGetter
	public EventHandler getOndragover();
	@ScriptGetter
	public EventHandler getOndragstart();
	@ScriptGetter
	public EventHandler getOndrop();
	@ScriptGetter
	public EventHandler getOndurationchange();
	@ScriptGetter
	public EventHandler getOnemptied();
	@ScriptGetter
	public EventHandler getOnended();
	//TODO: OnErrorEventHandler onerror;
	@ScriptGetter
	public EventHandler getOnfocus();
	@ScriptGetter
	public EventHandler getOninput();
	@ScriptGetter
	public EventHandler getOninvalid();
	@ScriptGetter
	public EventHandler getOnkeydown();
	@ScriptGetter
	public EventHandler getOnkeypress();
	@ScriptGetter
	public EventHandler getOnkeyup();
	@ScriptGetter
	public EventHandler getOnload();
	@ScriptGetter
	public EventHandler getOnloadeddata();
	@ScriptGetter
	public EventHandler getOnloadedmetadata();
	@ScriptGetter
	public EventHandler getOnloadstart();
	@ScriptGetter
	public EventHandler getOnmousedown();
	@ScriptGetter
	public EventHandler getOnmouseenter();
	@ScriptGetter
	public EventHandler getOnmouseleave();
	@ScriptGetter
	public EventHandler getOnmousemove();
	@ScriptGetter
	public EventHandler getOnmouseout();
	@ScriptGetter
	public EventHandler getOnmouseover();
	@ScriptGetter
	public EventHandler getOnmouseup();
	@ScriptGetter
	public EventHandler getOnmousewheel();
	@ScriptGetter
	public EventHandler getOnpause();
	@ScriptGetter
	public EventHandler getOnplay();
	@ScriptGetter
	public EventHandler getOnplaying();
	@ScriptGetter
	public EventHandler getOnprogress();
	@ScriptGetter
	public EventHandler getOnratechange();
	@ScriptGetter
	public EventHandler getOnreset();
	@ScriptGetter
	public EventHandler getOnresize();
	@ScriptGetter
	public EventHandler getOnscroll();
	@ScriptGetter
	public EventHandler getOnseeked();
	@ScriptGetter
	public EventHandler getOnseeking();
	@ScriptGetter
	public EventHandler getOnselect();
	@ScriptGetter
	public EventHandler getOnshow();
	@ScriptGetter
	public EventHandler getOnstalled();
	@ScriptGetter
	public EventHandler getOnsubmit();
	@ScriptGetter
	public EventHandler getOnsuspend();
	@ScriptGetter
	public EventHandler getOntimeupdate();
	@ScriptGetter
	public EventHandler getOntoggle();
	@ScriptGetter
	public EventHandler getOnvolumechange();
	@ScriptGetter
	public EventHandler getOnwaiting();
	
	@ScriptSetter
	public void setOnabort(EventHandler handler);
	@ScriptSetter
	public void setOnblur(EventHandler handler);
	@ScriptSetter
	public void setOncancel(EventHandler handler);
	@ScriptSetter
	public void setOncanplay(EventHandler handler);
	@ScriptSetter
	public void setOncanplaythrough(EventHandler handler);
	@ScriptSetter
	public void setOnchange(EventHandler handler);
	@ScriptSetter
	public void setOnclick(EventHandler handler);
	@ScriptSetter
	public void setOnclose(EventHandler handler);
	@ScriptSetter
	public void setOncuechange(EventHandler handler);
	@ScriptSetter
	public void setOndblclick(EventHandler handler);
	@ScriptSetter
	public void setOndrag(EventHandler handler);
	@ScriptSetter
	public void setOndragend(EventHandler handler);
	@ScriptSetter
	public void setOndragenter(EventHandler handler);
	@ScriptSetter
	public void setOndragexit(EventHandler handler);
	@ScriptSetter
	public void setOndragleave(EventHandler handler);
	@ScriptSetter
	public void setOndragover(EventHandler handler);
	@ScriptSetter
	public void setOndragstart(EventHandler handler);
	@ScriptSetter
	public void setOndrop(EventHandler handler);
	@ScriptSetter
	public void setOndurationchange(EventHandler handler);
	@ScriptSetter
	public void setOnemptied(EventHandler handler);
	@ScriptSetter
	public void setOnended(EventHandler handler);
	//TODO: OnErrorEventHandler onerror;
	@ScriptSetter
	public void setOnfocus(EventHandler handler);
	@ScriptSetter
	public void setOninput(EventHandler handler);
	@ScriptSetter
	public void setOninvalid(EventHandler handler);
	@ScriptSetter
	public void setOnkeydown(EventHandler handler);
	@ScriptSetter
	public void setOnkeypress(EventHandler handler);
	@ScriptSetter
	public void setOnkeyup(EventHandler handler);
	@ScriptSetter
	public void setOnload(EventHandler handler);
	@ScriptSetter
	public void setOnloadeddata(EventHandler handler);
	@ScriptSetter
	public void setOnloadedmetadata(EventHandler handler);
	@ScriptSetter
	public void setOnloadstart(EventHandler handler);
	@ScriptSetter
	public void setOnmousedown(EventHandler handler);
	@ScriptSetter
	public void setOnmouseenter(EventHandler handler);
	@ScriptSetter
	public void setOnmouseleave(EventHandler handler);
	@ScriptSetter
	public void setOnmousemove(EventHandler handler);
	@ScriptSetter
	public void setOnmouseout(EventHandler handler);
	@ScriptSetter
	public void setOnmouseover(EventHandler handler);
	@ScriptSetter
	public void setOnmouseup(EventHandler handler);
	@ScriptSetter
	public void setOnmousewheel(EventHandler handler);
	@ScriptSetter
	public void setOnpause(EventHandler handler);
	@ScriptSetter
	public void setOnplay(EventHandler handler);
	@ScriptSetter
	public void setOnplaying(EventHandler handler);
	@ScriptSetter
	public void setOnprogress(EventHandler handler);
	@ScriptSetter
	public void setOnratechange(EventHandler handler);
	@ScriptSetter
	public void setOnreset(EventHandler handler);
	@ScriptSetter
	public void setOnresize(EventHandler handler);
	@ScriptSetter
	public void setOnscroll(EventHandler handler);
	@ScriptSetter
	public void setOnseeked(EventHandler handler);
	@ScriptSetter
	public void setOnseeking(EventHandler handler);
	@ScriptSetter
	public void setOnselect(EventHandler handler);
	@ScriptSetter
	public void setOnshow(EventHandler handler);
	@ScriptSetter
	public void setOnstalled(EventHandler handler);
	@ScriptSetter
	public void setOnsubmit(EventHandler handler);
	@ScriptSetter
	public void setOnsuspend(EventHandler handler);
	@ScriptSetter
	public void setOntimeupdate(EventHandler handler);
	@ScriptSetter
	public void setOntoggle(EventHandler handler);
	@ScriptSetter
	public void setOnvolumechange(EventHandler handler);
	@ScriptSetter
	public void setOnwaiting(EventHandler handler);
}

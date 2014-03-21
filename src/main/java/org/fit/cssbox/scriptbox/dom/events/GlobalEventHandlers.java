package org.fit.cssbox.scriptbox.dom.events;


public interface GlobalEventHandlers {
	public final static String onabort_msg = "onabort";
	public final static String onblur_msg = "onblur";
	public final static String oncancel_msg = "oncancel";
	public final static String oncanplay_msg = "oncanplay";
	public final static String oncanplaythrough_msg = "oncanplaythrough";
	public final static String onchange_msg = "onchange";
	public final static String onclick_msg = "onclick";
	public final static String onclose_msg = "onclose";
	public final static String oncuechange_msg = "oncuechange";
	public final static String ondblclick_msg = "ondblclick";
	public final static String ondrag_msg = "ondrag";
	public final static String ondragend_msg = "ondragend";
	public final static String ondragenter_msg = "ondragenter";
	public final static String ondragexit_msg = "ondragexit";
	public final static String ondragleave_msg = "ondragleave";
	public final static String ondragover_msg = "ondragover";
	public final static String ondragstart_msg = "ondragstart";
	public final static String ondrop_msg = "ondrop";
	public final static String ondurationchange_msg = "ondurationchange";
	public final static String onemptied_msg = "onemptied";
	public final static String onended_msg = "onended";
	public final static String onerror_msg = "onerror";
	public final static String onfocus_msg = "onfocus";
	public final static String oninput_msg = "oninput";
	public final static String oninvalid_msg = "oninvalid";
	public final static String onkeydown_msg = "onkeydown";
	public final static String onkeypress_msg = "onkeypress";
	public final static String onkeyup_msg = "onkeyup";
	public final static String onload_msg = "onload";
	public final static String onloadeddata_msg = "onloadeddata";
	public final static String onloadedmetadata_msg = "onloadedmetadata";
	public final static String onloadstart_msg = "onloadstart";
	public final static String onmousedown_msg = "onmousedown";
	public final static String onmouseenter_msg = "onmouseenter";
	public final static String onmouseleave_msg = "onmouseleave";
	public final static String onmousemove_msg = "onmousemove";
	public final static String onmouseout_msg = "onmouseout";
	public final static String onmouseover_msg = "onmouseover";
	public final static String onmouseup_msg = "onmouseup";
	public final static String onmousewheel_msg = "onmousewheel";
	public final static String onpause_msg = "onpause";
	public final static String onplay_msg = "onplay";
	public final static String onplaying_msg = "onplaying";
	public final static String onprogress_msg = "onprogress";
	public final static String onratechange_msg = "onratechange";
	public final static String onreset_msg = "onreset";
	public final static String onresize_msg = "onresize";
	public final static String onscroll_msg = "onscroll";
	public final static String onseeked_msg = "onseeked";
	public final static String onseeking_msg = "onseeking";
	public final static String onselect_msg = "onselect";
	public final static String onshow_msg = "onshow";
	public final static String onstalled_msg = "onstalled";
	public final static String onsubmit_msg = "onsubmit";
	public final static String onsuspend_msg = "onsuspend";
	public final static String ontimeupdate_msg = "ontimeupdate";
	public final static String ontoggle_msg = "ontoggle";
	public final static String onvolumechange_msg = "onvolumechange";
	public final static String onwaiting_msg = "onwaiting";
	
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
	//TODO: OnErrorEventHandler onerr;
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
	//TODO: OnErrorEventHandler onerror;
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

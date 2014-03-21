package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.views.AbstractView;

public class AdaptedMouseEvent<MouseEventTypeImpl extends MouseEventImpl> extends AdaptedUIEvent<MouseEventTypeImpl> {

	public AdaptedMouseEvent(MouseEventTypeImpl eventImpl) {
		super(eventImpl);
	}
	
	@ScriptGetter
	public long getScreenX() {
		return eventImpl.getScreenX();
	}
	
	@ScriptGetter
	public long getScreenY() {
		return eventImpl.getScreenY();
	}
	
	@ScriptGetter
	public long getClientX() {
		return eventImpl.getClientX();
	}
	
	@ScriptGetter
	public long getClientY() {
		return eventImpl.getClientY();
	}
	
	@ScriptGetter
	public boolean getCtrlKey() {
		return eventImpl.getCtrlKey();
	}
	
	@ScriptGetter
	public boolean getShiftKey() {
		return eventImpl.getShiftKey();
	}
	
	@ScriptGetter
	public boolean getAltKey() {
		return eventImpl.getAltKey();
	}
	
	@ScriptGetter
	public boolean getMetaKey() {
		return eventImpl.getMetaKey();
	}
	
	@ScriptGetter
	public short getButton() {
		return eventImpl.getButton();
	}
	
	@ScriptGetter
	public org.w3c.dom.events.EventTarget getRelatedTarget() {
		return eventImpl.getRelatedTarget();
	}
	  
	@ScriptFunction
    public void initMouseEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, 
            int detailArg, int screenXArg, int screenYArg, int clientXArg, int clientYArg, 
            boolean ctrlKeyArg, boolean altKeyArg, boolean shiftKeyArg, boolean metaKeyArg, 
            short buttonArg, EventTarget relatedTargetArg) {
    	eventImpl.initMouseEvent(typeArg, canBubbleArg, cancelableArg, viewArg, 
                detailArg, screenXArg, screenYArg, clientXArg, clientYArg, 
                ctrlKeyArg, altKeyArg, shiftKeyArg, metaKeyArg, 
                buttonArg, relatedTargetArg);
    }
	
	@Override
	public String toString() {
		return "[object MouseEvent]";
	}
	
	  /* TODO?:
	  // Introduced in DOM Level 3:
	  boolean            getModifierState(in DOMString keyIdentifierArg);
	  // Introduced in DOM Level 3:
	  void               initMouseEventNS(in DOMString namespaceURI, 
	                                      in DOMString typeArg, 
	                                      in boolean canBubbleArg, 
	                                      in boolean cancelableArg, 
	                                      in views::AbstractView viewArg, 
	                                      in long detailArg, 
	                                      in long screenXArg, 
	                                      in long screenYArg, 
	                                      in long clientXArg, 
	                                      in long clientYArg, 
	                                      in unsigned short buttonArg, 
	                                      in EventTarget relatedTargetArg, 
	                                      in DOMString modifiersList);
	*/
}

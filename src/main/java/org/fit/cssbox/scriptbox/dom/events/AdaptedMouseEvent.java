/**
 * AdaptedMouseEvent.java
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

import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.events.MouseEvent;
import org.w3c.dom.views.AbstractView;

public class AdaptedMouseEvent<MouseEventTypeImpl extends MouseEventImpl> extends AdaptedUIEvent<MouseEventTypeImpl> implements MouseEvent {

	public AdaptedMouseEvent(MouseEventTypeImpl eventImpl) {
		super(eventImpl);
	}
	
	@ScriptGetter
	@Override
	public int getScreenX() {
		return eventImpl.getScreenX();
	}
	
	@ScriptGetter
	@Override
	public int getScreenY() {
		return eventImpl.getScreenY();
	}
	
	@ScriptGetter
	@Override
	public int getClientX() {
		return eventImpl.getClientX();
	}
	
	@ScriptGetter
	@Override
	public int getClientY() {
		return eventImpl.getClientY();
	}
	
	@ScriptGetter
	@Override
	public boolean getCtrlKey() {
		return eventImpl.getCtrlKey();
	}
	
	@ScriptGetter
	@Override
	public boolean getShiftKey() {
		return eventImpl.getShiftKey();
	}
	
	@ScriptGetter
	@Override
	public boolean getAltKey() {
		return eventImpl.getAltKey();
	}
	
	@ScriptGetter
	@Override
	public boolean getMetaKey() {
		return eventImpl.getMetaKey();
	}
	
	@ScriptGetter
	@Override
	public short getButton() {
		return eventImpl.getButton();
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getRelatedTarget() {
		return eventImpl.getRelatedTarget();
	}
	  
	@ScriptFunction
	@Override
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

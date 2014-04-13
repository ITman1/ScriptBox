/**
 * KeyboardEvent.java
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

package org.fit.cssbox.scriptbox.dom.events.script;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.w3c.dom.views.AbstractView;

public class KeyboardEvent extends UIEvent {
	public static final String DOM_MODIFIER_CONTROL  = "Control";
	public static final String DOM_MODIFIER_SHIFT    = "Shift";
	public static final String DOM_MODIFIER_ALT      = "Alt";
	public static final String DOM_MODIFIER_META     = "Meta";
	
	// KeyLocationCode
	public static final long DOM_KEY_LOCATION_STANDARD  = 0x00;
	public static final long DOM_KEY_LOCATION_LEFT      = 0x01;
	public static final long DOM_KEY_LOCATION_RIGHT     = 0x02;
	public static final long DOM_KEY_LOCATION_NUMPAD    = 0x03;

	protected String keyIdentifier;
	protected long keyLocation;
	protected boolean ctrlKey;
	protected boolean shiftKey;
	protected boolean altKey;
	protected boolean metaKey;
	
	@ScriptGetter
	public String getKeyIdentifier() {
		return keyIdentifier;
	}
	
	@ScriptGetter
	public long getKeyLocation() {
		return keyLocation;
	}
	
	@ScriptGetter
	public boolean getCtrlKey() {
		return ctrlKey;
	}
	
	@ScriptGetter
	public boolean getShiftKey() {
		return shiftKey;
	}
	
	@ScriptGetter
	public boolean getAltKey() {
		return altKey;
	}
	
	@ScriptGetter
	public boolean getMetaKey() {
		return metaKey;
	}
	
	@ScriptFunction
	public void initKeyboardEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg,
			AbstractView viewArg, String keyIdentifierArg, long keyLocationArg, String modifiersList) {
		initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, -1);
		
		this.keyIdentifier = keyIdentifierArg;
		this.keyLocation = keyLocationArg;
		
		String[] tokensArr = modifiersList.split("\\s+");
		Set<String> tokens = new HashSet<String>(Arrays.asList(tokensArr));

		this.ctrlKey = tokens.contains(DOM_MODIFIER_CONTROL);
		this.shiftKey = tokens.contains(DOM_MODIFIER_SHIFT);
		this.metaKey = tokens.contains(DOM_MODIFIER_META);
		this.altKey = tokens.contains(DOM_MODIFIER_ALT);
	}
	
	@Override
	public String toString() {
		return "[object KeyboardEvent]";
	}
	
	/* TODO?:
	 * 	@ScriptGetter
	boolean getModifierState(String keyIdentifierArg) {
		
	}
	void               initKeyboardEventNS(in DOMString namespaceURI, 
	                                       in DOMString typeArg, 
	                                       in boolean canBubbleArg, 
	                                       in boolean cancelableArg, 
	                                       in views::AbstractView viewArg, 
	                                       in DOMString keyIdentifierArg, 
	                                       in unsigned long keyLocationArg, 
	                                       in DOMString modifiersList);
	                                       */
}

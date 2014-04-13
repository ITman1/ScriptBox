/**
 * AdaptedTrustedEvent.java
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

import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class TrustedEvent extends Event {
	protected boolean isTrusted;
	protected EventTarget targetOverride;
		
	@ScriptGetter
	public boolean getIsTrusted() {
		return isTrusted;
	}
	
	public EventTarget getTargetOverride() {
		return targetOverride;
	}

	public void initEvent(String eventTypeArg, boolean canBubbleArg, boolean cancelableArg, boolean isTrusted, EventTarget targetOverride) {
		super.initEvent(eventTypeArg, canBubbleArg, cancelableArg);
		
		this.isTrusted = isTrusted;
		this.targetOverride = targetOverride;
	}

}

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

package org.fit.cssbox.scriptbox.dom.events;

import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class AdaptedTrustedEvent extends AdaptedEvent<TrustedEventImpl> {

	public AdaptedTrustedEvent(TrustedEventImpl eventImpl) {
		super(eventImpl);
	}
	
	@ScriptGetter
	public boolean getIsTrusted() {
		return eventImpl.isTrusted;
	}
	
	@ScriptGetter
	@Override
	public String getType() {
		return super.getType();
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getTarget() {
		return super.getTarget();
	}
	
	@ScriptGetter
	@Override
	public org.w3c.dom.events.EventTarget getCurrentTarget() {
		return super.getCurrentTarget();
	}
	
	@ScriptGetter
	@Override
	public short getEventPhase() {
		return super.getEventPhase();
	}
	
	@ScriptGetter
	@Override
	public boolean getBubbles() {
		return super.getBubbles();
	}
	
	@ScriptGetter
	@Override
	public boolean getCancelable() {
		return super.getCancelable();
	}
	
	@ScriptGetter
	@Override
	public long getTimeStamp() {
		return super.getTimeStamp();
	}
	
	@ScriptFunction
	@Override
	public void stopPropagation() {
		super.stopPropagation();
	}
	
	@ScriptFunction
	@Override
	public void preventDefault() {
		super.preventDefault();
	}

}

/**
 * AdaptedUIEvent.java
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

import org.apache.xerces.dom.events.UIEventImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.w3c.dom.events.UIEvent;
import org.w3c.dom.views.AbstractView;

public class AdaptedUIEvent<UIEventTypeImpl extends UIEventImpl> extends AdaptedEvent<UIEventTypeImpl> implements UIEvent {

	public AdaptedUIEvent(UIEventTypeImpl eventImpl) {
		super(eventImpl);
	}

	@ScriptGetter
	@Override
    public AbstractView getView() {
        return eventImpl.getView();
    }

	@ScriptGetter
	@Override
    public int getDetail() {
        return eventImpl.getDetail();
    }

	@ScriptFunction
	@Override
	public String toString() {
		return "[object UIEvent]";
	}
	
	@ScriptGetter
	@Override
    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, 
    		AbstractView viewArg, int detailArg) {
    	eventImpl.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
    }
}

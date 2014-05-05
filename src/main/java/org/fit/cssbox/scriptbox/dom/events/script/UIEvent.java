/**
 * UIEvent.java
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
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.w3c.dom.views.AbstractView;

/**
 * Represents adapter class which adapts xerces event implementation into
 * implementation which is visible in scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/TR/dom/#exception-domexception">Event interface</a>
 */
public class UIEvent extends Event implements org.w3c.dom.events.UIEvent {
	protected AbstractView view;
    protected int detail;
    
    public UIEvent() {}
    
	public UIEvent(boolean isTrusted, EventTarget targetOverride) {
		super(isTrusted, targetOverride);
	}
    
	@ScriptFunction
	@Override
    public AbstractView getView() {
        return view;
    }

	@ScriptFunction
	@Override
    public int getDetail() {
        return detail;
    }

	@ScriptFunction
    @Override
    public String toString() {
    	return "[object UIEvent]";
    }
    
	@ScriptFunction
	@Override
    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, int detailArg) {
        view = viewArg;
        detail = detailArg;

        super.initEvent(typeArg, canBubbleArg, cancelableArg);
    }
}

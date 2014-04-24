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

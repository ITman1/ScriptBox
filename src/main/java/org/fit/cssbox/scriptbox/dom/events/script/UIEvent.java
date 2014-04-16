package org.fit.cssbox.scriptbox.dom.events.script;

import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.w3c.dom.views.AbstractView;

public class UIEvent extends Event {
	protected AbstractView view;
    protected int detail;
    
	@ScriptFunction
    public AbstractView getView() {
        return view;
    }

	@ScriptFunction
    public int getDetail() {
        return detail;
    }

	@ScriptFunction
    @Override
    public String toString() {
    	return "[object UIEvent]";
    }
    
	@ScriptFunction
    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, int detailArg) {
        view = viewArg;
        detail = detailArg;

        super.initEvent(typeArg, canBubbleArg, cancelableArg);
    }
}

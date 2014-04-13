package org.fit.cssbox.scriptbox.dom.events.script;

import org.w3c.dom.views.AbstractView;

public class UIEvent extends Event {
	protected AbstractView view;
    protected int detail;
    
    public AbstractView getView() {
        return view;
    }

    public int getDetail() {
        return detail;
    }

    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, AbstractView viewArg, int detailArg) {
        view = viewArg;
        detail = detailArg;

        super.initEvent(typeArg, canBubbleArg, cancelableArg);
    }
}

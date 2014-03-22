package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.UIEventImpl;
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

	@ScriptGetter
	@Override
    public void initUIEvent(String typeArg, boolean canBubbleArg, boolean cancelableArg, 
    		AbstractView viewArg, int detailArg) {
    	eventImpl.initUIEvent(typeArg, canBubbleArg, cancelableArg, viewArg, detailArg);
    }
}

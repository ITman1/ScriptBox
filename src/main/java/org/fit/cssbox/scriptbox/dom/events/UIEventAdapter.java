package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.UIEventImpl;
import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.Adapter;

public class UIEventAdapter implements Adapter {
	@Override
	public Object getProvider(Object obj) {
		if (obj instanceof UIEventImpl) {
			return new AdaptedUIEvent<UIEventImpl>((UIEventImpl)obj);
		}
		return null;
	}

	@Override
	public Class<?> getAdapteeClass() {
		return UIEventImpl.class;
	}

	@Override
	public Class<?> getResultClass() {
		return AdaptedUIEvent.class;
	}
}

package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.Adapter;

public class MouseEventAdapter implements Adapter {

	@Override
	public Object getProvider(Object obj) {
		if (obj instanceof MouseEventImpl) {
			return new AdaptedMouseEvent<MouseEventImpl>((MouseEventImpl)obj);
		}
		return null;
	}

	@Override
	public Class<?> getAdapteeClass() {
		return MouseEventImpl.class;
	}

	@Override
	public Class<?> getResultClass() {
		return AdaptedMouseEvent.class;
	}

}

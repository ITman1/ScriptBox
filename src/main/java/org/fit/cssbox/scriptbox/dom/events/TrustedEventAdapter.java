package org.fit.cssbox.scriptbox.dom.events;

import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.Adapter;

public class TrustedEventAdapter implements Adapter {
	@Override
	public Object getProvider(Object obj) {
		if (obj instanceof EventImpl) {
			return new AdaptedTrustedEvent((TrustedEventImpl)obj);
		}
		return null;
	}

	@Override
	public Class<?> getAdapteeClass() {
		return TrustedEventImpl.class;
	}

	@Override
	public Class<?> getResultClass() {
		return AdaptedTrustedEvent.class;
	}
}

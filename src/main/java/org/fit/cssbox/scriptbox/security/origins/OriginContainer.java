package org.fit.cssbox.scriptbox.security.origins;

public class OriginContainer<E extends Origin<?>> {
	private E _origin;
	private E _effectiveScriptOrigin;
	
	public OriginContainer(E origin, E effectiveScriptOrigin) {
		_origin = origin;
		_effectiveScriptOrigin = effectiveScriptOrigin;
	}

	public E getOrigin() {
		return _origin;
	}

	public E getEffectiveScriptOrigin() {
		return _effectiveScriptOrigin;
	}
}

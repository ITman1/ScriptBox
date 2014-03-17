package org.fit.cssbox.scriptbox.script.javascript.js;

import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMember;
import org.mozilla.javascript.Wrapper;

public class ScriptableOriginWrapper implements Wrapper {

	private static final long serialVersionUID = 2656404521462471628L;
	
	protected Object wrapped;
	protected ClassMember<?> origin;
	
	public ScriptableOriginWrapper(Object wrapped, ClassMember<?> origin) {		
		this.wrapped = wrapped;
		this.origin = origin;
	}

	public ClassMember<?> getOrigin() {
		return origin;
	}

	@Override
	public Object unwrap() {
		return wrapped;
	}
}

package org.fit.cssbox.scriptbox.script.javascript.wrap;

import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.SanboxedJavaObject;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.Shutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class SandboxWrapFactoryDecorator extends WrapFactoryDecorator {

	protected Shutter shutter;
	
	public SandboxWrapFactoryDecorator(Shutter shutter) {
		this(shutter, null);
	}
	
	public SandboxWrapFactoryDecorator(Shutter shutter, WrapFactoryDecorator decorator) {
		super(decorator);
		
		this.shutter = shutter;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		Scriptable returnedObject = super.wrapAsJavaObject(cx, scope, javaObject, staticType);
		Scriptable sandboxedObject = new SanboxedJavaObject(returnedObject, shutter);
		
		return sandboxedObject;
	}
}

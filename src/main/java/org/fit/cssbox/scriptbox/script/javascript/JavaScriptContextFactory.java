package org.fit.cssbox.scriptbox.script.javascript;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.SandboxWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.SandBoxClassShutter;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class JavaScriptContextFactory extends ContextFactory {
		
	protected ScriptAnnotationShutter shutter;
	
	public JavaScriptContextFactory(BrowserScriptEngine scriptEngine) {
		shutter = new ScriptAnnotationShutter(scriptEngine);
	}
	
	@Override
	public boolean hasFeature(Context cx, int feature) {
		if (feature == Context.FEATURE_E4X) {
			return false;
		} else {
			return super.hasFeature(cx, feature);
		}
	}
	
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		
		ClassShutter classShutter = new SandBoxClassShutter(shutter);
		WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
		wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);
		wrapFactoryDecorator = new SandboxWrapFactoryDecorator(shutter, wrapFactoryDecorator);

		cx.setClassShutter(classShutter);
		cx.setWrapFactory(wrapFactoryDecorator);
		
		return cx;
	}
}

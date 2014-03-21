package org.fit.cssbox.scriptbox.script.javascript;

import org.fit.cssbox.scriptbox.dom.events.EventAdapter;
import org.fit.cssbox.scriptbox.dom.events.MouseEventAdapter;
import org.fit.cssbox.scriptbox.dom.events.UIEventAdapter;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.annotation.ScriptAnnotationClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.annotation.ScriptAnnotationShutter;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.wrap.AdapterWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.SandboxWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.adapter.AdapterRegistry;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.SandBoxClassShutter;
import org.mozilla.javascript.ClassShutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

public class JavaScriptContextFactory extends ContextFactory {
		
	protected BrowserScriptEngine scriptEngine;
	protected ScriptAnnotationShutter shutter;
	protected ClassMembersResolverFactory membersResolverFactory;
	protected AdapterRegistry adapterRegistry;
	
	public JavaScriptContextFactory(BrowserScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
		this.shutter = new ScriptAnnotationShutter(scriptEngine);
		this.membersResolverFactory = new ScriptAnnotationClassMembersResolverFactory(scriptEngine);
		this.adapterRegistry = new AdapterRegistry();
		
		this.adapterRegistry.registerAdapter(EventAdapter.class);
		this.adapterRegistry.registerAdapter(UIEventAdapter.class);
		this.adapterRegistry.registerAdapter(MouseEventAdapter.class);
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
		WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator(membersResolverFactory);
		wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);
		wrapFactoryDecorator = new AdapterWrapFactoryDecorator(adapterRegistry, wrapFactoryDecorator);
		//wrapFactoryDecorator = new SandboxWrapFactoryDecorator(shutter, wrapFactoryDecorator);

		cx.setClassShutter(classShutter);
		cx.setWrapFactory(wrapFactoryDecorator);
		
		return cx;
	}
}

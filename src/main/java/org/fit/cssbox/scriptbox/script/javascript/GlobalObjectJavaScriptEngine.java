package org.fit.cssbox.scriptbox.script.javascript;

import org.apache.xerces.dom.ElementImpl;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.DefaultShutter;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

public class GlobalObjectJavaScriptEngine extends JavaScriptEngine {

	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings, contextFactory);
	}
	
	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
	}

	@Override
	protected TopLevel initializeTopLevel() {
		Object object = scriptSettings.getGlobalObject();
		return new ObjectTopLevel(object, this);
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();
		explicitGrantShutter.addVisibleClass(ElementImpl.class, true, false);
		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}
}

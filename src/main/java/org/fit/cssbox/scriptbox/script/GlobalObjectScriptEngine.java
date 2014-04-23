package org.fit.cssbox.scriptbox.script;

import org.apache.html.dom.HTMLElementImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotationClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.DefaultShutter;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;

public abstract class GlobalObjectScriptEngine extends BrowserScriptEngine {

	public GlobalObjectScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();
		explicitGrantShutter.addVisibleClass(HTMLElementImpl.class, true, false, false);
		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}

}

package org.fit.cssbox.scriptbox.script;

import org.apache.html.dom.HTMLElementImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotationClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultShutter;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;

/**
 * Abstract class representing JSR 223 compliant base class  
 * for all script engines that have set global object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class GlobalObjectScriptEngine extends BrowserScriptEngine {

	protected Object globalObject;
	
	/**
	 * Constructs global object script engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 * @param globalObject Global object that should be implemented.
	 */
	public GlobalObjectScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, Object globalObject) {
		super(factory, scriptSettings);
		
		this.globalObject = globalObject;
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();
		explicitGrantShutter.addVisibleClass(HTMLElementImpl.class, true, false, false);
		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}

}

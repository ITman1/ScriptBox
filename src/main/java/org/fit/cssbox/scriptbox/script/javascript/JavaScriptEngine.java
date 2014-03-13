package org.fit.cssbox.scriptbox.script.javascript;

import java.io.Reader;
import java.io.StringReader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class JavaScriptEngine extends BrowserScriptEngine {

	static {
		ContextFactory globalFactory = new JavaScriptContextFactory(null);
		ContextFactory.initGlobal(globalFactory);
	}

	protected ContextFactory contextFactory;
	protected TopLevel topLevel;
	
	public JavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		this(factory, scriptSettings, null);
	}
	
	public JavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings);

		this.contextFactory = (contextFactory != null)? contextFactory : new JavaScriptContextFactory(this);
		topLevel = initializeTopLevel();
	}
	
	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret;

		Context cx = enterContext();
		try {
			Scriptable windowScope = getRuntimeScope(context);
			ret = cx.evaluateReader(windowScope, reader, "<inline script>" , 1,  null);
		} catch (Exception ex) {
			throw new ScriptException(ex);
		} finally {
			exitContext();
		}

		return unwrap(ret);
	}

	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return eval(new StringReader(script) , context);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	public Context enterContext() {
		return contextFactory.enterContext();
	}
	
	public void exitContext() {
		Context.exit();
	}
	
	protected TopLevel initializeTopLevel() {
		TopLevel topLevel = new TopLevel();
		
		Context cx = enterContext();
		try {
			cx.initStandardObjects(topLevel, true);
		} finally {
			exitContext();
		}
		
		return topLevel;
	}
	
	protected Scriptable getRuntimeScope(ScriptContext context) throws ScriptException {		
		Scriptable windowScope = new ScriptContextScriptable(context);
		
		windowScope.setPrototype(topLevel);
		windowScope.setParentScope(null);
		
		return windowScope;
	}
	
	protected Object unwrap(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Wrapper) {
			value = ((Wrapper)value).unwrap();
		}

		if (value == null || value instanceof Undefined) {
			return null;
		} else {
			return value;
		}
	}
}

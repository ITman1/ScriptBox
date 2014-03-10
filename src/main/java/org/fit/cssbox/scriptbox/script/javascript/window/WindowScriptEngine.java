package org.fit.cssbox.scriptbox.script.javascript.window;

import java.io.Reader;
import java.io.StringReader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.ContextScriptable;
import org.fit.cssbox.scriptbox.script.javascript.ObjectTopLevel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;


public class WindowScriptEngine extends BrowserScriptEngine {

	static {
		ContextFactory.initGlobal(new ContextFactory() {
			@Override
			protected Context makeContext() {
				Context cx = super.makeContext();
				// TODO: Customize class shutter and wrapper factory
				return cx;
			}

			@Override
			public boolean hasFeature(Context cx, int feature) {
				if (feature == Context.FEATURE_E4X) {
					return false;
				} else {
					return super.hasFeature(cx, feature);
				}
			}
		});
	}

	private Window window;
	private ObjectTopLevel windowTopLevel;
	
	public static Context enterContext() {
		return Context.enter();
	}
	
	public static void exitContext() {
		Context.exit();
	}
	
	public WindowScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
		
		window = scriptSettings.getGlobalObject();
		windowTopLevel = new ObjectTopLevel(window, this);
	}
	
	public Window getWindow() {
		return window;
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret;

		Context cx = enterContext();
		try {
			Scriptable windowScope = getWindowScope(context);
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

	protected Scriptable getWindowScope(ScriptContext context) throws ScriptException {		
		Scriptable windowScope = new ContextScriptable(context);
		
		windowScope.setPrototype(windowTopLevel);
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

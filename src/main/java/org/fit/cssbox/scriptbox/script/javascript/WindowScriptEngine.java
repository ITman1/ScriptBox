package org.fit.cssbox.scriptbox.script.javascript;

import java.io.IOException;
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
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
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

	private WindowTopLevel windowTopLevel;
	
	public static Context enterContext() {
		return Context.enter();
	}
	
	public WindowScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
		
		Window window = scriptSettings.getGlobalObject();
		windowTopLevel = new WindowTopLevel(window);
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret;

		Context cx = enterContext();
		try {
			Scriptable scope = getExecutionScope(context);
			ret = cx.evaluateReader(scope, reader, "<inline script>" , 1,  null);
		} catch (RhinoException rhinoException) {
			int line = rhinoException.lineNumber();
			line = (line == 0)? -1 : line;
			
			String msg;
			if (rhinoException instanceof JavaScriptException) {
				msg = String.valueOf(((JavaScriptException)rhinoException).getValue());
			} else {
				msg = rhinoException.toString();
			}
			
			ScriptException se = new ScriptException(msg, rhinoException.sourceName(), line);
			se.initCause(rhinoException);
			throw se;
			
		} catch (IOException ex) {
			throw new ScriptException(ex);
		} finally {
			Context.exit();
		}

		return unwrap(ret);
	}

	@Override
	public Object eval(String script, ScriptContext ctxt) throws ScriptException {
		return eval(new StringReader(script) , ctxt);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	protected Scriptable getExecutionScope(ScriptContext context) {		
		Scriptable newScope = new ScriptContextScriptable(context);
		
		newScope.setPrototype(windowTopLevel);
		newScope.setParentScope(null);

		return newScope;
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

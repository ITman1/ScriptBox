package org.fit.cssbox.scriptbox.script.javascript;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.FunctionObject;
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

	private Window window;
	private WindowTopLevel windowTopLevel;
	
	public static Context enterContext() {
		return Context.enter();
	}
	
	public WindowScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
		
		window = scriptSettings.getGlobalObject();
		windowTopLevel = new WindowTopLevel(window);
	}

	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret;

		Context cx = enterContext();
		try {
			Scriptable windowScope = getWindowScope(context);
			ret = cx.evaluateReader(windowScope, reader, "<inline script>" , 1,  null);
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
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return eval(new StringReader(script) , context);
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	protected Scriptable getWindowScope(ScriptContext context) throws ScriptException {		
		Scriptable windowScope = new WindowScriptable(context);
		
		windowScope.setPrototype(windowTopLevel);
		windowScope.setParentScope(null);
		implementWindow(windowScope, context);
		
		return windowScope;
	}

	protected void implementWindow(Scriptable windowScope, ScriptContext context) throws ScriptException {
		Class<?> windowClass = window.getClass();
		for (Method method : windowClass.getMethods()){
			if (method.isAnnotationPresent(ScriptGetter.class)) {
				implementWindowGetter(method);
			} else if (method.isAnnotationPresent(ScriptFunction.class)) {
				implementWindowFunction(method, windowScope);
			} 
		}
	}
	
	protected void implementWindowFunction(Method method, Scriptable scope) throws ScriptException {
		Annotation functionAnnotation = method.getAnnotation(ScriptFunction.class);   
		boolean isSupported = ScriptAnnotation.isEngineSupported(functionAnnotation, this);
		
		if (isSupported && functionAnnotation != null) {
			String methodName = method.getName();
			try {
				FunctionObject functionObject = new FunctionObject(methodName, method, scope);
				putIntoEngineScope(methodName, functionObject);
			} catch (Exception e) {
				throw new ScriptException(e);
			}

		}
	}
	
	protected void implementWindowGetter(Method method) throws ScriptException {
		Annotation getterAnnotation = method.getAnnotation(ScriptGetter.class);  
		boolean isSupported = ScriptAnnotation.isEngineSupported(getterAnnotation, this);
		
		if (isSupported && getterAnnotation != null) {
			String methodName = method.getName();
			String fieldName = ScriptAnnotation.getFieldFromGetterName(methodName);
			try {
				Object fieldObject = method.invoke(window);
				putIntoEngineScope(fieldName, fieldObject);
			} catch (Exception e) {
				throw new ScriptException(e);
			}
		}
	}
	
	protected void putIntoEngineScope(String name, Object value) {
        Bindings nn = context.getBindings(ScriptContext.ENGINE_SCOPE);
        if (nn != null) {
            nn.put(name, value);
        }
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

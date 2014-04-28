/**
 * WindowJavaScriptEngine.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script.javascript;

import java.io.Reader;
import java.io.StringReader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.html.dom.HTMLElementImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.WindowScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotationClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultShutter;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Wrapper;

/**
 * JavaScript engine for the browser. It implements the Window object into 
 * top level scope and wraps this scope by ScriptContextScriptable above 
 * which runs the scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class WindowJavaScriptEngine extends WindowScriptEngine implements Invocable {

	public static final String JAVASCRIPT_LANGUAGE = "text/javascript";
	
	/*static {
		ContextFactory globalFactory = new JavaScriptContextFactory();
		ContextFactory.initGlobal(globalFactory);
	}*/

	protected ContextFactory contextFactory;
	protected TopLevel topLevel;
	protected Scriptable runtimeScope;
	
	/**
	 * Constructs window JavaScript engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 */
	public WindowJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		this(factory, scriptSettings, null);
	}
	
	/**
	 * Constructs window JavaScript engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 * @param contextFactory Context factory to be used for this script engine.
	 */
	public WindowJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings);
		
		this.contextFactory = (contextFactory != null)? contextFactory : new JavaScriptContextFactory(this);

		this.topLevel = initializeTopLevel();
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();
		explicitGrantShutter.addVisibleClass(HTMLElementImpl.class, true, false, false);
		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}
		
	@Override
	public Object eval(Reader reader, ScriptContext context) throws ScriptException {
		Object ret = null;

		Context cx = enterContext();
		try {
			Scriptable executionScope = getExecutionScope(context);
			ret = cx.evaluateReader(executionScope, reader, "<inline script>" , 1,  null);
		} catch (Exception ex) {
			throwWrappedScriptException(ex);
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

	/**
	 * Enters new context.
	 * 
	 * @return New entered context.
	 */
	public Context enterContext() {
		return contextFactory.enterContext();
	}
	
	/**
	 * Exits opened context for this thread.
	 */
	public void exitContext() {
		Context.exit();
	}
	
	@Override
	public Object invokeMethod(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
		//throw new UnsupportedOperationException("getInterface() is not implemented yet!");
		return invoke(thiz, name, args);
	}

	@Override
	public Object invokeFunction(String name, Object... args) throws ScriptException, NoSuchMethodException {
		//throw new UnsupportedOperationException("getInterface() is not implemented yet!");
		return invoke(null, name, args);
	}

	@Override
	public <T> T getInterface(Class<T> clasz) {
		throw new UnsupportedOperationException("getInterface() is not implemented yet!");
	}

	@Override
	public <T> T getInterface(Object thiz, Class<T> clasz) {
		throw new UnsupportedOperationException("getInterface() is not implemented yet!");
	}
	
	/**
	 * Wraps given exception and throws it as ScriptException.
	 * 
	 * @param ex Exception to be wrapped.
	 * @throws ScriptException Thrown always by this method.
	 */
	public static void throwWrappedScriptException(Exception ex) throws ScriptException {
		if ( ex instanceof RhinoException) {
			RhinoException rhinoException = (RhinoException)ex;
			int line = rhinoException.lineNumber();
			int column = rhinoException.columnNumber();
			
			String message;
			if (ex instanceof JavaScriptException) {
				message = String.valueOf(((JavaScriptException)ex).getValue());
			} else {
				message = ex.toString();
			}
			
			ScriptException scriptException = new ScriptException(message, rhinoException.sourceName(), line, column);
			scriptException.initCause(ex);
			throw scriptException;
		} else {
			throw new ScriptException(ex);
		} 
	}
	
	/**
	 * Converts JavaScript object to Java object, e.g. HostedJavaObject into wrapped Java object.
	 * 
	 * @param jsObj JavaScript object to be converted.
	 * @return Converted Java object.
	 */
	public static Object jsToJava(Object jsObj) {
		if (jsObj instanceof Wrapper) {
			Wrapper njb = (Wrapper) jsObj;

			if (njb instanceof NativeJavaClass) {
				return njb;
			}

			Object obj = njb.unwrap();
			if (obj instanceof Number || obj instanceof String ||
				obj instanceof Boolean || obj instanceof Character) {
				return njb;
			} else {
				return obj;
			}
		} else {
			return jsObj;
		}
	}
	
	/**
	 * Converts Java object into JavaScript object.
	 * 
	 * @param object Object to be converted.
	 * @param scope Scope to be used as parent scope.
	 * @return New converted JavaScript object.
	 */
	public static Object javaToJS(Object object, Scriptable scope) {
		return Context.javaToJS(object, scope);
	}
	
	/**
	 * Converts array of Java objects into array of JavaScript objects.
	 * 
	 * @param args Arguments to be converted.
	 * @param scope Top scope object
	 * @return Array of converted JavaScript objects.
	 */
	public static Object[] javaToJS(Object[] args, Scriptable scope) {
		Object[] wrapped = new Object[args.length];
		
		for (int i = 0; i < wrapped.length; i++) {
			wrapped[i] = javaToJS(args[i], scope);
		}
		
		return wrapped;
	}
	
	/**
	 * Initializes global top level scope.
	 * 
	 * @return New top level scope.
	 */
	protected TopLevel initializeTopLevel() {
		Object object = (scriptSettings != null)? scriptSettings.getGlobalObject() : null;
		
		if (object == null) {
			TopLevel topLevel = new TopLevel();
			
			Context cx = enterContext();
			try {
				cx.initStandardObjects(topLevel, true);
			} finally {
				exitContext();
			}
			
			return topLevel;
		}

		return new ObjectTopLevel(object, this);
	}
	
	/**
	 * Returns scope for running the scripts.
	 * 
	 * @param context Script context to be included into top level scope.
	 * @return New scope constructed from the top level scope and wrapped script context scope.
	 */
	protected Scriptable getExecutionScope(ScriptContext context) {		
		if (runtimeScope == null) {
			runtimeScope = new ScriptContextScriptable(context);
			
			runtimeScope.setPrototype(topLevel);
			runtimeScope.setParentScope(null);
		}
		
		return runtimeScope;
	}
	
	/**
	 * Unwraps passed value from JavaScript wrapper interface.
	 * 
	 * @param value Value to be unwrapped.
	 * @return Unwrapped value.
	 */
	protected Object unwrap(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Wrapper) {
			value = ((Wrapper)value).unwrap();
		}

		return value;
	}

	private Object invoke(Object thiz, String name, Object... args) throws ScriptException, NoSuchMethodException {
		Object ret = null;
		Context cx = enterContext();
		try {
			Scriptable executionScope = getExecutionScope(context);
			Scriptable functionScope = null;
			
			if (thiz == null) {
				functionScope = executionScope;
			} else {
				if (!(thiz instanceof Scriptable)) {
					thiz = Context.toObject(thiz, topLevel);
				}
				functionScope = (Scriptable) thiz;
			}

			Function function = null;
			
			if (name != null && !name.isEmpty()) {
				Object objectProperty = ObjectScriptable.getProperty(functionScope, name);
				if (!(objectProperty instanceof Function)) {
					throw new NoSuchMethodException("Function not found!");
				}

				function = (Function) objectProperty;
			} else if (thiz instanceof Function) {
				function = (Function)thiz;
			} else {
				throw new NoSuchMethodException("Passed function name is empty and passed thiz object is not function!");
			}
	 
			Scriptable parentScope = function.getParentScope();
			if (parentScope == null) {
				parentScope = functionScope;
			}
			
			Object[] callArgs = javaToJS(args, topLevel);
			ret = function.call(cx, parentScope, functionScope, callArgs);
		} catch (Exception ex) {
			throwWrappedScriptException(ex);
		} finally {
			exitContext();
		}
		
		return unwrap(ret);
	}
	

}

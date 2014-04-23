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
import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

import org.apache.html.dom.HTMLElementImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.WindowScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotationClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.DefaultShutter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.window.WindowScriptSettings;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Wrapper;

public class WindowJavaScriptEngine extends WindowScriptEngine {

	public static final String JAVASCRIPT_LANGUAGE = "text/javascript";
	
	static {
		ContextFactory globalFactory = new JavaScriptContextFactory();
		ContextFactory.initGlobal(globalFactory);
	}

	protected ContextFactory contextFactory;
	protected TopLevel topLevel;
	protected Scriptable runtimeScope;
	
	public WindowJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		this(factory, scriptSettings, null);
	}
	
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
			Scriptable executionScope = getRuntimeScope(context);
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

	public Context enterContext() {
		return contextFactory.enterContext();
	}
	
	public void exitContext() {
		Context.exit();
	}
	
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
	
	public static Object javaToJS(Object object, Scriptable scope) {
		return Context.javaToJS(object, scope);
	}
	
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
	
	protected Scriptable getRuntimeScope(ScriptContext context) throws ScriptException {		
		if (runtimeScope == null) {
			runtimeScope = new ScriptContextScriptable(context);
			
			runtimeScope.setPrototype(topLevel);
			runtimeScope.setParentScope(null);
		}
		
		return runtimeScope;
	}
	
	protected Object unwrap(Object value) {
		if (value == null) {
			return null;
		}
		
		if (value instanceof Wrapper) {
			value = ((Wrapper)value).unwrap();
		}

		return value;
	}
}

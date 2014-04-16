/**
 * JavaScriptEngine.java
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

import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.java.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.DefaultClassMembersResolverFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.RhinoException;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Wrapper;

public class JavaScriptEngine extends BrowserScriptEngine {
	public static final String JAVASCRIPT_LANGUAGE = "text/javascript";
	
	static {
		ContextFactory globalFactory = new JavaScriptContextFactory();
		ContextFactory.initGlobal(globalFactory);
	}

	protected ContextFactory contextFactory;
	protected TopLevel topLevel;
	protected ClassMembersResolverFactory membersFactory;
	protected Scriptable runtimeScope;
	
	public JavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		this(factory, scriptSettings, null);
	}
	
	public JavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings);
		
		// This must be set before... JavaScriptContextFactory needs to know members factory
		this.membersFactory = initializeClassMembersResolverFactory();
		
		this.contextFactory = (contextFactory != null)? contextFactory : new JavaScriptContextFactory(this);

		this.topLevel = initializeTopLevel();
	}
	
	public ClassMembersResolverFactory getClassMembersResolverFactory() {
		return membersFactory;
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
	
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		return new DefaultClassMembersResolverFactory();
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

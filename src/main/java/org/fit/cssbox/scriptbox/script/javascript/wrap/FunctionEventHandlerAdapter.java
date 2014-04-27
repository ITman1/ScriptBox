/**
 * FunctionEventHandlerAdapter.java
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

package org.fit.cssbox.scriptbox.script.javascript.wrap;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.exceptions.WrappedException;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.events.Event;

/**
 * Adapter class that adapts native JavaScript function object into EventHandler.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class FunctionEventHandlerAdapter implements EventHandler {
	protected Function function;
	
	/**
	 * Constructs new adapter for given function.
	 * 
	 * @param function Function to be adapted.
	 */
	public FunctionEventHandlerAdapter(Function function) {
		this.function = function;
	}
	
	@Override
	public void handleEvent(Event event) {
		Scriptable scope = function.getParentScope();
		ObjectTopLevel topLevel = getObjectTopLevel(scope);
		if (topLevel != null) {
			Context cx = topLevel.getBrowserScriptEngine().enterContext();
			try {
				Object arg = WindowJavaScriptEngine.javaToJS(event, scope);
				Object[] args = {arg};
				function.call(cx, scope, scope, args);
			} catch (Exception ex) {
				try {
					WindowJavaScriptEngine.throwWrappedScriptException(ex);
				} catch (ScriptException e) {
					throw new WrappedException(e);
				}
			} finally {
				Context.exit();
			}
		}
	}

	/**
	 * Returns adapted function.
	 * 
	 * @return Adapted function.
	 */
	public Function getFunction() {
		return function;
	}
	
	/**
	 * Returns top level scope for the passed scope.
	 * 
	 * @param scope Scope for which should be returned the top level scope.
	 * @return Top level scope for the passed scope.
	 */
	protected ObjectTopLevel getObjectTopLevel(Scriptable scope) {
		Scriptable parentScope;
		while ((parentScope = scope.getParentScope()) != null) {
			scope = parentScope;
		}
		
		Scriptable prototypeScope = scope.getPrototype();
		if (prototypeScope instanceof ObjectTopLevel) {
			return (ObjectTopLevel)prototypeScope;
		}
		
		return null;
	}
}

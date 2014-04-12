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

package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.dom.events.EventHandler;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.w3c.dom.events.Event;

public class FunctionEventHandlerAdapter implements EventHandler {
	protected Function function;
	
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
				Object arg = ObjectScriptable.javaToJS(event, scope);
				Object[] args = {arg};
				function.call(cx, scope, scope, args);
			} finally {
				Context.exit();
			}
		}
	}

	public Function getFunction() {
		return function;
	}
	
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

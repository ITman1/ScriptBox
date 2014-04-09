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

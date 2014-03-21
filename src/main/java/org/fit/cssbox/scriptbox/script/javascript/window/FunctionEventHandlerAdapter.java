package org.fit.cssbox.scriptbox.script.javascript.window;

import org.fit.cssbox.scriptbox.dom.events.EventHandler;
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
		Context cx = Context.getCurrentContext();
		Scriptable scope = function.getParentScope();
		Object[] args = {event};
		function.call(cx, scope, function, args);
	}

	public Function getFunction() {
		return function;
	}
}

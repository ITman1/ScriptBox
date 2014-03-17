package org.fit.cssbox.scriptbox.script.javascript;

import java.util.HashSet;
import java.util.Set;

import javax.script.Bindings;
import javax.script.ScriptContext;

import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptContextScriptable extends ScriptableObject {
	private static final long serialVersionUID = 1531587729453175461L;

	private ScriptContext context;

	public ScriptContextScriptable(ScriptContext context) {
		this.context = context;
	}

	public String getClassName() {
		return "ContextScriptable";
	}
	
	public ScriptContext getContext() {
		return context;
	}

	@Override
	public boolean has(String name, Scriptable start) {
		synchronized (context) {
			return context.getAttributesScope(name) != -1;
		}
	}
	
	@Override
	public Object get(int index, Scriptable start) {
		return super.get(index, start);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		synchronized (context) {
			int sourceScope = context.getAttributesScope(name);
			if (sourceScope != -1) {
				Object value = context.getAttribute(name, sourceScope);
				return Context.javaToJS(value, this);
			} else {
				return NOT_FOUND;
			}
		}
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		if (start == this) {
			synchronized (context) {
				int destinationScope = context.getAttributesScope(name);
				if (destinationScope == -1) {
					destinationScope = ScriptContext.ENGINE_SCOPE;
				}
				context.setAttribute(name, ObjectScriptable.jsToJava(value), destinationScope);
			}
		} else {
			start.put(name, start, value);
		}
	}

	@Override
	public void delete(String name) {
		synchronized (context) {
			int sourceScope = context.getAttributesScope(name);
			if (sourceScope != -1) {
				context.removeAttribute(name, sourceScope);
			}
		}
	}

	@Override
	public Object[] getIds() {
		Set<String> idSet = new HashSet<String>();
		
		synchronized (context) {
			for (int scope : context.getScopes()) {
				Bindings bindings = context.getBindings(scope);
				if (bindings != null) {
					for (String id : bindings.keySet()) {
						idSet.add(id);
					}
				}
			}
		}
		
		return idSet.toArray(new String[idSet.size()]);
	}
}

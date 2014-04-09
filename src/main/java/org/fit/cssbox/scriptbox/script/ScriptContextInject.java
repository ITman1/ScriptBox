package org.fit.cssbox.scriptbox.script;

import javax.script.ScriptContext;

public abstract class ScriptContextInject implements Comparable<ScriptContextInject> {
	public static final int ZERO_PRIORITY = 0;
	
	public abstract boolean inject(ScriptContext context);
	
	public int getPriority() {
		return ZERO_PRIORITY;
	}
	
	public boolean isValid(ScriptContext context) {
		return true;
	}
	
	@Override
	public int compareTo(ScriptContextInject o) {
		return getPriority() - o.getPriority();
	}
}

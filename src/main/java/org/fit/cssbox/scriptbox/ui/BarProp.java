package org.fit.cssbox.scriptbox.ui;

import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public abstract class BarProp {
	@ScriptGetter
	public abstract boolean getVisible();
	
	@Override
	public String toString() {
		return "[object BarProp]";
	}
}

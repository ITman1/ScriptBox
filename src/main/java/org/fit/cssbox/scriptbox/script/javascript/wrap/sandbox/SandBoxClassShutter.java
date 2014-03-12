package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.ClassShutter;

public class SandBoxClassShutter implements ClassShutter {
	private final Map<String, Boolean> visitedClasses;

	protected Shutter shutter;
	
	public SandBoxClassShutter(Shutter shutter) {
		this.shutter = shutter;
		this.visitedClasses = new HashMap<String, Boolean>();
	}
	
	@Override
	public boolean visibleToScripts(String name) {
		Boolean isVisible = visitedClasses.get(name);

		if (isVisible != null) {
			return isVisible.booleanValue();
		}

		Class<?> clazz;
		try {
			clazz = Class.forName(name);
		} catch (Exception exc) {
			visitedClasses.put(name, false);
			return false;
		}

		isVisible = shutter.isClassVisible(clazz);
		visitedClasses.put(name, isVisible);
		
		return isVisible;
	}
}

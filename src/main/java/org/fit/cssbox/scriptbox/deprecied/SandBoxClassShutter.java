/**
 * SandBoxClassShutter.java
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

package org.fit.cssbox.scriptbox.deprecied;

import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.Shutter;
import org.mozilla.javascript.ClassShutter;

/** 
 * @deprecated
 */
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

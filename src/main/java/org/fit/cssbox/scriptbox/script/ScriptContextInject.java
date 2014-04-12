/**
 * ScriptContextInject.java
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

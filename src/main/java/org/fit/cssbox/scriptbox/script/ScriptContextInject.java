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

/**
 * Abstract class for constructing the injects which serve for putting custom objects
 * inside script context when script engine is being initialized.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ScriptContextInject implements Comparable<ScriptContextInject> {
	public static final int ZERO_PRIORITY = 0;
	
	public abstract boolean inject(ScriptContext context);
	
	/**
	 * Returns priority which have this script context inject.
	 * 
	 * @return Priority which have this script context inject.
	 */
	public int getPriority() {
		return ZERO_PRIORITY;
	}
	
	/**
	 * Tests whether is this script context inject valid for a given script context.
	 * 
	 * @param context Script context against which to be tested.
	 * @return True if is script context valid, otherwise false.
	 */
	public boolean isValid(ScriptContext context) {
		return true;
	}
	
	@Override
	public int compareTo(ScriptContextInject o) {
		return getPriority() - o.getPriority();
	}
}

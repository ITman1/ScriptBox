/**
 * StateObject.java
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

package org.fit.cssbox.scriptbox.history;

/**
 * Represents class for all state objects of the session history.
 * Implements clone() method which ensures cloning of the object when 
 * they are pushed and popped into session history.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#state-object">State object</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class StateObject implements Cloneable {
	protected Object object;
	
	public StateObject (Object object) {
		this.object = object;
	}
	
	/*
	 * TODO:
	 * http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#structured-clone
	 */
	@Override
	public StateObject clone() {
		return this;
	}
	
	public Object getObject() {
		return object;
	}
}

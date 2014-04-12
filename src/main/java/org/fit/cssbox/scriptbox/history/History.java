/**
 * History.java
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

import org.fit.cssbox.scriptbox.browser.BrowsingContext;

public class History {
	
	/*readonly attribute*/
	protected long length;
	
	/*readonly attribute*/ 
	protected Object state;
	
	private BrowsingContext context;
	
	public History(BrowsingContext context) {
		this.context = context;
	}
	
	public void go(long delta) {
		
	}
	
	public void back() {
		
	}
	
	public void forward() {
		
	}
		
	public void pushState(Object data, /*DOMString*/ String title, /*DOMString*/ String url) {
		
	}

	public void pushState(Object data, /*DOMString*/ String title) {
		
	}
	
	public void replaceState(Object data, /*DOMString*/ String title, /*DOMString*/ String url) {
		
	}
	
	public void replaceState(Object data, /*DOMString*/ String title) {
		
	}
	
	@Override
	public String toString() {
		return "[object History]";
	}
}

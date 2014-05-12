/**
 * Html5ElementImpl.java
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

package org.fit.cssbox.scriptbox.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLElementImpl;

/*
 * TODO: Implement.
 */
/**
 * Extends DOM4 HTMLElementImpl about features of the HTML5.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#htmlelement">HTML element</a>
 */
public class Html5ElementImpl extends HTMLElementImpl {
	private static final long serialVersionUID = 3621278051202593807L;

	/**
	 * Constructs new element.
	 * 
	 * @param owner Document which owns this element.
	 * @param name Name of the element.
	 */
	public Html5ElementImpl(HTMLDocumentImpl owner, String tagName) {
		super(owner, tagName);
	}
}

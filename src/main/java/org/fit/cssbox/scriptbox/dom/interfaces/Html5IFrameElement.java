/**
 * Html5IFrameElement.java
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

package org.fit.cssbox.scriptbox.dom.interfaces;

/**
 * FIXME: Add missing methods according to the HTML5.
 */
/**
 * Extends DOM4 HTMLElementImpl about features of the HTML5.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#htmliframeelement">HTML IFrame Element</a>
 */
public interface Html5IFrameElement extends Html5Element {
	/**
	 * Returns true if there is any seamless attribute.
	 * 
	 * @return True if there is any seamless attribute, otherwise false.
	 */
	public boolean getSeamless();
	
	/**
	 * Sets or removes seamless attribute.
	 * 
	 * @param seamless If is passed true, then sets seamless attribute, otherwise removes this attribute.
	 */
	public void setSeamless(boolean seamless);
}

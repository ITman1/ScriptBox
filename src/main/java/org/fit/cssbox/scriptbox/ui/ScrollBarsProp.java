/**
 * ScrollBarsProp.java
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

package org.fit.cssbox.scriptbox.ui;

/**
 * Abstract class for browser UI scrollbars.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#barprop">BarProp interface</a>
 */
public abstract class ScrollBarsProp extends BarProp {
	/**
	 * Scrolls to the given position.
	 * 
	 * @param xCoord X coordinate.
	 * @param yCoord Y coordinate.
	 */
	public abstract void scroll(int xCoord, int yCoord);
	
	/**
	 * Scrolls to a given URL fragment.
	 * 
	 * @param fragment Fragment where to scroll.
	 * @return True if scrolling was successful, otherwise false.
	 */
	public abstract boolean scrollToFragment(String fragment);
	
	/**
	 * Returns position of the horizontal scrollbar.
	 * 
	 * @return Position of the horizontal scrollbar.
	 */
	public abstract int getScrollPositionX();
	
	/**
	 * Returns position of the vertical scrollbar.
	 * 
	 * @return Position of the vertical scrollbar.
	 */
	public abstract int getScrollPositionY();
}

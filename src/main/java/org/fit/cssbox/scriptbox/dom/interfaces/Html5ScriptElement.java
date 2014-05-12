/**
 * Html5ScriptElement.java
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
 * Defines interface for script elements according to HTML5.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#the-script-element">HTML script element</a>
 */
public interface Html5ScriptElement extends Html5Element {
	/**
	 * Returns <code>src</code> attribute if there is any.
	 * 
	 * @return <code>src</code> attribute if there is any, otherwise false.
	 */
	public String getSrc();
	
	/**
	 * Sets or removes <code>src</code> attribute.
	 * 
	 * @param src New value of the <code>src</code> attribute.
	 */
	public void  setSrc(String src);
	
	/**
	 * Returns true if there is any <code>async</code> attribute.
	 * 
	 * @return True if there is any <code>async</code> attribute, otherwise false.
	 */
	public boolean getAsync();
	
	/**
	 * Sets or removes <code>async</code> attribute.
	 * 
	 * @param seamless If is passed true, then sets <code>async</code> attribute, otherwise removes this attribute.
	 */
	public void setAsync(boolean async);
	
	/**
	 * Returns true if there is any <code>defer</code> attribute.
	 * 
	 * @return True if there is any <code>defer</code> attribute, otherwise false.
	 */
	public boolean getDefer();
	
	/**
	 * Sets or removes <code>defer</code> attribute.
	 * 
	 * @param seamless If is passed true, then sets <code>defer</code> attribute, otherwise removes this attribute.
	 */
	public void setDefer(boolean defer);
	
	/**
	 * Returns <code>type</code> attribute if there is any.
	 * 
	 * @return <code>type</code> attribute if there is any, otherwise false.
	 */
	public String getType();
	
	/**
	 * Sets or removes <code>type</code> attribute.
	 * 
	 * @param src New value of the <code>type</code> attribute.
	 */
	public void setType(String type);
	
	/**
	 * Returns <code>charset</code> attribute if there is any.
	 * 
	 * @return <code>charset</code> attribute if there is any, otherwise false.
	 */
	public String getCharset();
	
	/**
	 * Sets or removes <code>charset</code> attribute.
	 * 
	 * @param src New value of the <code>charset</code> attribute.
	 */
	public void setCharset(String charset);
	
	/**
	 * Returns <code>text</code> attribute if there is any.
	 * 
	 * @return <code>text</code> attribute if there is any, otherwise false.
	 */
	public String getText();
	
	/**
	 * Sets or removes <code>text</code> attribute.
	 * 
	 * @param src New value of the <code>text</code> attribute.
	 */
	public void setText(String text);	  
}

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
 * 
 * @see http://dev.w3.org/html5/markup/script.html#script-interface
 * @author Radim Loskot
 *
 */
public interface Html5ScriptElement extends Html5Element {
	public String getSrc();
	public void  setSrc(String src);
	
	public boolean getAsync();
	public void setAsync(boolean async);
	
	public boolean getDefer();
	public void setDefer(boolean defer);
	
	public String getType();
	public void setType(String type);
	
	public String getCharset();
	public void setCharset(String charset);
	
	public String getText();
	public void setText(String text);	  
}

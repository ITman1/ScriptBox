/**
 * WindowBrowsingContext.java
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

package org.fit.cssbox.scriptbox.browser;

import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.security.SandboxingFlag;

/**
 * Class representing top-level window browsing context.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class WindowBrowsingContext extends IFrameContainerBrowsingContext {		
	
	/**
	 * @see #getPopupSandboxingFlagSet()
	 */
	protected Set<SandboxingFlag> popupSandboxingFlagSet;
	
	/**
	 * Constructs window browsing context.
	 * 
	 * @param browsingUnit Browsing unit to which belongs this browsing context.
	 * @param name New name of this browsing context.
	 */
	public WindowBrowsingContext(BrowsingUnit browsingUnit, String name) {
		super(null, browsingUnit, name, null);
		
		this.popupSandboxingFlagSet = new HashSet<SandboxingFlag>();
	}
	
	/**
	 * Constructs window browsing context with default browsing context name.
	 * 
	 * @param browsingUnit Browsing unit to which belongs this browsing context.
	 */
	public WindowBrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, DEFAULT_NAME);
	}
	
	/**
	 * Returns popup sandboxing flag set.
	 * 
	 * @return Popup sandboxing flag set
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#popup-sandboxing-flag-set">Popup sandboxing flag set</a>
	 */
	public Set<SandboxingFlag> getPopupSandboxingFlagSet() {
		return popupSandboxingFlagSet;
	}

}

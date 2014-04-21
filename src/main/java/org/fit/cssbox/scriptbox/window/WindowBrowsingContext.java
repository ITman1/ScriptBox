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

package org.fit.cssbox.scriptbox.window;

import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.IFrameContainerBrowsingContext;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;

public class WindowBrowsingContext extends IFrameContainerBrowsingContext {		
	// Every top-level browsing context has a popup sandboxing flag set, 
	protected Set<SandboxingFlag> popupSandboxingFlagSet;
	
	public WindowBrowsingContext(BrowsingUnit browsingUnit, String name) {
		super(null, browsingUnit, name, null);
		
		this.popupSandboxingFlagSet = new HashSet<SandboxingFlag>();
	}
	
	public WindowBrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, DEFAULT_NAME);
	}
	
	public Set<SandboxingFlag> getPopupSandboxingFlagSet() {
		return popupSandboxingFlagSet;
	}

}

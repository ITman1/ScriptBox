/**
 * IFrameBrowsingContext.java
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

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;

public class IFrameBrowsingContext extends IFrameContainerBrowsingContext {
	// Every nested browsing context has an iframe sandboxing flag set
	protected Set<SandboxingFlag> iframeSandboxingFlagSet;
	protected boolean seamlessBrowsingFlag;
	protected boolean delayingLoadEventsMode;
	
	public IFrameBrowsingContext(BrowsingContext parentContext, Html5IFrameElementImpl iframeElement) {
		super(parentContext, null, DEFAULT_NAME, iframeElement);

		this.contextName = iframeElement.getName();
		this.iframeSandboxingFlagSet = new HashSet<SandboxingFlag>();
		this.seamlessBrowsingFlag = iframeElement.getSeamless();

		if (iframeElement.getSeamless()) {
			iframeSandboxingFlagSet.add(SandboxingFlag.SEAMLESS_IFRAMES_FLAG);
		}
	}
	
	public Set<SandboxingFlag> getIframeSandboxingFlagSet() {
		return iframeSandboxingFlagSet;
	}
	
	public boolean getSeamlessFlag() {
		return seamlessBrowsingFlag;
	}
	
	public void delayLoadEvents() {
		delayingLoadEventsMode = true;
	}
}

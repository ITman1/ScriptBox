package org.fit.cssbox.scriptbox.browser;

import java.util.HashSet;
import java.util.Set;

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

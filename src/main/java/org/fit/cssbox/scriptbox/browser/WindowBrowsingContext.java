package org.fit.cssbox.scriptbox.browser;

import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;

public class WindowBrowsingContext extends BrowsingContext {	
	// Every top-level browsing context has a popup sandboxing flag set, 
	protected Set<SandboxingFlag> popupSandboxingFlagSet;
	
	public WindowBrowsingContext(BrowsingUnit browsingUnit, String name) {
		super(null, browsingUnit, null, name);
		
		this.popupSandboxingFlagSet = new HashSet<SandboxingFlag>();
	}
	
	public WindowBrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, null);
	}

	public BrowsingContext createIFrameContext(Html5IFrameElementImpl iframeElement) {
		BrowsingContext childContext = new IFrameBrowsingContext(this, iframeElement);
		childContexts.add(childContext);
		return childContext;
	}
	
	public Set<SandboxingFlag> getPopupSandboxingFlagSet() {
		return popupSandboxingFlagSet;
	}

}

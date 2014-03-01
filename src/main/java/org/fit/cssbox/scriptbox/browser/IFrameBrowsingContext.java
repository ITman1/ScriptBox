package org.fit.cssbox.scriptbox.browser;

import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;

public class IFrameBrowsingContext extends BrowsingContext {
	
	// Every nested browsing context has an iframe sandboxing flag set
	protected Set<SandboxingFlag> iframeSandboxingFlagSet;
	protected boolean seamlessBrowsingFlag;
	protected boolean delayingLoadEventsMode;
	
	public IFrameBrowsingContext(BrowsingContext parentContext, Html5IFrameElementImpl iframeElement) {
		super(parentContext, null, null, iframeElement);
				
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
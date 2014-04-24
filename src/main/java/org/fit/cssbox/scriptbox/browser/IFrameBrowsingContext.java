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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;

/*
 * FIXME: This class should be fully implemented according to: 
 *        http://www.w3.org/html/wg/drafts/html/master/embedded-content.html#the-iframe-element
 */
/**
 * Class representing IFRAME browsing context nested via some element container.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class IFrameBrowsingContext extends IFrameContainerBrowsingContext {
	
	/**
	 * @see #getIframeSandboxingFlagSet()
	 */
	protected Set<SandboxingFlag> iframeSandboxingFlagSet;
	
	/**
	 * @see #getSeamlessFlag()
	 */
	protected boolean seamlessBrowsingFlag;
	
	/**
	 * @see #hasDelayingLoadEventsMode()
	 */
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
	
	/**
	 * Returns IFRAME sandboxing flag set.
	 * 
	 * @return IFRAME sandboxing flag set
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#iframe-sandboxing-flag-set">IFRAME sandboxing flag set</a>
	 */
	public Set<SandboxingFlag> getIframeSandboxingFlagSet() {
		return iframeSandboxingFlagSet;
	}
	
	/**
	 * Tests whether is this browsing context in seamless mode.
	 * 
	 * @return True if is this browsing context in seamless mode, otherwise false.
	 */
	public boolean getSeamlessFlag() {
		return seamlessBrowsingFlag;
	}
	
	@Override
	public synchronized boolean hasDelayingLoadEventsMode() {
		return super.hasDelayingLoadEventsMode() || delayingLoadEventsMode;
	}
	
	/**
	 * Removes delaying load event mode.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#delaying-load-events-mode">Delaying load events mode</a>
	 */
	public synchronized void resetDelayingLoadEventsMode() {
		delayingLoadEventsMode = false;
		
		Collection<BrowsingContext> contexts = getAncestorContexts();
		
		for (BrowsingContext context : contexts) {
			synchronized (context) {
				context.notifyAll();
			}
		}
		
		notifyAll();
	}
	
	/**
	 * Enables delaying load event mode.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#delaying-load-events-mode">Delaying load events mode</a>
	 */
	public synchronized void delayLoadEvents() {
		delayingLoadEventsMode = true;
	}
}

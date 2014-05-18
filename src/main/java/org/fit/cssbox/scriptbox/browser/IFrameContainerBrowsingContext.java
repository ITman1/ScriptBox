/**
 * IFrameContainerBrowsingContext.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fit.cssbox.scriptbox.dom.Html5IFrameElementImpl;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.ui.BarProp;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Class representing browsing contexts which may include IFRAME browsing contexts.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class IFrameContainerBrowsingContext extends BrowsingContext {
	
	/**
	 * Default class for bar properties which are not presented by this browsing unit. 
	 * 
	 * @author Radim Loskot
	 */
	public static class NoBarProp extends BarProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
	}
	
	/**
	 * Default class for scrollbar properties which are not presented by this browsing unit. 
	 * 
	 * @author Radim Loskot
	 */
	public static class NoScrollBarsProp extends ScrollBarsProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return false;
		}
		
		@Override
		public void scroll(int xCoord, int yCoord) {
		}

		@Override
		public boolean scrollToFragment(String fragment) {
			return true;
		}

		@Override
		public int getScrollPositionX() {
			return -1;
		}

		@Override
		public int getScrollPositionY() {
			return -1;
		}
	}
	
	protected BarProp menuBar;
	protected BarProp toolBar;
	protected BarProp statusBar;
	protected BarProp personalBar;
	protected BarProp locationBar;
	protected ScrollBarsProp scrollbars;
	
	private static BarProp noBarAvailable = new NoBarProp();
	private static NoScrollBarsProp noScrollBarsAvailable = new NoScrollBarsProp();
	
	/**
	 * Empty list of IFRAME browsing contexts.
	 */
	public final static List<IFrameBrowsingContext> EMPTY_IFRAMES = Collections.unmodifiableList(new ArrayList<IFrameBrowsingContext>());
	
	/**
	 * Map of the document to corresponding document IFRAME browsing contexts.
	 */
	protected Map<Document, List<IFrameBrowsingContext>> documentIframes;
	
	/**
	 * Constructs new top-level or IFRAME container browsing context.
	 * 
	 * @param parentContext {@inheritDoc}
	 * @param browsingUnit {@inheritDoc}
	 * @param contextName {@inheritDoc}
	 * @param container {@inheritDoc}
	 */
	protected IFrameContainerBrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, String contextName, Element container) {
		super(parentContext, browsingUnit, contextName, container);
	
		this.documentIframes = new HashMap<Document, List<IFrameBrowsingContext>>();
		
		this.menuBar = noBarAvailable;
		this.toolBar = noBarAvailable;
		this.statusBar = noBarAvailable;
		this.personalBar = noBarAvailable;
		this.locationBar = noBarAvailable;
		this.scrollbars = noScrollBarsAvailable;
	}
	
	/**
	 * Creates new nested IFRAME browsing context.
	 * 
	 * @param iframeElement IFRAME element which represents the container of the new browsing context.
	 * @return New nested IFRAME browsing context.
	 */
	public IFrameBrowsingContext createIFrameContext(Html5IFrameElementImpl iframeElement) {
		Document document = iframeElement.getOwnerDocument();
		IFrameBrowsingContext childContext = getBrowsingUnit().constructIFrameBrowsingContext(this, iframeElement);
		
		List<IFrameBrowsingContext> iframes = documentIframes.get(document);
		if (iframes == null) {
			iframes = new ArrayList<IFrameBrowsingContext>();
		}
		iframes.add(childContext);

		addChildContext(childContext);
		documentIframes.put(document, iframes);

		return childContext;
	}

	/**
	 * Returns list with all nested IFRAME browsing contexts for a given document.
	 * 
	 * @param document Document for which should be returned the IFRAME browsing contexts.
	 * @return List with all nested IFRAME browsing contexts for a given document
	 */
	public List<IFrameBrowsingContext> getDocumentIframes(Document document) {
		List<IFrameBrowsingContext> iframes = documentIframes.get(document);
		iframes = (iframes == null)? EMPTY_IFRAMES : iframes;
		return Collections.unmodifiableList(iframes);
	}
	
	/**
	 * Tests whether is this or nested browsing context in delaying load event mode.
	 * 
	 * @return True if is this or nested browsing context in delaying load event mode, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#delaying-load-events-mode">Delaying load events mode</a>
	 */
	public synchronized boolean hasDelayingLoadEventsMode() {		
		Document document = getActiveDocument();
		
		if (document == null) {
			return false;
		}
		
		List<IFrameBrowsingContext> iframes = documentIframes.get(document);
		
		boolean result = false;
		
		if (iframes != null && !iframes.isEmpty()) {
			for (IFrameBrowsingContext iframe : iframes) {
				if (iframe.hasDelayingLoadEventsMode()) {
					result = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Returns menu bar properties.
	 * 
	 * @param context Asked browsing context.
	 * @return Menu bar properties
	 */
	public BarProp getMenubar() {
		return menuBar;
	}

	/**
	 * Returns personal bar properties.
	 * 
	 * @param context Asked browsing context.
	 * @return Personal bar properties
	 */
	public BarProp getPersonalbar() {
		return personalBar;
	}

	/**
	 * Returns scroll bar properties.
	 * 
	 * @param context Asked browsing context.
	 * @return Scroll bar properties
	 */
	public ScrollBarsProp getScrollbars() {
		return scrollbars;
	}

	/**
	 * Returns status bar properties.
	 * 
	 * @param context Asked browsing context.
	 * @return Status bar properties
	 */
	public BarProp getStatusbar() {
		return statusBar;
	}

	/**
	 * Returns tool bar properties.
	 * 
	 * @param context Asked browsing context.
	 * @return Tool bar properties
	 */
	public BarProp getToolbar() {
		return toolBar;
	}
	
	/**
	 * Returns location bar properties.
	 * 
	 * @return Location bar properties
	 */
	public BarProp getLocationbar() {
		return locationBar;
	}
	
	/**
	 * Sets menu bar properties.
	 * 
	 * @param menuBar Menu bar.
	 */
	public void setMenubar(BarProp menuBar) {
		this.menuBar = menuBar;
	}

	/**
	 * Sets personal bar properties.
	 * 
	 * @param personalBar Personal bar.
	 */
	public void setPersonalbar(BarProp personalBar) {
		this.personalBar = personalBar;
	}

	/**
	 * Sets scroll bar properties.
	 * 
	 * @param scrollBar Scroll bar.
	 */
	public void setScrollbar(ScrollBarsProp scrollbars) {
		this.scrollbars = scrollbars;
	}

	/**
	 * Sets status bar properties.
	 * 
	 * @param statusBar Status bar.
	 */
	public void setStatusbar(BarProp statusBar) {
		this.statusBar = statusBar;
	}

	/**
	 * Sets tool bar properties.
	 * 
	 * @param toolBar Tool barLocation bar.
	 */
	public void setToolbar(BarProp toolBar) {
		this.toolBar = toolBar;
	}
	
	/**
	 * Sets location bar properties.
	 * 
	 * @param locationBar Location bar.
	 */
	public void setLocationbar(BarProp locationBar) {
		this.locationBar = locationBar;
	}
	
	/**
	 * Tries to scroll to fragment.
	 * 
	 * @param fragment Fragment where to scroll the document view.
	 * @return True if scroll was successful, otherwise false.
	 */
	public boolean scrollToFragment(String fragment) {
		ScrollBarsProp scrollbars = getScrollbars();
		
		return scrollbars.scrollToFragment(fragment);
	}
	
	/**
	 * Scroll to given coordinates.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public void scroll(int x, int y) {
		ScrollBarsProp scrollbars = getScrollbars();
		
		scrollbars.scroll(x, y);
	}
	
	@Override
	protected void removeChildContext(BrowsingContext child) {
		if (child instanceof IFrameBrowsingContext) {
			IFrameBrowsingContext iframeContext = (IFrameBrowsingContext)child;
			Element element = iframeContext.getContainer();
			Document document = element.getOwnerDocument();
			List<IFrameBrowsingContext> iframes = documentIframes.get(document);
			
			if (iframes != null) {
				iframes.remove(child);
			}
			
			if (iframes.isEmpty()) {
				documentIframes.remove(document);
			}
		}
	}
}

/**
 * Html5IFrameElementImpl.java
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

package org.fit.cssbox.scriptbox.dom;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.html.dom.HTMLIFrameElementImpl;
import org.fit.cssbox.scriptbox.dom.interfaces.Html5IFrameElement;

/*
 * TODO: Implement.
 * FIXME: This should extend from the HTML5 element implementation and not HTML4.
 */
/**
 * Extends DOM4 HTMLIFrameElement about features of the HTML5.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * 
 * @see <a href="http://www.whatwg.org/specs/web-apps/current-work/#htmliframeelement">HTML IFrame Element</a>
 */
public class Html5IFrameElementImpl extends HTMLIFrameElementImpl implements Html5IFrameElement {
	public static final String SEAMLESS_ATTR_NAME = "seamless";
	
	private static final long serialVersionUID = -2859222900868725576L;
	private static final String SRCDOC_ATTR_NAME = "srcdoc";

	/**
	 * Constructs new iframe element.
	 * 
	 * @param owner Document which owns this element.
	 * @param name Name of the element.
	 */
	public Html5IFrameElementImpl(HTMLDocumentImpl owner, String name) {
		super(owner, name);
	}


	@Override
	public boolean getSeamless() {
		return !getAttribute(SEAMLESS_ATTR_NAME).isEmpty();
	}

	@Override
	public void setSeamless(boolean seamless) {
		if (seamless) {
			setAttribute(SEAMLESS_ATTR_NAME, "");
		} else {
			removeAttribute(SEAMLESS_ATTR_NAME);
		}
	}
	
	public boolean isInSeamlessMode() {
		//TODO: Implement according to http://www.w3.org/html/wg/drafts/html/CR/embedded-content-0.html#in-seamless-mode
		return false;
	}

	public String getSrcdoc() {
		return getAttribute(SRCDOC_ATTR_NAME);
	}

	public void setSrcdoc(String value) {
		setAttribute(SRCDOC_ATTR_NAME, value);
	}
}

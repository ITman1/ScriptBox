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

public class Html5IFrameElementImpl extends HTMLIFrameElementImpl implements Html5IFrameElement {

	private static final long serialVersionUID = -2859222900868725576L;
	private static final String SRCDOC_ATTR_NAME = "srcdoc";

	public Html5IFrameElementImpl(HTMLDocumentImpl owner, String name) {
		super(owner, name);
	}

	@Override
	public boolean getSeamless() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setSeamless(boolean seamless) {
		// TODO Auto-generated method stub
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

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
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class IFrameContainerBrowsingContext extends BrowsingContext {
	public final static List<IFrameBrowsingContext> EMPTY_IFRAMES = Collections.unmodifiableList(new ArrayList<IFrameBrowsingContext>());
	protected Map<Document, List<IFrameBrowsingContext>> documentIframes;
	
	protected IFrameContainerBrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, String contextName, Element container) {
		super(parentContext, browsingUnit, contextName, container);
	
		this.documentIframes = new HashMap<Document, List<IFrameBrowsingContext>>();
	}
	
	public IFrameBrowsingContext createIFrameContext(Html5IFrameElementImpl iframeElement) {
		Document document = iframeElement.getOwnerDocument();
		IFrameBrowsingContext childContext = new IFrameBrowsingContext(this, iframeElement);
		
		List<IFrameBrowsingContext> iframes = documentIframes.get(document);
		if (iframes == null) {
			iframes = new ArrayList<IFrameBrowsingContext>();
		}
		iframes.add(childContext);

		addChildContext(childContext);
		documentIframes.put(document, iframes);

		return childContext;
	}

	public List<IFrameBrowsingContext> getDocumentIframes(Document document) {
		List<IFrameBrowsingContext> iframes = documentIframes.get(document);
		iframes = (iframes == null)? EMPTY_IFRAMES : iframes;
		return Collections.unmodifiableList(iframes);
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
		}
	}
}

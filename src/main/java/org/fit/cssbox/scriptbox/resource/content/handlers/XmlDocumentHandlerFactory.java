/**
 * XmlDocumentHandlerFactory.java
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

package org.fit.cssbox.scriptbox.resource.content.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.navigation.NavigationAttempt;
import org.fit.cssbox.scriptbox.resource.Resource;
import org.fit.cssbox.scriptbox.resource.content.ContentHandler;
import org.fit.cssbox.scriptbox.resource.content.ContentHandlerFactory;
import org.fit.cssbox.scriptbox.resource.content.DOMContentHandler;

/*
 * TODO: Implement.
 */
/**
 * Content handler factory for handlers following the processing model for XML files.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/browsers.html#read-xml">Read XML</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class XmlDocumentHandlerFactory extends ContentHandlerFactory {
	private class XmlDocumentHandler extends DOMContentHandler {

		public XmlDocumentHandler(NavigationAttempt navigationAttempt) {
			super(navigationAttempt);
		}
		
		@Override
		public void process(Resource resource) {
		}

	}
    private static List<String> mimeTypes;
	
    static {       
        mimeTypes = new ArrayList<String>(4);
        mimeTypes.add("application/xml");
        mimeTypes.add("text/xml");
        mimeTypes.add("image/svg+xml");
        mimeTypes.add("application/xhtml+xml");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
    
	@Override
	public ContentHandler getContentHandler(NavigationAttempt navigationAttempt) {
		return new XmlDocumentHandler(navigationAttempt);
	}
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}
	
	@Override
	public boolean isImplicitlySupported(String mimeType) {
		mimeType = mimeType.trim();
		
		return mimeType.endsWith("+xml");
	}

}

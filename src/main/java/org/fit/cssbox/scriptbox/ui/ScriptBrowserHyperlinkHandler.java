/**
 * ScriptBrowserHyperlinkHandler.java
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

package org.fit.cssbox.scriptbox.ui;

import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;

import org.fit.cssbox.layout.Box;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.swingbox.util.Constants;
import org.fit.cssbox.swingbox.util.DefaultHyperlinkHandler;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * Handler for handling hyperlinks according to the specification. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/links.html#following-hyperlinks">Follow a hyperlink </a>
 */
public class ScriptBrowserHyperlinkHandler extends DefaultHyperlinkHandler
{    
    @Override
    protected void loadPage(JEditorPane pane, HyperlinkEvent evt)
    {    	
    	Element sourceElement = evt.getSourceElement();
    	
    	if (sourceElement == null) {
    		return;
    	}
    	
    	AttributeSet attrSet = sourceElement.getAttributes();    	
		Object boxObject = attrSet.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);

		if (!(boxObject instanceof Box)) {
			return;
		}
		
		Box box = (Box) boxObject;
		
		Node node = box.getNode();
		if (node instanceof Text) {
			node = node.getParentNode();
		}
		
		if (!(node instanceof org.w3c.dom.Element)) {
			return;
		}
		
		org.w3c.dom.Element subject = (org.w3c.dom.Element)node;
		NavigationController.followHyperlink(subject);
    }

}

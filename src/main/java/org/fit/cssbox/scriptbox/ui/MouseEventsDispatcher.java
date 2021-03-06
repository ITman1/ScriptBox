/**
 * MouseEventsDispatcher.java
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

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.plaf.TextUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.GlobalEventHandlers;
import org.fit.cssbox.scriptbox.window.Window;
import org.fit.cssbox.swingbox.SwingBoxDocument;
import org.fit.cssbox.swingbox.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

/**
 * Mouse adapter that captures mouse events above the document 
 * and dispatches corresponding events to the DOM. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class MouseEventsDispatcher extends MouseAdapter {
	
	protected NodeImpl previousTarget;
	
	public MouseEventsDispatcher() {
		reset();
	}
	
	private void reset() {
		previousTarget = null;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);		
		
		if (currentTarget != null) {
			dispatchMouseButtonEvent(e, GlobalEventHandlers.onclick);
			
			if (e.getClickCount() == 2) {
				dispatchMouseButtonEvent(e, GlobalEventHandlers.ondblclick);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);		
		
		if (currentTarget != null) {
			dispatchMouseButtonEvent(e, GlobalEventHandlers.onmousedown);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);		
		
		if (currentTarget != null) {
			dispatchMouseButtonEvent(e, GlobalEventHandlers.onmouseup);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);		
		
		if (currentTarget != null) {
			mouseEnter(e);
		}

		previousTarget = currentTarget;
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);
		
		if (currentTarget != null) {
			if (previousTarget != currentTarget) {
				mouseEnter(e);
			}
			
			mouseMove(currentTarget, e);
		}
		
		previousTarget = currentTarget;
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);		
		
		if (currentTarget != null) {
			mouseExit(e);
		}
		
		previousTarget = null;
	}
	
	private boolean isDescendantNode(Node descendant, Node parent) {
		while (descendant != null && descendant != parent) {
			descendant = descendant.getParentNode();
		}
		
		return parent == descendant;
	}
	
	private void mouseEnter(MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);
		
		if (previousTarget == null || !isDescendantNode(currentTarget, previousTarget)) {
			dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseenter, false, false);
		}

		dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseover, true, true);
	}

	private void mouseMove(EventTarget target, MouseEvent e) {
		NodeImpl currentTarget = getTargetNode(e);
		
		dispatchMouseMotionEvent(e, GlobalEventHandlers.onmousemove, true, true);
		
		previousTarget = currentTarget;
	}

	private void mouseExit(MouseEvent e) {
		dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseout, true, true);
		dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseleave, false, false);
	}
	
	private void dispatchMouseMotionEvent(MouseEvent e, String argType, boolean canBubble, boolean cancelable) {
		NodeImpl node = getTargetNode(e);
		MouseEventImpl event = new MouseEventImpl();
		Window window = getWindowFromNode(node);

		event.initMouseEvent(argType, canBubble, cancelable, window.getWindow(), 
				0, e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), 
				e.isControlDown(), e.isAltDown(), e.isShiftDown(), e.isMetaDown(), (short)0, previousTarget);
		window.dispatchEvent(event, node);
	}
	
	private void dispatchMouseButtonEvent(MouseEvent e, String argType) {
		NodeImpl node = getTargetNode(e);
		Window window = getWindowFromNode(node);
		MouseEventImpl event = new MouseEventImpl();
		event.initMouseEvent(argType, true, true, window.getWindow(), 
				e.getClickCount(), e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), 
				e.isControlDown(), e.isAltDown(), e.isShiftDown(), e.isMetaDown(), getButton(e), null);
		window.dispatchEvent(event, node);
	}
	
	private NodeImpl getTargetNode(MouseEvent e) {
		JEditorPane editor = (JEditorPane) e.getSource();
		NodeImpl node = getNodeFromCoordinates(editor, e.getX(), e.getY());
		
		if (node instanceof CharacterDataImpl) {
			Node _node = node.getParentNode();
			return (_node instanceof NodeImpl)? (NodeImpl)_node : null;
		} else {
			return node;
		}
		
	}
	
	private NodeImpl getNodeFromCoordinates(JEditorPane editor, int x, int y) {
		Bias[] bias = new Bias[1];
		Point pt = new Point(x, y);
		TextUI ui = editor.getUI();
		int pos = ui.viewToModel(editor, pt, bias);
		
		if (pos >= 0) {
			SwingBoxDocument swingBoxDocument = (SwingBoxDocument) editor.getDocument();
			Element element = swingBoxDocument.getCharacterElement(pos);
			
			if (bias[0] == Position.Bias.Backward) {
				// FIXME: - 1 is bug, use getViewAtPoint, but it is protected, or getParagraphElement, but probably not implemented yet.
				pos = element.getStartOffset() - 1; 
				element = swingBoxDocument.getCharacterElement(pos);
			}/* else if (bias[0] == Position.Bias.Forward) {
				pos = element.getStartOffset();
				element = swingBoxDocument.getCharacterElement(pos);
			}*/
			
			AttributeSet attrSet = element.getAttributes();
			Object boxObject = attrSet.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);

			//System.err.println(pos + " -> " + element + " $ " + element.hashCode());
			
			if (boxObject instanceof Box) {
				Box box = (Box) boxObject;
				Node node = box.getNode();
				return (node instanceof NodeImpl)? (NodeImpl)node : null;
			}
		}
		
		return null;
	}
	
	private Window getWindowFromNode(Node node) {
		Document doc = node.getOwnerDocument();

		if (doc instanceof Html5DocumentImpl) {
			Html5DocumentImpl document = (Html5DocumentImpl) doc;
			return document.getWindow();
		}
		
		return null;
	}

	private short getButton(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			return 0;
		}
		
		if (SwingUtilities.isMiddleMouseButton(event)) {
			return 1;
		}
		
		if (SwingUtilities.isRightMouseButton(event)) {
			return 2;
		}

		return (short)(event.getButton() - 1);
	}
}

package org.fit.cssbox.scriptbox.ui;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Position.Bias;

import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.events.MouseEventImpl;
import org.fit.cssbox.layout.Box;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.events.GlobalEventHandlers;
import org.fit.cssbox.swingbox.SwingBoxDocument;
import org.fit.cssbox.swingbox.util.Constants;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

public class MouseEventsDispatcher extends MouseAdapter {
	public final static String onmouseleave_msg = "onmouseleave";
	public final static String onmousemove_msg = "onmousemove";
	public final static String onmouseout_msg = "onmouseout";
	public final static String onmouseover_msg = "onmouseover";
	public final static String onmousewheel_msg = "onmousewheel";
	
	@Override
	public void mouseClicked(MouseEvent e) {
		dispatchMouseButtonEvent(e, GlobalEventHandlers.onclick_msg);
		
		if (e.getClickCount() == 2) {
			dispatchMouseButtonEvent(e, GlobalEventHandlers.ondblclick_msg);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		dispatchMouseButtonEvent(e, GlobalEventHandlers.onmousedown_msg);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		dispatchMouseButtonEvent(e, GlobalEventHandlers.onmouseup_msg);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		return;
		//dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseenter_msg, false, false);
	}
	
	@Override
	public void mouseMoved(MouseEvent e) {
		return;
		/*dispatchMouseMotionEvent(e, GlobalEventHandlers.onmousemove_msg);*/
	}
	
	@Override
	public void mouseExited(MouseEvent e) {
		return;
		/*dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseleave_msg, false, false);
		dispatchMouseMotionEvent(e, GlobalEventHandlers.onmouseout_msg, true, true);*/
	}
	
	protected void dispatchMouseMotionEvent(MouseEvent e, String argType, boolean canBubble, boolean cancelable) {
		NodeImpl node = getTargetNode(e);
		MouseEventImpl event = new MouseEventImpl();
		Window window = getWindowFromNode(node);
		Component component = e.getComponent();
		EventTarget relatedTarget = (component instanceof EventTarget)? (EventTarget)component : null;
		
		event.initMouseEvent(argType, canBubble, cancelable, window.getWindow(), 
				0, e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), 
				e.isControlDown(), e.isAltDown(), e.isShiftDown(), e.isMetaDown(), (short)0, relatedTarget);
		window.dispatchEvent(event, node);
	}
	
	protected void dispatchMouseButtonEvent(MouseEvent e, String argType) {
		NodeImpl node = getTargetNode(e);
		Window window = getWindowFromNode(node);
		MouseEventImpl event = new MouseEventImpl();
		event.initMouseEvent(argType, true, true, window.getWindow(), 
				e.getClickCount(), e.getXOnScreen(), e.getYOnScreen(), e.getX(), e.getY(), 
				e.isControlDown(), e.isAltDown(), e.isShiftDown(), e.isMetaDown(), getButton(e), null);
		window.dispatchEvent(event, node);
	}
	
	protected NodeImpl getTargetNode(MouseEvent e) {
		JEditorPane editor = (JEditorPane) e.getSource();
		NodeImpl node = getNodeFromCoordinates(editor, e.getX(), e.getY());
		
		if (node instanceof CharacterDataImpl) {
			Node _node = node.getParentNode();
			return (_node instanceof NodeImpl)? (NodeImpl)_node : null;
		} else {
			return node;
		}
		
	}
	
	protected NodeImpl getNodeFromCoordinates(JEditorPane editor, int x, int y) {
		Bias[] bias = new Bias[1];
		Point pt = new Point(x, y);
		int pos = editor.getUI().viewToModel(editor, pt, bias);
		SwingBoxDocument swingBoxDocument = (SwingBoxDocument) editor
				.getDocument();
		Element element = swingBoxDocument.getCharacterElement(pos);
		AttributeSet attrSet = element.getAttributes();
		Object boxObject = attrSet.getAttribute(Constants.ATTRIBUTE_BOX_REFERENCE);

		if (boxObject instanceof Box) {
			Box box = (Box) boxObject;
			Node node = box.getNode();
			return (node instanceof NodeImpl)? (NodeImpl)node : null;
		}
		
		return null;
	}
	
	protected Window getWindowFromNode(Node node) {
		Document doc = node.getOwnerDocument();

		if (doc instanceof Html5DocumentImpl) {
			Html5DocumentImpl document = (Html5DocumentImpl) doc;
			return document.getWindow();
		}
		
		return null;
	}

	protected short getButton(MouseEvent event) {
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

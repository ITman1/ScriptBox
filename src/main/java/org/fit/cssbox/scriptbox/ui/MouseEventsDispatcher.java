package org.fit.cssbox.scriptbox.ui;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JEditorPane;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.Position.Bias;
import javax.swing.text.StyledDocument;

import org.fit.cssbox.layout.Viewport;
import org.fit.cssbox.swingbox.SwingBoxDocument;

public class MouseEventsDispatcher extends MouseAdapter {
	
	/*protected Viewport viewport;
	
	MouseEventsDispatcher(/*Viewport viewport) {
		this.viewport = viewport;
	}*/
	
	@Override
	public void mouseClicked(MouseEvent e) {
		JEditorPane editor = (JEditorPane) e.getSource();

			Bias[] bias = new Bias[1];
			Point pt = new Point(e.getX(), e.getY());

			int pos = editor.getUI().viewToModel(editor, pt);

			if (bias[0] == Position.Bias.Backward && pos > 0)
				pos--;

			// Point pt = new Point(e.getX(), e.getY());
			// int pos = editor.viewToModel(pt);
			// System.err.println("found position : " + pos);
			if (pos >= 0) {
				Element el = ((SwingBoxDocument) editor.getDocument())
						.getCharacterElement(pos);
				return;
			} else {
				// fire window event}
			}

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		JEditorPane editor = (JEditorPane) e.getSource();

			Bias[] bias = new Bias[1];
			Point pt = new Point(e.getX(), e.getY());
			int pos = editor.getUI().viewToModel(editor, pt, bias);

			if (bias[0] == Position.Bias.Backward && pos > 0)
				pos--;

			if (pos >= 0 && (editor.getDocument() instanceof StyledDocument)) {
				Element elem = ((StyledDocument) editor.getDocument())
						.getCharacterElement(pos);

			} else // nothing found
			{
				// fire window event
			}
		
	}

}

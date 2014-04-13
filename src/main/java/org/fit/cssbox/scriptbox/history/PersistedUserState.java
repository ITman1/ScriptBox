package org.fit.cssbox.scriptbox.history;

import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;

public class PersistedUserState {
	private Html5DocumentImpl _document;
	private int _scrollPositionX;
	private int _scrollPositionY;
	
	public PersistedUserState(Html5DocumentImpl document) {
		_document = document;
		
		updateState();
	}
	
	public int getScrollPositionX() {
		return _scrollPositionX;
	}
	
	public void setScrollPositionX(int scrollPositionX) {
		_scrollPositionX = scrollPositionX;
	}
	
	public int getScrollPositionY() {
		return _scrollPositionY;
	}
	
	public void setScrollPositionY(int scrollPositionY) {
		_scrollPositionY = scrollPositionY;
	}
	
	public void updateState() {
		Window currentWindow = _document.getWindow();
		ScrollBarsProp scrollbars = currentWindow.getScrollbars();

		_scrollPositionX = scrollbars.getScrollPositionX();
		_scrollPositionY = scrollbars.getScrollPositionY();
	}
	
	public void applyState() {
		Window currentWindow = _document.getWindow();
		ScrollBarsProp scrollbars = currentWindow.getScrollbars();

		scrollbars.scroll(_scrollPositionX, _scrollPositionY);
	}
	
	public static boolean shouldPersist(Html5DocumentImpl document) {
		if (document != null) {
			Window currentWindow = document.getWindow();
			
			if (currentWindow == null) {
				return false;
			}
			ScrollBarsProp scrollbars = currentWindow.getScrollbars();
			
			if (scrollbars == null) {
				return false;
			}
			
			int scrollPositionX = scrollbars.getScrollPositionX();
			int scrollPositionY = scrollbars.getScrollPositionY();
			
			return scrollPositionX != -1 && scrollPositionY != -1;
		}
		
		return false;
	}
}

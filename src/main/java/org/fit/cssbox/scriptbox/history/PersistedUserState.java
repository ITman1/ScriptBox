package org.fit.cssbox.scriptbox.history;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.fit.cssbox.scriptbox.window.Window;

/**
 * Represents class for storing user persisted states.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class PersistedUserState {
	private Html5DocumentImpl _document;
	private int _scrollPositionX;
	private int _scrollPositionY;
	
	/**
	 * Constructs new user persisted state for given document.
	 * 
	 * @param document Document for which will be state stored.
	 */
	public PersistedUserState(Html5DocumentImpl document) {
		_document = document;
		
		updateState();
	}
	
	/**
	 * Returns associated scroll position on X coordinate.
	 * 
	 * @return Associated scroll position on X coordinate
	 */
	public int getScrollPositionX() {
		return _scrollPositionX;
	}
	
	/**
	 * Sets associated scroll position on X coordinate.
	 * 
	 * @param scrollPositionX New associated scroll position on X coordinate
	 */
	public void setScrollPositionX(int scrollPositionX) {
		_scrollPositionX = scrollPositionX;
	}
	
	/**
	 * Returns associated scroll position on Y coordinate.
	 * 
	 * @return Associated scroll position on Y coordinate
	 */
	public int getScrollPositionY() {
		return _scrollPositionY;
	}
	
	/**
	 * Sets associated scroll position on Y coordinate.
	 * 
	 * @param scrollPositionX New associated scroll position on Y coordinate
	 */
	public void setScrollPositionY(int scrollPositionY) {
		_scrollPositionY = scrollPositionY;
	}
	
	/**
	 * Updates settings in user persisted state.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public void updateState() {
		Window currentWindow = _document.getWindow();
		
		if (currentWindow == null) {
			return;
		}
		
		ScrollBarsProp scrollbars = currentWindow.getScrollbars();

		_scrollPositionX = scrollbars.getScrollPositionX();
		_scrollPositionY = scrollbars.getScrollPositionY();
	}
	
	/**
	 * Applies settings from the user persisted state.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#an-entry-with-persisted-user-state">An entry with persisted user state</a>
	 */
	public void applyState() {
		Window currentWindow = _document.getWindow();
		ScrollBarsProp scrollbars = currentWindow.getScrollbars();

		scrollbars.scroll(_scrollPositionX, _scrollPositionY);
	}
	
	/**
	 * Tests whether there should be constructed user persisted state for given document.
	 *  
	 * @param document Document that should be tested.
	 * @return True if it is appropriate to store user persisted state for this document.
	 */
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

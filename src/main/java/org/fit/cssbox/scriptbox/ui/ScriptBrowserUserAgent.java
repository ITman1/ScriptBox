package org.fit.cssbox.scriptbox.ui;

import java.awt.Container;
import java.awt.Rectangle;

import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

public class ScriptBrowserUserAgent extends UserAgent {
	
	public static class LocationBarProp extends BarProp {
		@ScriptGetter
		@Override
		public boolean getVisible() {
			return true;
		}
	}
	
	public static class UiScrollBarsProp extends ScrollBarsProp {
		private JScrollPane scrollPane;
		private ScriptBrowser browser;
		private boolean scrollSuccess;
		
		UiScrollBarsProp(ScriptBrowser browser) {
			this.browser = browser;
			
			Container container = browser.getParent();
			if (container instanceof JScrollPane) {
				scrollPane = (JScrollPane) container;
			}
		}
		
		@ScriptGetter
		@Override
		public boolean getVisible() {
			if (scrollPane != null) {
				JScrollBar vsb = scrollPane.getVerticalScrollBar();
				JScrollBar hsb = scrollPane.getHorizontalScrollBar();
				return (vsb != null && vsb.isVisible()) || (hsb != null && hsb.isVisible());
			}
			
			return false;
		}
		
		@Override
		public void scroll(int xCoord, int yCoord) {
			if (scrollPane != null) {
				JScrollBar vsb = scrollPane.getVerticalScrollBar();
				JScrollBar hsb = scrollPane.getHorizontalScrollBar();
				
				vsb.setValue(yCoord);
				hsb.setValue(xCoord);
			}
		}
		
		@Override
		public synchronized boolean scrollToFragment(final String fragment) {
			scrollSuccess = false;
			if (SwingUtilities.isEventDispatchThread()) {
				scrollToFragmentImpl(fragment);
	        } else {
	    		try {
					SwingUtilities.invokeAndWait(new Runnable() {
						@Override
						public void run() {
							scrollToFragmentImpl(fragment);
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
			
			return scrollSuccess;
		}
		
		public void scrollToFragmentImpl(String fragment) {
			Rectangle top = new Rectangle(0, 0, 1, 1); // top of pane
			Rectangle bottom = new Rectangle(0, browser.getHeight() - 1, 1, 1);
			if (fragment != null) {
				// scroll down and back to reference to get reference
				// the topmost item
				browser.scrollRectToVisible(bottom);
				scrollSuccess = browser.scrollToReferenceWithBoolean(fragment);
			} else {
				// scroll to the top of the new page
				browser.scrollRectToVisible(top);
				scrollSuccess = false;
			}
		}
	}
	
	protected ScriptBrowser browser;
	protected LocationBarProp locationBarProp;
	protected UiScrollBarsProp uiScrollBarsProp;
	
	public ScriptBrowserUserAgent(ScriptBrowser browser) {
		this.browser = browser;
		this.locationBarProp = new LocationBarProp();
		this.uiScrollBarsProp = new UiScrollBarsProp(browser);
	}
	
	@Override
	public BarProp getLocationbar() {
		return locationBarProp;
	}
	
	@Override
	public ScrollBarsProp getScrollbars() {
		return uiScrollBarsProp;
	}
}

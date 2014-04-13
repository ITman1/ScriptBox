/**
 * ScriptBrowserUserAgent.java
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
				scrollSuccess = browser.tryScrollToReference(fragment);
			} else {
				// scroll to the top of the new page
				browser.scrollRectToVisible(top);
				scrollSuccess = false;
			}
		}

		@Override
		public int getScrollPositionX() {
			if (scrollPane != null) {
				JScrollBar hsb = scrollPane.getHorizontalScrollBar();
				return hsb.getValue();
			}
			return 0;
		}

		@Override
		public int getScrollPositionY() {
			if (scrollPane != null) {
				JScrollBar vsb = scrollPane.getVerticalScrollBar();				
				return vsb.getValue();
			}
			return 0;
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

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

import javax.swing.JOptionPane;
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
		private ScriptBrowser browser;
		private boolean scrollSuccess;
		
		UiScrollBarsProp(ScriptBrowser browser) {
			this.browser = browser;
		}
		
		@ScriptGetter
		@Override
		public boolean getVisible() {
			JScrollPane scrollPane = getScrollPane();
			
			if (scrollPane != null) {
				JScrollBar vsb = scrollPane.getVerticalScrollBar();
				JScrollBar hsb = scrollPane.getHorizontalScrollBar();
				return (vsb != null && vsb.isVisible()) || (hsb != null && hsb.isVisible());
			}
			
			return false;
		}
		
		@Override
		public void scroll(int xCoord, int yCoord) {
			final Rectangle rect = new Rectangle(xCoord, yCoord, 0, 0);
			
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					browser.scrollRectToVisible(rect);
				}
			});		
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
		
		protected void scrollToFragmentImpl(String fragment) {
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
			Rectangle rect = browser.getVisibleRect();
			return new Double(rect.getX()).intValue();
		}

		@Override
		public int getScrollPositionY() {
			Rectangle rect = browser.getVisibleRect();
			return new Double(rect.getY()).intValue();
		}
		
		protected JScrollPane getScrollPane() {
			Container container = browser.getParent();
			container = (container != null)? container.getParent() : null;
			
			if (container instanceof JScrollPane) {
				return (JScrollPane) container;
			}
			
			return null;
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
	
	@Override
	public void showAlertDialog(String message) {
		JOptionPane.showMessageDialog(browser, message, "Alert dialog", JOptionPane.INFORMATION_MESSAGE);
	}
	
	@Override
	public boolean showConfirmDialog(String message) {
		int res = JOptionPane.showOptionDialog(browser, message, "Confirm dialog", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		return res == JOptionPane.YES_OPTION;
	}
	
	@Override
	public String showPromptDialog(String message, String defaultChoice) {
		String res = JOptionPane.showInputDialog(browser, message, "Prompt dialog", JOptionPane.QUESTION_MESSAGE);
		return (res == null)? defaultChoice : res;
	}
}

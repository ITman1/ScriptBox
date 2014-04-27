/**
 * JavaScriptTester.java
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

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;

/**
 * Browsing units containing a simple user interface and browsable canvas
 * provided by a ScriptBrowser. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ScriptBrowserBrowsingUnit extends BrowsingUnit {
	protected LocationBarProp locationBarProp;
	protected UiScrollBarsProp uiScrollBarsProp;
	
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
	
	protected ScriptBrowser scriptBrowser;
	
	/**
	 * Constructs browsing unit that was constructed by passed user agent.
	 * 
	 * @param userAgent User agent that constructed this browsing unit.
	 * @param constructScriptBrowser If true then should be explicitly constructed 
	 *        ScriptBrowser inside this constructor. If false, then it should be 
	 *        associated later via {@link #setScriptBrowser(ScriptBrowser)} 
	 */
	protected ScriptBrowserBrowsingUnit(ScriptBrowserUserAgent userAgent, boolean constructScriptBrowser) {
		super(userAgent);
		
		if (constructScriptBrowser) {
			this.scriptBrowser = new ScriptBrowser(this);
			initialize();
		}
	}
	
	/**
	 * Constructs browsing unit that was constructed by passed user agent and creates new {@link #setScriptBrowser(ScriptBrowser)}.
	 * 
	 * @param userAgent User agent that constructed this browsing unit.
	 * @see #ScriptBrowserBrowsingUnit(ScriptBrowserUserAgent, boolean)
	 */
	public ScriptBrowserBrowsingUnit(ScriptBrowserUserAgent userAgent) {
		this(userAgent, true);
	}
	
	/**
	 * Constructs browsing unit that was constructed by user agent of the passed {@link #setScriptBrowser(ScriptBrowser)}.
	 * 
	 * @param scriptBrowser Script browser used for this browsing unit.
	 * @see #ScriptBrowserBrowsingUnit(ScriptBrowserUserAgent, boolean)
	 */
	public ScriptBrowserBrowsingUnit(ScriptBrowser scriptBrowser) {
		super(scriptBrowser.getUserAgent());
		
		setScriptBrowser(scriptBrowser);
	}
	
	/**
	 * Sets new script browser.
	 * 
	 * @param scriptBrowser New script browser to be set.
	 */
	public void setScriptBrowser(ScriptBrowser scriptBrowser) {
		this.scriptBrowser = scriptBrowser;
		
		initialize();
	}
	
	/**
	 * Returns associated script browser.
	 * 
	 * @return Associated script browser.
	 */
	public ScriptBrowser getScriptBrowser() {
		return scriptBrowser;
	}
	
	/**
	 * Initializes this browsing unit.
	 */
	protected void initialize() {
		if (scriptBrowser.getBrowsingUnit() != this) {
			scriptBrowser.setBrowsingUnit(this);
		}
		
		this.locationBarProp = new LocationBarProp();
		this.uiScrollBarsProp = new UiScrollBarsProp(scriptBrowser);
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
		JOptionPane.showMessageDialog(scriptBrowser, message, "Alert dialog", JOptionPane.INFORMATION_MESSAGE);
	}
	
	@Override
	public boolean showConfirmDialog(String message) {
		int res = JOptionPane.showOptionDialog(scriptBrowser, message, "Confirm dialog", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
		return res == JOptionPane.YES_OPTION;
	}
	
	@Override
	public String showPromptDialog(String message, String defaultChoice) {
		String res = JOptionPane.showInputDialog(scriptBrowser, message, "Prompt dialog", JOptionPane.QUESTION_MESSAGE);
		return (res == null)? defaultChoice : res;
	}
	
}

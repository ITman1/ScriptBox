/**
 * BrowserUi.java
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

package org.fit.cssbox.scriptbox.demo.browser;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import org.fit.cssbox.scriptbox.ui.ScriptBrowser;

/**
 * Interface for all UI views.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface BrowserUi {
	public JFrame getWindow();
	public JButton getNavigateButton();
	public JTextField getNavigationField();
	public JButton getHistoryBackButton();
	public JButton getHistoryForwardButton();
	public ScriptBrowser getScriptBrowser();
}

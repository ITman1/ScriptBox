/**
 * JavaScriptTesterController.java
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

package org.fit.cssbox.scriptbox.demo.tester;

import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.demo.browser.BrowserUiController;
import org.fit.cssbox.scriptbox.demo.browser.SimpleBrowsingUnit;

/**
 * Class with the main browsing unit for the JavaScript tester.
 * This class creates the corresponding {@link JavaScriptTesterUi} view
 * and then registers event listeners above view components.
 * It also integrates {@link ConsoleInjector}.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptTesterBrowsingUnit extends SimpleBrowsingUnit {
	public JavaScriptTesterBrowsingUnit(JavaScriptTesterUserAgent userAgent) {
		super(userAgent);
	}
	
	/**
	 * Constructs UI controller for given window context.
	 * 
	 * @param windowContext Window context.
	 * @return Constructed UI controller.
	 */
	protected BrowserUiController constructWindowTopLevelBrowsingContextUiController(WindowBrowsingContext windowContext) {
		return new JavaScriptTesterUiController(windowContext);
	}
}

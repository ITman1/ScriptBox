/**
 * SimpleBrowsingUnit.java
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

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;

import org.fit.cssbox.scriptbox.browser.AuxiliaryBrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingContextEvent;
import org.fit.cssbox.scriptbox.browser.BrowsingContextListener;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserBrowsingUnit;
import org.fit.cssbox.scriptbox.ui.ScriptBrowserUserAgent;

/**
 * Browsing unit with simple user interface which contains navigation
 * field and history traversal buttons.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class SimpleBrowsingUnit extends ScriptBrowserBrowsingUnit {

	private BrowsingContextListener contextListener = new BrowsingContextListener() {

		@Override
		public void onBrowsingContextEvent(BrowsingContextEvent event) {
			BrowsingContext target = event.getTarget();
			
			if (!(target instanceof AuxiliaryBrowsingContext)) {
				return;
			}
			
			AuxiliaryBrowsingContext windowContext = (AuxiliaryBrowsingContext)target;
			
			switch (event.getEventType()) {
				case DESTROYED:
					onAuxiliaryBrowsingContextDestroyed(windowContext);
					break;
				default:
					break;
			}
		}
	};
	
	protected BrowserUIPresenter windowContextUiController;
	protected Map<WindowBrowsingContext, BrowserUIPresenter> auxiliaryUiControllers;
	
	public SimpleBrowsingUnit(ScriptBrowserUserAgent userAgent) {
		super(userAgent, false);
		
		initializeBrowsingUnit();
	}
	
	public SimpleBrowsingUnit(BrowsingContext openerBrowsingContext, String name, boolean createdByScript) {
		super(openerBrowsingContext, name, createdByScript, false);
		
		initializeBrowsingUnit();
	}
	
	@Override
	public void discard() {
		super.discard();
		
		windowContextUiController = null;
		auxiliaryUiControllers.clear();
		contextListener = null;
	}
	
	/**
	 * Initializes this browsing unit.
	 */
	protected void initializeBrowsingUnit() {
		windowBrowsingContext.addListener(contextListener);
		
		auxiliaryUiControllers = new HashMap<WindowBrowsingContext, BrowserUIPresenter>();
		
		windowContextUiController = constructWindowTopLevelBrowsingContextUiController(windowBrowsingContext);
		if (windowContextUiController != null) {
			windowContextUiController.showUI();
		}
		
		BrowserUi browserUI = windowContextUiController.getUI();
		setScriptBrowser(browserUI.getScriptBrowser());
	}
	
	/**
	 * Constructs UI controller for given window context.
	 * 
	 * @param windowContext Window context.
	 * @return Constructed UI controller.
	 */
	protected BrowserUIPresenter constructAuxiliaryBrowsingContextUiController(WindowBrowsingContext windowContext) {
		return new SimpleBrowserUIPresenter(windowContext);
	}
	
	/**
	 * Constructs UI controller for given window context.
	 * 
	 * @param windowContext Window context.
	 * @return Constructed UI controller.
	 */
	protected BrowserUIPresenter constructWindowTopLevelBrowsingContextUiController(WindowBrowsingContext windowContext) {
		return new SimpleBrowserUIPresenter(windowContext);
	}
	
	@SuppressWarnings("unused")
	private void onAuxiliaryBrowsingContextShow(AuxiliaryBrowsingContext windowContext) {
		BrowserUIPresenter controller = auxiliaryUiControllers.get(windowContext);
		
		if (controller == null) {
			controller = constructAuxiliaryBrowsingContextUiController(windowContext);
			auxiliaryUiControllers.put(windowContext, controller);
		}
		
		final BrowserUIPresenter _controller = controller;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				_controller.showUI();
			}
		});
		
	}
	
	private void onAuxiliaryBrowsingContextDestroyed(AuxiliaryBrowsingContext windowContext) {
		BrowserUIPresenter controller = auxiliaryUiControllers.get(windowContext);
		
		if (controller != null) {
			final BrowserUIPresenter _controller = controller;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					_controller.hideUI();
				}
			});
		}
		
		auxiliaryUiControllers.remove(windowContext);
	}

}

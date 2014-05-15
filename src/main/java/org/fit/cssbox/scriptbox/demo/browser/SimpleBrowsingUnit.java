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
	
	protected BrowserUiController windowContextUiController;
	protected Map<WindowBrowsingContext, BrowserUiController> auxiliaryUiControllers;
	
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
		
		auxiliaryUiControllers = new HashMap<WindowBrowsingContext, BrowserUiController>();
		
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
	protected BrowserUiController constructAuxiliaryBrowsingContextUiController(WindowBrowsingContext windowContext) {
		return new SimpleBrowserUiController(windowContext);
	}
	
	/**
	 * Constructs UI controller for given window context.
	 * 
	 * @param windowContext Window context.
	 * @return Constructed UI controller.
	 */
	protected BrowserUiController constructWindowTopLevelBrowsingContextUiController(WindowBrowsingContext windowContext) {
		return new SimpleBrowserUiController(windowContext);
	}
	
	private void onAuxiliaryBrowsingContextShow(AuxiliaryBrowsingContext windowContext) {
		BrowserUiController controller = auxiliaryUiControllers.get(windowContext);
		
		if (controller == null) {
			controller = constructAuxiliaryBrowsingContextUiController(windowContext);
			auxiliaryUiControllers.put(windowContext, controller);
		}
		
		final BrowserUiController _controller = controller;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				_controller.showUI();
			}
		});
		
	}
	
	private void onAuxiliaryBrowsingContextDestroyed(AuxiliaryBrowsingContext windowContext) {
		BrowserUiController controller = auxiliaryUiControllers.get(windowContext);
		
		if (controller != null) {
			final BrowserUiController _controller = controller;
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					_controller.hideUI();
				}
			});
		}
		
		auxiliaryUiControllers.remove(windowContext);
	}

}

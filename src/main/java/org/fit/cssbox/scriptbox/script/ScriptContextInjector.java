/**
 * ScriptContextInjector.java
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

package org.fit.cssbox.scriptbox.script;

import java.util.Set;

/**
 * Abstract class that acts as a script context inject, but instead of that 
 * allows also automatic registration of this inject in the proper 
 * script engine factory via {@link BrowserScriptEngineManager}.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ScriptContextInjector extends ScriptContextInject {
	protected final static String[] ALL_SCRIPT_ENGINE_FACTORIES = null;
	protected final static BrowserScriptEngineManager manager = BrowserScriptEngineManager.getInstance();
	
	protected String[] scriptEngineNames;
	protected boolean isRegistered;
	
	/**
	 * Constructs injector for the supported script engines.
	 * 
	 * @param scriptEngineNames Names of the script engines of which factories
	 *        should have registered the script context inject implemented by this injector. 
	 */
	public ScriptContextInjector(String[] scriptEngineNames) {
		this.scriptEngineNames = scriptEngineNames;
	}
	
	/**
	 * Returns associated names of the script engines.
	 * 
	 * @return Associated names of the script engines
	 */
	public String[] getScriptEngineName() {
		return scriptEngineNames;
	}
	
	/**
	 * Tests whether has been this inject already registered inside corresponding script engine factories.
	 * 
	 * @return True if script context inject is registered.
	 */
	public boolean isRegistered() {
		return isRegistered;
	}
	
	/**
	 * Registers this script context inject inside script engine factories
	 * that constructs script engines with the names that have associated this injector.
	 */
	public void registerScriptContextInject() {
		if (scriptEngineNames != ALL_SCRIPT_ENGINE_FACTORIES) {
			for (String scriptEngineName : scriptEngineNames) {
				Set<BrowserScriptEngineFactory> factories = manager.getMimeContentFactories(scriptEngineName); 
				registerForScriptEngineFactories(factories);
			}
		} else {
			Set<BrowserScriptEngineFactory> factories = manager.getAllMimeContentFactories(); 
			registerForScriptEngineFactories(factories);
		}
		
		isRegistered = true;
	}
	
	/**
	 * Unregisters this script context inject inside script engine factories
	 * that constructs script engines with the names that have associated this injector.
	 */
	public void unregisterScriptContextInject() {
		if (scriptEngineNames != ALL_SCRIPT_ENGINE_FACTORIES) {
			for (String scriptEngineName : scriptEngineNames) {
				Set<BrowserScriptEngineFactory> factories = manager.getMimeContentFactories(scriptEngineName); 
				unregisterForScriptEngineFactories(factories);
			}
		} else {
			Set<BrowserScriptEngineFactory> factories = manager.getAllMimeContentFactories(); 
			unregisterForScriptEngineFactories(factories);
		}

		isRegistered = false;
	}
	
	private void registerForScriptEngineFactories(Set<BrowserScriptEngineFactory> factories) {
		for (BrowserScriptEngineFactory factory : factories) {
			factory.registerScriptContextsInject(this);
		}
	}
	
	private void unregisterForScriptEngineFactories(Set<BrowserScriptEngineFactory> factories) {
		for (BrowserScriptEngineFactory factory : factories) {
			factory.unregisterScriptContextsInject(this);
		}
	}
}

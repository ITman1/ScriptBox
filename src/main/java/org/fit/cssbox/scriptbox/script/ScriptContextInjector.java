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

public abstract class ScriptContextInjector extends ScriptContextInject {
	protected final static String[] ALL_SCRIPT_ENGINE_FACTORIES = null;
	protected final static BrowserScriptEngineManager manager = BrowserScriptEngineManager.getInstance();
	
	protected String[] scriptEngineNames;
	protected boolean isRegistered;
	
	public ScriptContextInjector(String[] scriptEngineNames) {
		this.scriptEngineNames = scriptEngineNames;
	}
	
	public String[] getScriptEngineName() {
		return scriptEngineNames;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	
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
	
	protected void registerForScriptEngineFactories(Set<BrowserScriptEngineFactory> factories) {
		for (BrowserScriptEngineFactory factory : factories) {
			factory.registerScriptContextsInject(this);
		}
	}
	
	protected void unregisterForScriptEngineFactories(Set<BrowserScriptEngineFactory> factories) {
		for (BrowserScriptEngineFactory factory : factories) {
			factory.unregisterScriptContextsInject(this);
		}
	}
}

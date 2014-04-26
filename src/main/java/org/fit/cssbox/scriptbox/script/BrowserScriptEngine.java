/**
 * BrowserScriptEngine.java
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

import javax.script.AbstractScriptEngine;
import javax.script.ScriptEngineFactory;

import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultClassMembersResolverFactory;

/**
 * Abstract class representing JSR 223 compliant base class  
 * for all script engines that supports the browser.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class BrowserScriptEngine extends AbstractScriptEngine {
	protected ScriptSettings<?> scriptSettings;
	protected BrowserScriptEngineFactory factory;
	protected ClassMembersResolverFactory membersFactory;
	
	/**
	 * Constructs script engine for the given settings and that was constructed using passed factory.
	 * 
	 * @param factory Script engine factory that created this browser engine.
	 * @param scriptSettings Script settings that might be used for initialization of this script engine.
	 */
	protected BrowserScriptEngine(BrowserScriptEngineFactory factory, ScriptSettings<?> scriptSettings) {
		this.scriptSettings = scriptSettings;
		this.factory = factory;
		
		this.membersFactory = initializeClassMembersResolverFactory();
	}
	
	/**
	 * Returns script settings.
	 * 
	 * @return Associated script settings.
	 */
	public ScriptSettings<?> getScriptSettings() {
		return scriptSettings;
	}
	
	@Override
    public ScriptEngineFactory getFactory() {
		return factory;
	}

	/**
	 * Returns script engine factory that created this browser script engine.
	 * 
	 * @return Script engine factory that created this browser script engine
	 */
    public BrowserScriptEngineFactory getBrowserFactory() {
		return factory;
	}
    
    /**
     * Returns associated class members resolver factory.
     * 
     * @return Associated class members resolver factory.
     */
	public ClassMembersResolverFactory getClassMembersResolverFactory() {
		return membersFactory;
	}
    
	/**
	 * Initializes class members resolver factory, which will be used for
     * transforming Java object into script adapted objects
     * 
	 * @return New class members resolver factory.
	 */
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		return new DefaultClassMembersResolverFactory();
	}
}

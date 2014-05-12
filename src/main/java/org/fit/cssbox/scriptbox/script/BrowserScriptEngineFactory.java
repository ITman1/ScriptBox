/**
 * BrowserScriptEngineFactory.java
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;

/**
 * Abstract class representing JSR 223 compliant base class  
 * for all script engine factories that supports the browser.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class BrowserScriptEngineFactory extends MimeContentFactoryBase<BrowserScriptEngine> implements ScriptEngineFactory {
	protected static final String UNSUPORTED_OPERATION_MESSAGE = "Operation is not supported for browser script engine!";
	protected static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
	
	/**
	 * Injects which are registered by this factory.
	 */
	protected List<ScriptContextInject> scriptContextsInjects;
	
	public BrowserScriptEngineFactory() {
		scriptContextsInjects = new ArrayList<ScriptContextInject>();
	}
	
	/**
	 * Constructs new script engine with a given script settings and 
	 * injects all script context injects inside it.
	 * 
	 * @param scriptSettings Script settings to be passed into new script engine.
	 * @return New constructed script engine with a given script settings.
	 */
	public BrowserScriptEngine getBrowserScriptEngine(ScriptSettings<?> scriptSettings) {
		BrowserScriptEngine scriptEngine = getBrowserScriptEngineProtected(scriptSettings);
		
		installScriptContextInjects(scriptEngine);
		
		return scriptEngine;
	}
	
	@Override
	public BrowserScriptEngine getContent(Object... args) {
		if (args.length == 1 && args[0] instanceof ScriptSettings<?>) {
			return getBrowserScriptEngine((ScriptSettings<?>)args[0]);
		}

		return null;
	}
	
	@Override
	public List<String> getExtensions() {
		return EMPTY_STRING_LIST;
	}

	@Override
	public List<String> getMimeTypes() {
		return getExplicitlySupportedMimeTypes();
	}

	@Override
	public List<String> getNames() {
		return EMPTY_STRING_LIST;
	}
	
	@Override
	public Object getParameter(String key) {
		if (key.equals(ScriptEngine.NAME)) {
			return getEngineShortName();
		} else if (key.equals(ScriptEngine.ENGINE)) {
			return getEngineName();
		} else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
			return getEngineVersion();
		} else if (key.equals(ScriptEngine.LANGUAGE)) {
			return getLanguageName();
		} else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
			return getLanguageVersion();
		} else {
			throw new IllegalArgumentException("Invalid key");
		}
	}
	
	@Override
	public ScriptEngine getScriptEngine() {
		return null;
	}
	
	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
	
	@Override
	public String getOutputStatement(String toDisplay) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
	
	@Override
	public String getProgram(String... statements) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
	
	/**
	 * Registers passed script context inject.
	 * 
	 * @param inject Inject to be registered.
	 */
	public synchronized void registerScriptContextsInject(ScriptContextInject inject) {
		scriptContextsInjects.add(inject);
		
		Collections.sort(scriptContextsInjects);
	}
	
	/**
	 * Unregisters passed script context inject.
	 * 
	 * @param inject Inject to be unregistered.
	 */
	public synchronized void unregisterScriptContextsInject(ScriptContextInject inject) {
		scriptContextsInjects.remove(inject);
	}
	
	/**
	 * Returns all registered script context injects.
	 * 
	 * @return All registered script context injects.
	 */
	public synchronized Collection<ScriptContextInject> getScriptContextsInjects() {
		return Collections.unmodifiableList(scriptContextsInjects);
	}
	
	/**
	 * Installs script context injects into passed script engine.
	 * 
	 * @param scriptEngine Script engine where should be applied all script context injects.
	 */
	protected synchronized void installScriptContextInjects(BrowserScriptEngine scriptEngine) {
		ScriptContext context = scriptEngine.getContext();
		
		for (ScriptContextInject inject : scriptContextsInjects) {
			if (inject.isValid(context)) {
				inject.inject(context);
			}
		}
	}
	
	/**
	 * Returns short name of this engine.
	 * 
	 * @return Short name of this engine.
	 */
	public abstract String getEngineShortName();
	
	/**
	 * Constructs new script engine with a given script settings.
	 * 
	 * @param scriptSettings Script settings to be passed into new script engine.
	 * @return New constructed script engine with a given script settings.
	 */
	protected abstract BrowserScriptEngine getBrowserScriptEngineProtected(ScriptSettings<?> scriptSettings);
}

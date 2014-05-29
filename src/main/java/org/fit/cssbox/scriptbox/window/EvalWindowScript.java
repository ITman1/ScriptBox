/**
 * EvalWindowScript.java
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

package org.fit.cssbox.scriptbox.window;

import java.io.Reader;
import java.net.URL;

import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.resource.ResourceReader;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;

/**
 * Represents class for creating scripts that have Window as a global object.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#script-settings-for-browsing-contexts">Script settings for browsing contexts</a>
 */
public class EvalWindowScript extends WindowScript<Reader, Object> {
	
	static final boolean COMPILE_SCRIPTS = false;
	
	public EvalWindowScript(Reader source, URL sourceURL, String language, WindowScriptSettings settings, boolean mutedErrorsFlag) {
		super(new ResourceReader(sourceURL, source), sourceURL, language, settings, mutedErrorsFlag);
	}
	
	// Parse/compile/initialize the source of the script using the script execution environment
	@SuppressWarnings("unused")
	@Override
	protected Object obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source) throws ScriptException {
		if (COMPILE_SCRIPTS && executionEnviroment instanceof Compilable) {
			return ((Compilable)executionEnviroment).compile(source);
		} else {
			return source;
		}
	}

	@Override
	protected Object executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, Object codeEntryPoint) throws ScriptException {
		if (codeEntryPoint instanceof CompiledScript) {
			return ((CompiledScript)codeEntryPoint).eval();	
		} else if (codeEntryPoint instanceof Reader) {
			return executionEnviroment.eval((Reader)codeEntryPoint);
		} else {
			throw new ScriptException("Invalid type of code entry point!");
		}
			
	}
}

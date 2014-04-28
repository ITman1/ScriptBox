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

import java.net.URL;

import javax.script.Invocable;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.FunctionInvocation;

/**
 * Represents class for creating scripts that have Window as a global object
 * and that have as a source the invocable function.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/master/webappapis.html#script-settings-for-browsing-contexts">Script settings for browsing contexts</a>
 */
public class InvokeWindowScript extends WindowScript<FunctionInvocation> {
	
	public InvokeWindowScript(FunctionInvocation source, URL sourceURL, String language, WindowScriptSettings settings, boolean mutedErrorsFlag) {
		super(source, sourceURL, language, settings, mutedErrorsFlag);
	}
	
	// Parse/compile/initialize the source of the script using the script execution environment
	@Override
	protected FunctionInvocation obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, FunctionInvocation source) {
		return source;
	}

	@Override
	protected Object executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, FunctionInvocation codeEntryPoint) throws ScriptException {
		if (executionEnviroment instanceof Invocable) {
			Invocable engine = (Invocable)executionEnviroment;
			Object thiz = codeEntryPoint.getThiz();
			String name = codeEntryPoint.getName();
			Object[] args = codeEntryPoint.getArgs();
			try {
				return engine.invokeMethod(thiz, name, args);
			} catch (NoSuchMethodException e) {
				throw new ScriptException(e);
			}
		} else {
			throw new ScriptException("Execution enviroment does not implement Invocable interface");
		}	
	}
}

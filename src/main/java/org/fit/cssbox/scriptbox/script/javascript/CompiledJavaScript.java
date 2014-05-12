/**
 * CompiledJavaScript.java
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

package org.fit.cssbox.scriptbox.script.javascript;

import javax.script.CompiledScript;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**
 * Wraps compiled Rhino script into JSR 223 compiled Script.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class CompiledJavaScript extends CompiledScript {

	private WindowJavaScriptEngine scriptEngine;
	private Script script;
	
	public CompiledJavaScript(WindowJavaScriptEngine scriptEngine, Script script) {
		this.scriptEngine = scriptEngine;
		this.script = script;
	}
	
	@Override
	public Object eval(ScriptContext context) throws ScriptException {
		Object ret = null;
		 
		Context cx = scriptEngine.enterContext();

		try {
			Scriptable executionScope = scriptEngine.getExecutionScope(context);
			Object res = script.exec(cx, executionScope);
			ret = scriptEngine.unwrap(res);
		} catch (Exception e) {
			WindowJavaScriptEngine.throwWrappedScriptException(e);
		} finally {
			Context.exit();
		}

		return ret;
	}

	@Override
	public ScriptEngine getEngine() {
		return scriptEngine;
	}

}

/**
 * WindowScript.java
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

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.Script;

public class WindowScript extends Script<Reader, WindowScriptSettings> {
	
	public WindowScript(Reader source, URL sourceURL, String language, WindowScriptSettings settings, boolean mutedErrorsFlag) {
		super(source, sourceURL, language, settings, mutedErrorsFlag);
	}

	@Override
	protected boolean prepareRunCallback(WindowScriptSettings settings) {
		Window window = settings.getGlobalObject();
		Html5DocumentImpl windowDocument = window.getDocumentImpl();
		if (!windowDocument.isFullyActive()) {
			return false;
		}
		return super.prepareRunCallback(settings);
	}
	
	// Parse/compile/initialize the source of the script using the script execution environment
	@Override
	protected Reader obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source) {
		return source;
	}

	@Override
	protected Object executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader codeEntryPoint) throws ScriptException {
		return executionEnviroment.eval(codeEntryPoint);		
	}
}

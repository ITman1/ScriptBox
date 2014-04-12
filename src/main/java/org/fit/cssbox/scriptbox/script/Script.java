/**
 * Script.java
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

import java.io.Reader;
import java.net.URL;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;

public abstract class Script<CodeEntryPoint, ScriptSettingsTemplate extends ScriptSettings<?>> {
	protected Reader source;
	protected URL sourceURL;
	protected String language;
	protected boolean mutedErrorsFlag;
	protected ScriptSettingsTemplate settings;
	protected Object result;
	protected Exception exception;
	
	public Script(Reader source, URL sourceURL, String language, ScriptSettingsTemplate settings, boolean mutedErrorsFlag) {
		this.source = source;
		this.sourceURL = sourceURL;
		this.language = language;
		this.settings = settings;
		this.mutedErrorsFlag = mutedErrorsFlag;
		
		createScript();
	}
			
	public Reader getSource() {
		return source;
	}
	
	public URL getSourceURL() {
		return sourceURL;
	}
	
	public String getLanguage() {
		return language;
	}
	
	public boolean hasMutedErros() {
		return mutedErrorsFlag;
	}
	
	public ScriptSettingsTemplate getScriptSettings() {
		return settings;
	}
	
	public Object getResult() {
		return result;
	}
	
	public Exception getException() {
		return exception;
	}
	
	protected void jumpToCodeEntryPoint(CodeEntryPoint codeEntryPoint) {		
		if (!prepareRunCallback(settings)) {
			return;
		}
		
		BrowserScriptEngine executionEnviroment = obtainExecutionEnviroment(settings);
		
		if (executionEnviroment != null) {
			try {
				exception = null;
				result = executeCodeEntryPoint(executionEnviroment, codeEntryPoint);
			} catch (ScriptException e) {
				e.printStackTrace();
				exception = e;
				result = null;
			}
		}
		
		cleanupAfterRunningCallback();
	} 
	
	protected boolean prepareRunCallback(ScriptSettingsTemplate context) {	
		if (isScriptingDisabled()) {
			return false;
		}
		
		BrowsingContext browsingContext = context.getResposibleBrowsingContext();
		BrowsingUnit browsingUnit = browsingContext.getBrowsingUnit();
		
		if (browsingUnit == null) {
			return false;
		}
		
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		stack.push(settings, true);
		
		return true;
	} 
	
	protected void cleanupAfterRunningCallback() {
		BrowsingContext browsingContext = settings.getResposibleBrowsingContext();
		
		if (browsingContext == null) {
			return;
		}
		
		BrowsingUnit browsingUnit = browsingContext.getBrowsingUnit();
		
		if (browsingUnit == null) {
			return;
		}
		
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		stack.popIncumbentScriptSettings(); // this will maybe invoke cleanup jobs and microtask checkpoint
	}
	
	protected void createScript() {
		if (isScriptingDisabled()) {
			return;
		}
		
		BrowserScriptEngine executionEnviroment = obtainExecutionEnviroment(settings);
		
		if (executionEnviroment == null) {
			return;
		}
		
		CodeEntryPoint codeEntryPoint = obtainCodeEntryPoint(executionEnviroment, source);
		jumpToCodeEntryPoint(codeEntryPoint);
	}
	
	protected boolean isScriptingDisabled() {
		BrowsingContext browsingContext = settings.getResposibleBrowsingContext();
		
		return !browsingContext.scriptingEnabled();
	}
	
	protected BrowserScriptEngine obtainExecutionEnviroment(ScriptSettingsTemplate settings) {
		return settings.getExecutionEnviroment(this);
	}
	
	protected abstract CodeEntryPoint obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source);
	protected abstract Object executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, CodeEntryPoint codeEntryPoint) throws ScriptException;
}

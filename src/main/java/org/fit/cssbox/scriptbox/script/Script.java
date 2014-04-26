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

/**
 * Represents class for creating and executing the scripts.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#concept-script">Script</a>
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class Script<CodeEntryPoint, ScriptSettingsTemplate extends ScriptSettings<?>> {
	protected Reader source;
	protected URL sourceURL;
	protected String language;
	protected boolean mutedErrorsFlag;
	protected ScriptSettingsTemplate settings;
	protected Object result;
	protected ScriptException exception;

	/**
	 * Creates new script.
	 * 
	 * @param source Source code with the script.
	 * @param sourceURL URL from which was script retrieved
	 * @param language Language of the script engine that should be used for executing this script.
	 * @param settings Script settings to be used for this script.
	 * @param mutedErrorsFlag If true then no script errors are signaled.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#create-a-script">Create a script</a>
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#muted-errors">Muted errors flag</a>
	 */
	public Script(Reader source, URL sourceURL, String language, ScriptSettingsTemplate settings, boolean mutedErrorsFlag) {
		this.source = source;
		this.sourceURL = sourceURL;
		this.language = language;
		this.settings = settings;
		this.mutedErrorsFlag = mutedErrorsFlag;
		
		createScript();
	}
			
	/**
	 * Returns associated script source code.
	 * 
	 * @return Associated script source code
	 */
	public Reader getSource() {
		return source;
	}
	
	/**
	 * Returns origin location of this script.
	 * 
	 * @return URL of the location of this script.
	 */
	public URL getSourceURL() {
		return sourceURL;
	}
	
	/**
	 * Returns language of this script.
	 * 
	 * @return Language of this script.
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Tests whether has this script muted errors.
	 * 
	 * @return True if has this script muted errors, otherwise false.
	 */
	public boolean hasMutedErros() {
		return mutedErrorsFlag;
	}
	
	/**
	 * Returns associated script settings.
	 * 
	 * @return Associated script settings.
	 */
	public ScriptSettingsTemplate getScriptSettings() {
		return settings;
	}
	
	/**
	 * Returns evaluated result of this script.
	 * 
	 * @return Evaluated result of this script.
	 */
	public Object getResult() {
		return result;
	}
	
	/**
	 * Returns exception that occured while script was executed.
	 * 
	 * @return Exception that occured while script was executed, or null if there is not any.
	 */
	public ScriptException getException() {
		return exception;
	}
	
	/**
	 * Runs preparation steps and then executes the code entry point.
	 * 
	 * @param codeEntryPoint Code entry-point to be executed
	 * @see <a href="www.w3.org/html/wg/drafts/html/CR/webappapis.html#jump-to-a-code-entry-point">Jump to a code entry point</a>
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#code-entry-point">A code entry-point</a>
	 */
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
	
	/**
	 * If scripting is enabled and no other error occurs then pushes 
	 * new script execution onto script settings stack.
	 * 
	 * @param context Script settings object
	 * @return Either "run" or "do not run" as boolean values.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#prepare-to-run-a-callback">Prepare to run a callback</a>
	 */
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
		
		stack.push(context, true);
		
		return true;
	} 
	
	/**
	 * Runs finalization steps after run script callback.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#clean-up-after-running-a-callback">Clean up after running a callback</a>
	 */
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
	
	/**
	 * Creates new script.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#create-a-script">Create a script</a>
	 */
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
	
	/**
	 * Tests whether is scripting disabled.
	 * 
	 * @return True if is scripting disabled, otherwise false.
	 */
	protected boolean isScriptingDisabled() {
		BrowsingContext browsingContext = settings.getResposibleBrowsingContext();
		
		return !browsingContext.scriptingEnabled();
	}
	
	/**
	 * Obtains execution environment where to run this script.
	 * 
	 * @param settings Settings used for obtaining the execution environment.
	 * @return Execution environment where to run this script
	 */
	protected BrowserScriptEngine obtainExecutionEnviroment(ScriptSettingsTemplate settings) {
		return settings.getExecutionEnviroment(this);
	}
	
	/**
	 * Retrieves code entry-point which to execute.
	 * 
	 * @param executionEnviroment Execution environment to be used for obtaining the code entry-point.
	 * @param source Source from which to get the code entry-point
	 * @return Corresponding code entry-point.
	 */
	protected abstract CodeEntryPoint obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source);
	
	/**
	 * Executes code entry-point inside execution environment.
	 * @param executionEnviroment Execution environment used for the execution.
	 * @param codeEntryPoint code entry-point
	 * @return Result of the execution
	 * @throws ScriptException Exception thrown by the execution.
	 */
	protected abstract Object executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, CodeEntryPoint codeEntryPoint) throws ScriptException;
}

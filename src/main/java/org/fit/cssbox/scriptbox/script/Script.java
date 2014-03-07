package org.fit.cssbox.scriptbox.script;

import java.io.Reader;
import java.net.URL;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;

public abstract class Script<CodeEntryPoint> {
	protected Reader source;
	protected URL sourceURL;
	protected String language;
	protected boolean mutedErrorsFlag;
	protected ScriptSettings settings;
	
	public Script(Reader source, URL sourceURL, String language, ScriptSettings settings, boolean mutedErrorsFlag) {
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
	
	public ScriptSettings getScriptSettings() {
		return settings;
	}
	
	protected void jumpToCodeEntryPoint(CodeEntryPoint codeEntryPoint) {		
		if (!prepareRunCallback(settings)) {
			return;
		}
		
		BrowserScriptEngine executionEnviroment = obtainExecutionEnviroment(settings);
		
		if (executionEnviroment != null) {
			executeCodeEntryPoint(executionEnviroment, codeEntryPoint);
		}
		
		cleanupAfterRunningCallback();
	} 
	
	protected boolean prepareRunCallback(ScriptSettings context) {	
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
	
	protected BrowserScriptEngine obtainExecutionEnviroment(ScriptSettings settings) {
		return settings.getExecutionEnviroment(this);
	}
	
	protected abstract CodeEntryPoint obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source);
	protected abstract void executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, CodeEntryPoint codeEntryPoint);
}

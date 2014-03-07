package org.fit.cssbox.scriptbox.script;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.BrowsingUnit;

public abstract class Script {
	protected InputStream scriptInputStream;
	protected boolean mutedErrorsFlag;
	protected ScriptSettings scriptSettings;
	
	public void setCodeEntryPoint(String script) {
		scriptInputStream = new ByteArrayInputStream(script.getBytes());
	}
	
	public InputStream getCodeEntryPointInputStream() {
		return scriptInputStream;
	}
	
	public void muteErrors() {
		mutedErrorsFlag = true;
	}
	
	public boolean hasMutedErros() {
		return mutedErrorsFlag;
	}
	
	public ScriptSettings getScriptSettings() {
		return scriptSettings;
	}

	public void setScriptSettings(ScriptSettings scriptSettings) {
		this.scriptSettings = scriptSettings;
	}
	
	public void jumpToCodeEntryPoint() {
		ScriptSettings context = scriptSettings;
		
		if (!prepareRunCallback(context)) {
			return;
		}
		
		ScriptEngine executionEnviroment = context.getExecutionEnviroment(this);
		
		if (executionEnviroment != null) {
			try {
				executionEnviroment.eval(getCodeEntryPoint());
			} catch (ScriptException e) {
				// TODO: Throw exception?
			}
		}
		
		cleanupAfterRunningCallback();
	} 
	
	protected boolean prepareRunCallback(ScriptSettings settings) {
		BrowsingContext browsingContext = settings.getResposibleBrowsingContext();
		
		if (!browsingContext.scriptingEnabled()) {
			return false;
		}
		
		BrowsingUnit browsingUnit = browsingContext.getBrowsingUnit();
		
		if (browsingUnit == null) {
			return false;
		}
		
		ScriptSettingsStack stack = browsingUnit.getScriptSettingsStack();
		
		stack.push(settings, true);
		
		return true;
	} 
	
	protected void cleanupAfterRunningCallback() {
		BrowsingContext browsingContext = scriptSettings.getResposibleBrowsingContext();
		
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
	
	protected abstract Reader getCodeEntryPoint();
}

package org.fit.cssbox.scriptbox.browser;

import java.io.Reader;
import java.net.URL;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.Script;
import org.fit.cssbox.scriptbox.script.ScriptSettings;

public class WindowScript extends Script<Reader> {
	
	public WindowScript(Reader source, URL sourceURL, String language, ScriptSettings settings, boolean mutedErrorsFlag) {
		super(source, sourceURL, language, settings, mutedErrorsFlag);
	}

	@Override
	protected boolean prepareRunCallback(ScriptSettings settings) {
		Object globalObject = settings.getGlobalObject();
		if (globalObject instanceof Window) {
			Window window = (Window)globalObject;
			Html5DocumentImpl windowDocument = window.getDocumentImpl();
			if (!windowDocument.isFullyActive()) {
				return false;
			}
		}
		return super.prepareRunCallback(settings);
	}
	
	// Parse/compile/initialize the source of the script using the script execution environment
	@Override
	protected Reader obtainCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader source) {
		return source;
	}

	@Override
	protected void executeCodeEntryPoint(BrowserScriptEngine executionEnviroment, Reader codeEntryPoint) {
		try {
			executionEnviroment.eval(codeEntryPoint);
		} catch (ScriptException e) {
			// TODO: Throw exception?
		}
		
	}
}

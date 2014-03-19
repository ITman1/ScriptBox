package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptSettings;

public class GlobalObjectJavaScriptEngineFactory extends BrowserScriptEngineFactory {

	private static List<String> mimeTypes;
	
	static {	   
		/* http://tools.ietf.org/html/rfc4329#page-9 */
		mimeTypes = new ArrayList<String>(4);
		mimeTypes.add("application/javascript");
		mimeTypes.add("application/ecmascript");
		mimeTypes.add("text/javascript");
		mimeTypes.add("text/ecmascript");
		mimeTypes = Collections.unmodifiableList(mimeTypes);
	}
	
	private static final String ENGINE_SHORTNAME = "javascript";
	private static final String ENGINE_NAME = "Window Rhino Engine";
	private static final String ENGINE_VERSION = "0.9";
	private static final String LANGUAGE_NAME = "ECMAScript";
	private static final String LANGUAGE_VERSION = "1.8";
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}

	@Override
	public BrowserScriptEngine getBrowserScriptEngine(ScriptSettings<?> scriptSettings) {
		if (scriptSettings instanceof WindowScriptSettings) {
			BrowserScriptEngine engine = new GlobalObjectJavaScriptEngine(this, (WindowScriptSettings)scriptSettings);
			return engine;
		}
		return null;
	}

	@Override
	public String getEngineName() {
		return ENGINE_NAME;
	}

	@Override
	public String getEngineVersion() {
		return ENGINE_VERSION;
	}

	@Override
	public String getLanguageName() {
		return LANGUAGE_NAME;
	}

	@Override
	public String getLanguageVersion() {
		return LANGUAGE_VERSION;
	}

	@Override
	public String getEngineShortName() {
		return ENGINE_SHORTNAME;
	}
}

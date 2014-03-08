package org.fit.cssbox.scriptbox.script;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;

public abstract class BrowserScriptEngineFactory extends MimeContentFactoryBase<BrowserScriptEngine> implements ScriptEngineFactory {
	protected static final String UNSUPORTED_OPERATION_MESSAGE = "Operation is not supported for browser script engine!";
	protected static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
	
	public abstract BrowserScriptEngine getBrowserScriptEngine(ScriptSettings<?> scriptSettings);
	public abstract String getEngineShortName();
	
	@Override
	public BrowserScriptEngine getContent(Object... args) {
		if (args.length == 1 && args[0] instanceof ScriptSettings<?>) {
			return getBrowserScriptEngine((ScriptSettings<?>)args[0]);
			}

			return null;
	}
	
	@Override
	public List<String> getExtensions() {
		return EMPTY_STRING_LIST;
	}

	@Override
	public List<String> getMimeTypes() {
		return getExplicitlySupportedMimeTypes();
	}

	@Override
	public List<String> getNames() {
		return EMPTY_STRING_LIST;
	}
	
	@Override
	public Object getParameter(String key) {
		if (key.equals(ScriptEngine.NAME)) {
			return getEngineShortName();
		} else if (key.equals(ScriptEngine.ENGINE)) {
			return getEngineName();
		} else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
			return getEngineVersion();
		} else if (key.equals(ScriptEngine.LANGUAGE)) {
			return getLanguageName();
		} else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
			return getLanguageVersion();
		} else {
			throw new IllegalArgumentException("Invalid key");
		}
	}
	
	@Override
	public ScriptEngine getScriptEngine() {
		return null;
	}
	
	@Override
	public String getMethodCallSyntax(String obj, String m, String... args) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
	
	@Override
	public String getOutputStatement(String toDisplay) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
	
	@Override
	public String getProgram(String... statements) {
		throw new UnsupportedOperationException(UNSUPORTED_OPERATION_MESSAGE);
	}
}

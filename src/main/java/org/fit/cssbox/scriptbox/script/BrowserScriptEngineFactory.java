package org.fit.cssbox.scriptbox.script;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;

import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;

public abstract class BrowserScriptEngineFactory extends MimeContentFactoryBase<BrowserScriptEngine> implements ScriptEngineFactory {
	protected static final String UNSUPORTED_OPERATION_MESSAGE = "Operation is not supported for browser script engine!";
	protected static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();
	
	protected Set<ScriptContextInject> scriptContextsInjects;
	
	public abstract String getEngineShortName();
	
	public BrowserScriptEngineFactory() {
		scriptContextsInjects = new TreeSet<ScriptContextInject>();
	}
	
	public BrowserScriptEngine getBrowserScriptEngine(ScriptSettings<?> scriptSettings) {
		BrowserScriptEngine scriptEngine = getBrowserScriptEngineProtected(scriptSettings);
		
		installScriptContextInjects(scriptEngine);
		
		return scriptEngine;
	}
	
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
	
	public void registerScriptContextsInject(ScriptContextInject inject) {
		scriptContextsInjects.add(inject);
	}
	
	public void unregisterScriptContextsInject(ScriptContextInject inject) {
		scriptContextsInjects.remove(inject);
	}
	
	public Collection<ScriptContextInject> getScriptContextsInjects() {
		return Collections.unmodifiableSet(scriptContextsInjects);
	}
	
	protected void installScriptContextInjects(BrowserScriptEngine scriptEngine) {
		ScriptContext context = scriptEngine.getContext();
		
		for (ScriptContextInject inject : scriptContextsInjects) {
			if (inject.isValid(context)) {
				inject.inject(context);
			}
		}
	}
	
	protected abstract BrowserScriptEngine getBrowserScriptEngineProtected(ScriptSettings<?> scriptSettings);
}

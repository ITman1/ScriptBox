package org.fit.cssbox.scriptbox.script;

import java.io.Reader;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

public abstract class DocumentScriptEngine implements ScriptEngine {
	protected Html5DocumentImpl document;
	
	protected DocumentScriptEngine(Html5DocumentImpl document) {
		this.document = document;
	}
	
	public Html5DocumentImpl getDocument() {
		return document;
	}
	
	@Override
	public Object eval(String script, ScriptContext context) throws ScriptException {
		return getEngine().eval(script, context);
	}

	@Override
    public Object eval(Reader reader , ScriptContext context) throws ScriptException {
		return getEngine().eval(reader, context);
	}

	@Override
    public Object eval(String script) throws ScriptException {
		return getEngine().eval(script);
	}

	@Override
    public Object eval(Reader reader) throws ScriptException {
		return getEngine().eval(reader);
	}

	@Override
    public Object eval(String script, Bindings n) throws ScriptException {
		return getEngine().eval(script, n);
	}

	@Override
    public Object eval(Reader reader , Bindings n) throws ScriptException {
		return getEngine().eval(reader, n);
	}

	@Override
    public void put(String key, Object value) {
		getEngine().put(key, value);
	}

	@Override
    public Object get(String key) {
		return getEngine().get(key);
	}

	@Override
    public Bindings getBindings(int scope) {
		return getEngine().getBindings(scope);
	}

	@Override
    public void setBindings(Bindings bindings, int scope) {
		getEngine().setBindings(bindings, scope);
	}

	@Override
    public Bindings createBindings() {
		return getEngine().createBindings();
	}

	@Override
    public ScriptContext getContext() {
		return getEngine().getContext();
	}

	@Override
    public void setContext(ScriptContext context) {
		getEngine().setContext(context);
	}

	@Override
    public ScriptEngineFactory getFactory() {
		return getEngine().getFactory();
	}
	
	protected abstract ScriptEngine getEngine();
}

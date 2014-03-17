package org.fit.cssbox.scriptbox.script.javascript.annotation;

import org.fit.cssbox.scriptbox.script.annotation.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;

public class ScriptAnnotationTopLevel extends ObjectTopLevel {
	private static final long serialVersionUID = -3228079633589503494L;

	public ScriptAnnotationTopLevel(Object globalObject, JavaScriptEngine browserScriptEngine) {
		super(globalObject, browserScriptEngine, new ScriptAnnotationImplementor(globalObject, browserScriptEngine));
	}

}

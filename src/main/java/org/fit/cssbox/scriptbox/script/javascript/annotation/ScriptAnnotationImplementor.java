package org.fit.cssbox.scriptbox.script.javascript.annotation;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectImplementor;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectMembers;

public class ScriptAnnotationImplementor extends ObjectImplementor {
		
	public ScriptAnnotationImplementor(Object implementedObject, BrowserScriptEngine browserScriptEngine) {
		super(new ObjectMembers(
				new ScriptAnnotationObjectMembersResolver(implementedObject, browserScriptEngine)), 
				browserScriptEngine
			);
	}
}

package org.fit.cssbox.scriptbox.script.javascript.annotation;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectImplementor;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectMembers;

public class ScriptAnnotationImplementor extends ObjectImplementor {
		
	public ScriptAnnotationImplementor(Object implementedObject, BrowserScriptEngine browserScriptEngine) {
		super(getClassMembers(implementedObject, browserScriptEngine), browserScriptEngine);
	}
	
	protected static ObjectMembers getClassMembers(Object object, BrowserScriptEngine engine) {
		ClassMembersResolverFactory resolverFactory = new ScriptAnnotationClassMembersResolverFactory(engine);
		ObjectMembers objectMembers = ObjectMembers.getObjectMembers(object, resolverFactory);
		return objectMembers;
	}
}

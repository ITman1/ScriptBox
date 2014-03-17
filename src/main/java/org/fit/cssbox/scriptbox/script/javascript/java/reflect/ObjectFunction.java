package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Method;

public class ObjectFunction extends ObjectMember<ClassFunction, Method> implements MemberFunction {

	public ObjectFunction(Object object, ClassFunction classFunction) {
		super(object, classFunction);
	}
	
	public ObjectFunction(Object object, Method method) {
		this(object, new ClassFunction(object.getClass(), method));
	}
	
	public Object invoke(Object ...args) {		
		return classMember.invoke(object, args);
	}

}

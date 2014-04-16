package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Constructor;

public class ObjectConstructor extends ObjectMember<ClassConstructor, Constructor<?>> implements MemberConstructor {
	public ObjectConstructor(Object object, ClassConstructor classConstructor) {
		super(object, classConstructor);
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	public ObjectConstructor(Object object, Constructor<?> constructor) {
		this(object, new ClassConstructor(object.getClass(), constructor));
	}
}

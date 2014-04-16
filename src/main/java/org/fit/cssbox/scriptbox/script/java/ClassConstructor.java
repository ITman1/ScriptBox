package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;

public class ClassConstructor extends ClassMember<Constructor<?>> implements MemberConstructor {
	public ClassConstructor(Class<?> clazz, Constructor<?> constructor) {
		super(clazz, constructor, new String[0]);
	}
	
	public static String extractConstructorName(Method method) {
		return method.getName();
	}
	
	public Object newInstance(Object ...args) {		
		try {
			Object returnObject = member.newInstance(args);
			
			return ClassField.unwrap(returnObject);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	public static boolean isConstructor(Constructor<?> constructor) {
		return true;
	}
}

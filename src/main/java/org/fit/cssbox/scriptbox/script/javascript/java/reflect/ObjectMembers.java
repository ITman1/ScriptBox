package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.util.Map.Entry;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;

public final class ObjectMembers extends ClassMembers {

	private Object object;
	private ClassMembers classMembers;
	
	private ObjectMembers(Object object) {
		this(object, createDefaultClassMembers(object));
	}
	
	private ObjectMembers(Object object, ClassMembers classMembers) {
		Class<?> objectClass = object.getClass();
		Class<?> classMembersClass = classMembers.getClazz();
		
		if (!objectClass.equals(classMembersClass)) {
			throw new InternalException("Class of the object does not belong to class of the class members!");
		}
		
		this.object = object;
		this.classMembers = classMembers;
	}
	
	public Object getObject() {
		return object;
	}

	public ClassMembers getClassMembers() {
		return classMembers;
	}

	@Override
	public ClassFunction getObjectGetter() {
		return classMembers.getObjectGetter();
	}

	@Override
	public boolean hasMemberWithName(String name) {
		return classMembers.hasMemberWithName(name);
	}

	@Override
	public Set<ClassMember<?>> getMembersByName(String name) {
		return classMembers.getMembersByName(name);
	}

	@Override
	public Set<Entry<String, Set<ClassMember<?>>>> getNamedMemberEtrySet() {
		return classMembers.getNamedMemberEtrySet();
	}

	@Override
	public Set<String> getMemberNames() {
		return classMembers.getMemberNames();
	}

	@Override
	public Set<String> getEnumerableMemberNames() {
		return classMembers.getEnumerableMemberNames();
	}
	
	public static ObjectMembers getObjectMembers(Object object, ClassMembersResolverFactory resolverFactory) {
		ClassMembersResolver resolver = resolverFactory.create(object.getClass());
		ObjectMembers objectMembers = getObjectMembers(object, resolver);
		return objectMembers;
	}
		
	public static ObjectMembers getObjectMembers(Object object, ClassMembers classMembers) {
		ObjectMembers objectMembers = new ObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	public static ObjectMembers getObjectMembers(Object object, ClassMembersResolver resolver) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(resolver);
		ObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	public static ObjectMembers getObjectMembers(Object object) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(object.getClass());
		ObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	private static ClassMembers createDefaultClassMembers(Object object) {
		Class<?> objectClass = object.getClass();
		ClassMembers classMembers = new DefaultClassMembers(objectClass);
		
		return classMembers;
	}
}

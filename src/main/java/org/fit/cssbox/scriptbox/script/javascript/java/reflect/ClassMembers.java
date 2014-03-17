package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.util.Map.Entry;
import java.util.Set;

public abstract class ClassMembers {
	
	protected Class<?> clazz;
	protected ClassMembersResolver membersResolver;
	
	// For adapters, wrappers etc.
	protected ClassMembers() {}
	
	public ClassMembers(ClassMembersResolver membersResolver) {
		this.clazz = membersResolver.getClazz();
		this.membersResolver = membersResolver;
	}
	
	public ClassMembers(Class<?> clazz) {
		this(createDefaultClassMembersResolver(clazz));
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public ClassMembersResolver getClassMembersResolver() {
		return membersResolver;
	}
	
	public abstract ClassFunction getObjectGetter();
	public abstract boolean hasMemberWithName(String name);
	public abstract Set<ClassMember<?>> getMembersByName(String name);
	public abstract Set<Entry<String, Set<ClassMember<?>>>> getNamedMemberEtrySet();
	public abstract Set<String> getMemberNames();
	public abstract Set<String> getEnumerableMemberNames();
	
	protected static ClassMembersResolver createDefaultClassMembersResolver(Class<?> clazz) {
		ClassMembersResolverFactory factory = new DefaultClassMembersResolverFactory();
		ClassMembersResolver membersResolver = factory.create(clazz);
		
		return membersResolver;
	}
}

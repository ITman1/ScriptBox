package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Member;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.MemberException;

public class ObjectMember<ClassMemberType extends ClassMember<MemberType>, MemberType extends Member> extends ClassMember<MemberType> {

	protected Object object;
	protected ClassMemberType classMember;
	
	public ObjectMember(Object object, ClassMemberType classMember) {
		super(classMember.clazz, classMember.member);
		this.object = object;
		this.classMember = classMember;
		
		Class<?> objectClass = object.getClass();
		Class<?> classMemberClass = classMember.getClazz();
		
		if (!objectClass.equals(classMemberClass)) {
			throw new MemberException("Class of the object does not belongs to class of the class member!");
		}
	}
	
	public Object getObject() {
		return object;
	}
	
	public Class<?> getObjectType() {
		return clazz;
	}

}

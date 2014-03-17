package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Member;

public class ClassMember<MemberType extends Member> implements MemberClass<MemberType> {
	public final static String ENUMERABLE = "enumerable";
	public final static String PERNAMENT = "pernament";
	public final static String DEFAULT_OPTIONS[] = {ENUMERABLE, PERNAMENT};
	
	protected Class<?> clazz;
	protected MemberType member;
	
	protected String[] options;
	
	public ClassMember(Class<?> clazz, MemberType member) {
		this(clazz, member, null);
	}
	
	public ClassMember(Class<?> clazz, MemberType member, String[] options) {
		this.clazz = clazz;
		this.member = member;
		this.options = (options == null)? DEFAULT_OPTIONS : options;
	}
	
	@Override
	public MemberType getMember() {
		return member;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
		
	public boolean hasOption(String option) {
		for (String currOption : options) {
			if (currOption.equals(option)) {
				return true;
			}
		}
		
		return false;
	}
}

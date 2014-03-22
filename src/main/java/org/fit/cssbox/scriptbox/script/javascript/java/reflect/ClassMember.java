package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Member;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(7, 43).
			append(clazz).
			append(member).
			toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof ClassMember<?>))
			return false;

		ClassMember<?> rhs = (ClassMember<?>) obj;
		return new EqualsBuilder().
			append(clazz, rhs.clazz).
			append(member, rhs.member).
			isEquals();
	}
}

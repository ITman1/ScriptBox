package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Member;

public class ObjectMember<MemberType extends Member> {
	public final static String ENUMERABLE = "enumerable";
	public final static String PERNAMENT = "pernament";
	public final static String DEFAULT_OPTIONS[] = {ENUMERABLE, PERNAMENT};
	
	protected Object object;
	protected MemberType member;
	protected String[] options;
	
	public ObjectMember(Object object, MemberType member) {
		this(object, member, null);
	}
	
	public ObjectMember(Object object, MemberType member, String[] options) {
		this.object = object;
		this.member = member;
		this.options = (options == null)? DEFAULT_OPTIONS : options;
	}
	
	public Object getObject() {
		return object;
	}
	
	public MemberType getMember() {
		return member;
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

package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Member;

public interface InvocableMember<MemberType extends Member> extends MemberClass<MemberType> {
	public Class<?>[] getParameterTypes();
}

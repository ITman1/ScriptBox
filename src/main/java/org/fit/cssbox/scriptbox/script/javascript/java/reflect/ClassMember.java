/**
 * ClassMember.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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

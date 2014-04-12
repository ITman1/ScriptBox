/**
 * ClassMembers.java
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

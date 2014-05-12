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

package org.fit.cssbox.scriptbox.script.reflect;

import java.util.Map.Entry;
import java.util.Set;

/**
 * Abstract class for collecting all members of some specific class 
 * that will be visible inside scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ClassMembers {
	
	protected Class<?> clazz;
	protected ClassMembersResolver membersResolver;
	
	// For adapters, wrappers etc.
	protected ClassMembers() {}
	
	/**
	 * Constructs class members which are resolved using the passed members resolver.
	 * 
	 * @param membersResolver Members resolver used to resolve the class members.
	 */
	public ClassMembers(ClassMembersResolver membersResolver) {
		this.clazz = membersResolver.getClazz();
		this.membersResolver = membersResolver;
	}
	
	/**
	 * Constructs class members which are resolved using the default members resolver - 
	 * created by {@link DefaultClassMembersResolverFactory}.
	 * 
	 * @param clazz Class of which members should be resolved.
	 */
	public ClassMembers(Class<?> clazz) {
		this(createDefaultClassMembersResolver(clazz));
	}
	
	/**
	 * Returns class of which are these class members.
	 * 
	 * @return Class of which are these class members
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	
	/**
	 * Returns used class members resolver for resolving this class members.
	 * 
	 * @return Used class members resolver for resolving this class members
	 */
	public ClassMembersResolver getClassMembersResolver() {
		return membersResolver;
	}
	
	/**
	 * Returns all class visible constructs.
	 * 
	 * @return Set of all visible class constructors.
	 */
	public abstract Set<ClassConstructor> getConstructors();
	
	/**
	 * Returns object getter function if there is any.
	 * 
	 * @return Object getter function if there is any, otherwise null.
	 */
	public abstract ClassFunction getObjectGetter();
	
	/**
	 * Tests whether has this class the member with the passed name.
	 * 
	 * @param name Name of the member that should be tested for existence.
	 * @return True if resolved wrapped class contains member of the passed name, otherwise false.
	 */
	public abstract boolean hasMemberWithName(String name);
	
	/**
	 * Returns all members with the given name.
	 * 
	 * @param name Name which should be searched and of which members should be returned.
	 * @return  Set with all resolved members with the given name.
	 */
	public abstract Set<ClassMember<?>> getMembersByName(String name);
	
	/**
	 * Returns all class members.
	 * 
	 * @return Set with all resolved members of the wrapped class.
	 */
	public abstract Set<Entry<String, Set<ClassMember<?>>>> getNamedMemberEtrySet();
	
	/**
	 * Returns all member names.
	 * 
	 * @return Set with all resolved member names that contains wrapped class.
	 */
	public abstract Set<String> getMemberNames();
	
	/**
	 * Returns all member names that are enumerable.
	 * 
	 * @return Set with all resolved member names that contains wrapped class and are enumerable.
	 */
	public abstract Set<String> getEnumerableMemberNames();
	
	/**
	 * Creates default class members resolver for given class.
	 * 
	 * @param clazz Class of the class members.
	 * @return Default class members resolver for given class
	 */
	protected static ClassMembersResolver createDefaultClassMembersResolver(Class<?> clazz) {
		ClassMembersResolverFactory factory = new DefaultClassMembersResolverFactory();
		ClassMembersResolver membersResolver = factory.create(clazz);
		
		return membersResolver;
	}
}

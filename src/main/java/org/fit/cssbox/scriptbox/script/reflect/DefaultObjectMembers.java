/**
 * ObjectMembers.java
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

import org.fit.cssbox.scriptbox.script.exceptions.InternalException;

/**
 * Default implementation of the object members which are determined 
 * from the passed class members or default constructed class members.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public final class DefaultObjectMembers extends ObjectMembers {

	private ClassMembers classMembers;
	
	/**
	 * Constructs the object members with from the default class members.
	 * 
	 * @param object Object of which members should be resolved.
	 */
	private DefaultObjectMembers(Object object) {
		this(object, createDefaultClassMembers(object));
	}

	/**
	 * Constructs the object members with from the given class members.
	 * 
	 * @param object Object of which members should be resolved.
	 * @param classMembers Given class members that are used to determine the object members.
	 */
	private DefaultObjectMembers(Object object, ClassMembers classMembers) {
		super(object);
		
		Class<?> objectClass = object.getClass();
		Class<?> classMembersClass = classMembers.getClazz();
		
		if (!objectClass.equals(classMembersClass)) {
			throw new InternalException("Class of the object does not belong to class of the class members!");
		}
		
		this.object = object;
		this.classMembers = classMembers;
	}

	/**
	 * Returns class members that are used for resolving of object members.
	 * 
	 * @return Class members that are used for resolving of object members
	 */
	public ClassMembers getClassMembers() {
		return classMembers;
	}

	@Override
	public Set<ClassConstructor> getConstructors() {
		return classMembers.getConstructors();
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
	
	/**
	 * Constructs default object members for given object from resolver factory.
	 * 
	 * @param object Object which will be wrapped and its members will be resolved.
	 * @param resolverFactory Factory used for members resolving.
	 * @return Default object members for given object.
	 */
	public static DefaultObjectMembers getObjectMembers(Object object, ClassMembersResolverFactory resolverFactory) {
		ClassMembersResolver resolver = resolverFactory.create(object.getClass());
		DefaultObjectMembers objectMembers = getObjectMembers(object, resolver);
		return objectMembers;
	}
		
	/**
	 * Constructs default object members for given object from given class members.
	 * 
	 * @param object Object which will be wrapped and its members will be resolved.
	 * @param classMembers Class members used for construction of the object members.
	 * @return Default object members for given object.
	 */
	public static DefaultObjectMembers getObjectMembers(Object object, ClassMembers classMembers) {
		DefaultObjectMembers objectMembers = new DefaultObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	/**
	 * Constructs default object members for given object from given resolver.
	 * 
	 * @param object Object which will be wrapped and its members will be resolved.
	 * @param resolver Resolver used for members resolving.
	 * @return Default object members for given object.
	 */
	public static DefaultObjectMembers getObjectMembers(Object object, ClassMembersResolver resolver) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(resolver);
		DefaultObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	/**
	 * Constructs default object members for given object using default class members.
	 * 
	 * @param object Object which will be wrapped and its members will be resolved.
	 * @return Default object members for given object.
	 */
	public static DefaultObjectMembers getObjectMembers(Object object) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(object.getClass());
		DefaultObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	private static ClassMembers createDefaultClassMembers(Object object) {
		Class<?> objectClass = object.getClass();
		ClassMembers classMembers = new DefaultClassMembers(objectClass);
		
		return classMembers;
	}
}

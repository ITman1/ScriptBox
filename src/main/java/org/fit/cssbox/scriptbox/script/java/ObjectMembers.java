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

package org.fit.cssbox.scriptbox.script.java;

import java.util.Map.Entry;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.exceptions.InternalException;

public final class ObjectMembers extends ClassMembers {

	private Object object;
	private ClassMembers classMembers;
	
	private ObjectMembers(Object object) {
		this(object, createDefaultClassMembers(object));
	}
	
	private ObjectMembers(Object object, ClassMembers classMembers) {
		Class<?> objectClass = object.getClass();
		Class<?> classMembersClass = classMembers.getClazz();
		
		if (!objectClass.equals(classMembersClass)) {
			throw new InternalException("Class of the object does not belong to class of the class members!");
		}
		
		this.object = object;
		this.classMembers = classMembers;
	}
	
	public Object getObject() {
		return object;
	}

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
	
	public static ObjectMembers getObjectMembers(Object object, ClassMembersResolverFactory resolverFactory) {
		ClassMembersResolver resolver = resolverFactory.create(object.getClass());
		ObjectMembers objectMembers = getObjectMembers(object, resolver);
		return objectMembers;
	}
		
	public static ObjectMembers getObjectMembers(Object object, ClassMembers classMembers) {
		ObjectMembers objectMembers = new ObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	public static ObjectMembers getObjectMembers(Object object, ClassMembersResolver resolver) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(resolver);
		ObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	public static ObjectMembers getObjectMembers(Object object) {
		// TODO: This could use some kind of cache
		ClassMembers classMembers = new DefaultClassMembers(object.getClass());
		ObjectMembers objectMembers = getObjectMembers(object, classMembers);
		return objectMembers;
	}
	
	private static ClassMembers createDefaultClassMembers(Object object) {
		Class<?> objectClass = object.getClass();
		ClassMembers classMembers = new DefaultClassMembers(objectClass);
		
		return classMembers;
	}
}

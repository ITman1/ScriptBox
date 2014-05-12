/**
 * ObjectMember.java
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

import java.lang.reflect.Member;

import org.fit.cssbox.scriptbox.script.exceptions.MemberException;

/**
 * Base class for representing the object member 
 * - class member which have an associated object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectMember<ClassMemberType extends ClassMember<MemberType>, MemberType extends Member> extends ClassMember<MemberType> {

	protected Object object;
	protected ClassMemberType classMember;
	
	/**
	 * Constructs object member for given object using class member.
	 * 
	 * @param object Object that contains the passed member.
	 * @param classMember Class member that is wrapped by this class and associated with the object.
	 */
	public ObjectMember(Object object, ClassMemberType classMember) {
		super(classMember.clazz, classMember.member, classMember.options);
		this.object = object;
		this.classMember = classMember;
		
		Class<?> objectClass = object.getClass();
		Class<?> classMemberClass = classMember.getClazz();
		
		if (!objectClass.equals(classMemberClass)) {
			throw new MemberException("Class of the object does not belongs to class of the class member!");
		}
	}
	
	/**
	 * Returns associated object.
	 * 
	 * @return Associated object.
	 */
	public Object getObject() {
		return object;
	}
	
	/**
	 * Returns object class.
	 * 
	 * @return Object class.
	 */
	public Class<?> getObjectType() {
		return clazz;
	}

}

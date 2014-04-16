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

package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Member;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.MemberException;

public class ObjectMember<ClassMemberType extends ClassMember<MemberType>, MemberType extends Member> extends ClassMember<MemberType> {

	protected Object object;
	protected ClassMemberType classMember;
	
	public ObjectMember(Object object, ClassMemberType classMember) {
		super(classMember.clazz, classMember.member);
		this.object = object;
		this.classMember = classMember;
		
		Class<?> objectClass = object.getClass();
		Class<?> classMemberClass = classMember.getClazz();
		
		if (!objectClass.equals(classMemberClass)) {
			throw new MemberException("Class of the object does not belongs to class of the class member!");
		}
	}
	
	public Object getObject() {
		return object;
	}
	
	public Class<?> getObjectType() {
		return clazz;
	}

}

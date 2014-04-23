/**
 * ObjectField.java
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectField extends ObjectMember<ClassField, Field> implements MemberField {	
	public ObjectField(Object object, ClassField classField) {
		super(object, classField);
	}
	
	public ObjectField(Object object, Field member) {
		this(object, new ClassField(object.getClass(), member));
	}

	public Object get() {
		return classMember.get(object);
	}
	
	public void set(Object value) {
		classMember.set(object, value);
	}	

	@Override
	public Object getObject() {
		return null;
	}

	@Override
	public Method getFieldGetterMethod() {
		return classMember.getFieldGetterMethod();
	}

	@Override
	public Method getFieldSetterMethod() {
		return classMember.getFieldSetterMethod();
	}

	@Override
	public boolean hasGetOverride() {
		return classMember.hasGetOverride();
	}

	@Override
	public boolean hasSetOverride() {
		return classMember.hasSetOverride();
	}	
}

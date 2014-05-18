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

package org.fit.cssbox.scriptbox.script.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Represents the field of the object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectField extends ObjectMember<ClassField, Field> implements FieldMember {	
	
	/**
	 * Constructs object field for given object using class field.
	 * 
	 * @param object Object containing the object field.
	 * @param classFunction Class field that is wrapped by this class and associated with the object.
	 */
	public ObjectField(Object object, ClassField classField) {
		super(object, classField);
	}
	
	/**
	 * Constructs object field for given object from the passed field.
	 * 
	 * @param object Object containing the object field.
	 * @param field Field that will be wrapped into class field.
	 */
	public ObjectField(Object object, Field field) {
		this(object, new ClassField(object.getClass(), field));
	}

	/**
	 * Returns value of the field.
	 * 
	 * @return Field value.
	 */
	public Object get() {
		return classMember.get(object);
	}
	
	/**
	 * Sets new value of the field.
	 * 
	 * @param value New field value.
	 */
	public void set(Object value) {
		classMember.set(object, value);
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

	@Override
	public Class<?> getFieldType() {
		return classMember.getFieldType();
	}	
}

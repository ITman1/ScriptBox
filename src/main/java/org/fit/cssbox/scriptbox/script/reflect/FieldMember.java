/**
 * FieldMember.java
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
 * Interface for field members.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public interface FieldMember extends JavaMember<Field> {
	/**
	 * Returns field getter method.
	 * 
	 * @return Field getter method.
	 */
	public Method getFieldGetterMethod();
	
	/**
	 * Returns field setter method.
	 * 
	 * @return Field setter method.
	 */	
	public Method getFieldSetterMethod();
	
	/**
	 * Tests whether has this member set get override.
	 * 
	 * @return True if has this member set get override.
	 */
	public boolean hasGetOverride();
	
	/**
	 * Tests whether has this member set set override.
	 * 
	 * @return True if has this member set set override.
	 */
	public boolean hasSetOverride();
	
	/**
	 * Returns type of this field member.
	 * 
	 * @return Type of this field member.
	 */
	public Class<?> getFieldType();
}

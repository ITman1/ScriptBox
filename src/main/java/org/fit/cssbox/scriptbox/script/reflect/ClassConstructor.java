/**
 * ClassConstructor.java
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

import java.lang.reflect.Constructor;

import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;

/**
 * Represents the constructor of the class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ClassConstructor extends ClassMember<Constructor<?>> implements ConstructorMember {
	
	/**
	 * Constructs class constructor wrapper.
	 * 
	 * @param clazz Class to which belongs wrapped constructor.
	 * @param constructor Constructor to be wrapped.
	 */
	public ClassConstructor(Class<?> clazz, Constructor<?> constructor) {
		super(clazz, constructor, new String[0]);
	}
	
	/**
	 * Calls wrapped constructor and creates new instance of the object with passed arguments.
	 * 
	 * @param args Arguments passed into the constructors.
	 * @return New instance of the wrapped class.
	 */
	public Object newInstance(Object ...args) {		
		try {
			Object returnObject = member.newInstance(args);
			
			return ClassField.unwrap(returnObject);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	/**
	 * Tests whether is passed constructor the valid constructor.
	 * 
	 * @param constructor Constructor to be tested.
	 * @return True if is passed constructor the valid constructor, otherwise false.
	 */
	public static boolean isConstructor(Constructor<?> constructor) {
		return true;
	}
}

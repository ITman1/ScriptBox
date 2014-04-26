/**
 * ClassMembersResolver.java
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Abstract class for creating the class members resolvers - classes
 * that ensure resolving of the valid members that are visible
 * from the class inside the scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public abstract class ClassMembersResolver {
	protected Class<?> clazz;
	
	/**
	 * Constructs class resolver for a given class.
	 * 
	 * @param clazz Class that should be resolved for a visible members.
	 */
	public ClassMembersResolver(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	/**
	 * Returns class which is this resolver resolving.
	 * 
	 * @return Class which is resolved by this resolver.
	 */
	public Class<?> getClazz() {
		return clazz;
	}
	
	/**
	 * Tests whether is the passed method the object getter method.
	 * 
	 * @param method Method to be tested.
	 * @return True if the passed method is object getter method.
	 */
	public abstract boolean isObjectGetter(Method method);
	
	/**
	 * Tests whether the passed method is the getter method for some field.
	 * 
	 * @param method Method to be tested.
	 * @return True if is passed method the getter method, otherwise false.
	 */
	public abstract boolean isGetter(Method method);
	
	/**
	 * Tests whether the passed method is the setter method for some field.
	 * 
	 * @param method Method to be tested.
	 * @return True if is passed method the setter method, otherwise false.
	 */
	public abstract boolean isSetter(Method method);
	
	/**
	 * Tests whether passed method is the function method.
	 * 
	 * @param method Method to be tested.
	 * @return True if passed method is the function method.
	 */
	public abstract boolean isFunction(Method method);
	
	/**
	 * Tests whether is passed constructor the valid constructor.
	 * 
	 * @param constructor Constructor to be tested.
	 * @return True if is passed constructor the valid constructor, otherwise false.
	 */
	public abstract boolean isConstructor(Constructor<?> constructor);
	
	/**
	 * Tests whether the passed field is the field.
	 * 
	 * @param field Field to be tested.
	 * @return True if passed field is the field, otherwise false.
	 */
	public abstract boolean isField(Field field);
	
	/**
	 * Extracts the name for field given by a getter method.
	 * 
	 * @param method Field getter method from which to extract the name.
	 * @return Name of the field.
	 */
	public abstract String extractFieldNameFromGetter(Method method);
	
	/**
	 * Extracts the name for field given by a setter method.
	 * 
	 * @param method Field setter method from which to extract the name.
	 * @return Name of the field.
	 */
	public abstract String extractFieldNameFromSetter(Method method);
	
	/**
	 * Extracts the name for passed field.
	 * 
	 * @param field Field.
	 * @return Name of the field.
	 */
	public abstract String extractFieldName(Field field);
	
	/**
	 * Extract the function name from the method.
	 * 
	 * @param method Method of which name should be extracted.
	 * @return Name of the function.
	 */
	public abstract String extractFunctionName(Method method);
	
	/**
	 * Constructs class field from a given field getter, field setter and field.
	 * 
	 * @param objectFieldGetter Field getter for instantiation of the class field.
	 * @param objectFieldSetter Field setter for instantiation of the class field.
	 * @param field Field for instantiation of the class field.
	 * @return New constructed class field for the passed arguments.
	 */
	public abstract ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field);
	
	/**
	 * Constructs class function from a given method.
	 * 
	 * @param method Method which will be wrapped by class function.
	 * @return New constructed class function for the passed method.
	 */
	public abstract ClassFunction constructClassFunction(Method method);
	
	/**
	 * Constructs class constructor from a given method.
	 * 
	 * @param constructor Constructor which will be wrapped by class function.
	 * @return New constructed class constructor for the passed method.
	 */
	public abstract ClassConstructor constructClassConstructor(Constructor<?> constructor);
}

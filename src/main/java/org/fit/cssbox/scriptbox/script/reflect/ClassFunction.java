/**
 * ClassFunction.java
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

import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;

/**
 * Represents the function of the class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ClassFunction extends ClassMember<Method> implements FunctionMember {	
	
	/**
	 * Constructs class method wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped method.
	 * @param functionMethod Method to be wrapped.
	 */
	public ClassFunction(Class<?> clazz, Method functionMethod) {
		this(clazz, functionMethod, null);
	}
	
	/**
	 * Constructs class method wrapper.
	 * 
	 * @param clazz Class to which belongs the wrapped method.
	 * @param functionMethod Method to be wrapped.
	 * @param options Options associated with this class method.
	 */
	public ClassFunction(Class<?> clazz, Method functionMethod, String[] options) {
		super(clazz, functionMethod, options);
	}
	
	/**
	 * Invokes the wrapped method of the passed object with the given arguments.
	 * 
	 * @param object Object which contains the method.
	 * @param args Arguments used for calling the method.
	 * @return Return of the wrapped method.
	 */
	public Object invoke(Object object, Object ...args) {
		if (!clazz.isInstance(object)) {
			throw new FunctionException("Passed object is not instance of the class to which function belongs to.");
		}
		
		try {
			Object returnObject = member.invoke(object, args);
			
			return ClassField.unwrap(returnObject);
		} catch (Exception e) {
			throw new UnknownException(e);
		}
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	@Override
	public Class<?> getReturnType() {
		return member.getReturnType();
	}
	
	/**
	 * Tests whether are passed arguments assignable to the given types.
	 * 
	 * @param args Arguments to be tested.
	 * @param types Types.
	 * @return True if are passed arguments assignable to the given types.
	 */
	public static boolean isAssignableTypes(Object[] args, Class<?>... types) {
		if (args.length != types.length) {
			return false;
		}
		
		for (int i = 0; i < args.length; i++) {
			Class<?> argClass = (args[i] == null)? null : args[i].getClass();
			Class<?> paramClass = types[i];
			
			if (paramClass.isPrimitive() && argClass == null) {
				return false;
			} else if (!paramClass.isPrimitive() && argClass == null) {
				continue; 
			} else if (!ClassUtils.isAssignable(argClass, paramClass, true)) {
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Tests whether is the passed method the object getter method.
	 * 
	 * @param clazz Class that should contain the object getter method.
	 * @param method Method to be tested.
	 * @return True if the passed method is object getter method.
	 */
	public static boolean isObjectGetterMethod(Class<?> clazz, Method method) {
		if (ObjectGetter.class.isAssignableFrom(clazz)) {
			Method getterMethod = ClassFunction.getObjectGetterMetod(clazz);	
			return method.equals(getterMethod);
		}
		return false;
	}
	
	/**
	 * Tests whether passed method is the function method.
	 * 
	 * @param clazz Class which contains the passed method.
	 * @param method Method to be tested.
	 * @return True if passed method is the function method.
	 */
	public static boolean isFunction(Class<?> clazz, Method method) {
		return !isObjectGetterMethod(clazz, method);
	}
	
	/**
	 * Extract the function name from the method.
	 * 
	 * @param method Method of which name should be extracted.
	 * @return Name of the function.
	 */
	public static String extractFunctionName(Method method) {
		return method.getName();
	}
	
	/**
	 * Returns method of the class that equals to the object getter method.
	 *  
	 * @param clazz Class which should contain the object getter method.
	 * @return Object getter method.
	 */
	private static Method getObjectGetterMetod(Class<?> clazz) {
		try {
			return ClassUtils.getPublicMethod(clazz, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
		} catch (Exception e) {
			throw new InternalException(e);
		}
	}
}

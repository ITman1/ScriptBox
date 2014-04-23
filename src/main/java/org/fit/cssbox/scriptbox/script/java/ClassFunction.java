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

package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.exceptions.UnknownException;

public class ClassFunction extends ClassMember<Method> implements MemberFunction {	
	
	public ClassFunction(Class<?> clazz, Method functionMethod) {
		this(clazz, functionMethod, null);
	}
	
	public ClassFunction(Class<?> clazz, Method functionMethod, String[] options) {
		super(clazz, functionMethod, options);
	}
	
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
	
	public static Method getObjectGetterMetod(Class<?> clazz) {
		try {
			return ClassUtils.getPublicMethod(clazz, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
		} catch (Exception e) {
			throw new InternalException(e);
		}
	}
	
	public static boolean isObjectGetterMethod(Class<?> clazz, Method method) {
		if (ObjectGetter.class.isAssignableFrom(clazz)) {
			Method getterMethod = ClassFunction.getObjectGetterMetod(clazz);	
			return method.equals(getterMethod);
		}
		return false;
	}
	
	public static boolean isFunction(Class<?> clazz, Method method) {
		return !isObjectGetterMethod(clazz, method);
	}
	
	public static String extractFunctionName(Method method) {
		return method.getName();
	}
}

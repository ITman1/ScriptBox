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

package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Method;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FunctionException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.UnknownException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectScriptable;

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
	
	public static Object[] castArgs(Class<?>[] expectedTypes, Object... args) {

			
		if (expectedTypes != null && args != null) {

			if (expectedTypes.length <= args.length + 1 && expectedTypes.length > 0) {
				Class<?> lastType = expectedTypes[expectedTypes.length - 1];
				if (lastType.isArray()) {
					Class<?> arrayType = lastType.getComponentType();
					
					boolean maybeVarargs = true;
					if (expectedTypes.length == args.length) {
						Class<?> lastArg = args[args.length - 1].getClass();
						maybeVarargs = !ClassUtils.isAssignable(lastArg, lastType);
					}
					
					if (maybeVarargs) {
						for (int i = expectedTypes.length - 1; i < args.length; i++) {
							Class<?> argType = args[i].getClass();
							
							if (!ClassUtils.isAssignable(argType, arrayType)) {
								maybeVarargs = false;
								break;
							}
						}
						
						if (maybeVarargs) {
							Object[] oldArgs = args;
							args = new Object[expectedTypes.length];
							
							for (int i = 0; i < expectedTypes.length - 1; i++) {
								args[i] = oldArgs[i];
							}
							
							Object[] varargs = new Object[oldArgs.length - expectedTypes.length + 1];
							
							for (int i = expectedTypes.length - 1; i < oldArgs.length; i++) {
								varargs[i - expectedTypes.length + 1] = oldArgs[i];
							}
							
							args[expectedTypes.length - 1] = varargs;
						}
					}
				}
			}
			
			if (expectedTypes.length == args.length) {
				Object[] castedArgs = new Object[args.length];
				for (int i = 0; i < args.length; i++) {
					Object arg = args[i];
					Class<?> expectedType = expectedTypes[i];
					if (arg == null) {
						castedArgs[i] = null;
					} else if (arg instanceof Double && (expectedType.equals(Integer.class) || expectedType.equals(int.class))) {
						castedArgs[i] = ((Double)arg).intValue();
					} else {
						castedArgs[i] = ObjectScriptable.jsToJava(arg);;
					}
					
					castedArgs[i] = ClassField.wrap(expectedTypes[i], castedArgs[i]);
				}
				
				return castedArgs;
			}
		}
		
		return null;
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

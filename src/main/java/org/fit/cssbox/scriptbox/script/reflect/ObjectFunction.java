/**
 * ObjectFunction.java
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

/**
 * Represents the function of the object.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectFunction extends ObjectMember<ClassFunction, Method> implements FunctionMember {

	/**
	 * Constructs object function for given object using class function.
	 * 
	 * @param object Object containing the object function.
	 * @param classFunction Class function that is wrapped by this class and associated with the object.
	 */
	public ObjectFunction(Object object, ClassFunction classFunction) {
		super(object, classFunction);
	}
	
	/**
	 * Constructs object function for given object from passed method.
	 * 
	 * @param object Object containing the object function.
	 * @param method Method that will be wrapped into class function.
	 */
	public ObjectFunction(Object object, Method method) {
		this(object, new ClassFunction(object.getClass(), method));
	}
	
	@Override
	public Class<?>[] getParameterTypes() {
		return member.getParameterTypes();
	}
	
	@Override
	public Class<?> getReturnType() {
		return classMember.getReturnType();
	}
	
	/**
	 * Invokes the wrapped method with the given arguments.
	 * 
	 * @param args Arguments used for calling the method.
	 * @return Return of the wrapped method.
	 */
	public Object invoke(Object ...args) {		
		return classMember.invoke(object, args);
	}
}

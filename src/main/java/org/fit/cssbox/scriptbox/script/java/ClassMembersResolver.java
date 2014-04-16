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

package org.fit.cssbox.scriptbox.script.java;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ClassMembersResolver {
	protected Class<?> clazz;
	
	public ClassMembersResolver(Class<?> clazz) {
		this.clazz = clazz;
	}
	
	public Class<?> getClazz() {
		return clazz;
	}
	
	public abstract boolean isObjectGetter(Method method);
	public abstract boolean isGetter(Method method);
	public abstract boolean isSetter(Method method);
	public abstract boolean isFunction(Method method);
	public abstract boolean isConstructor(Constructor<?> constructor);
	public abstract boolean isField(Field field);
	public abstract String extractFieldNameFromGetter(Method method);
	public abstract String extractFieldNameFromSetter(Method method);
	public abstract String extractFieldName(Field field);
	public abstract String extractFunctionName(Method method);
	public abstract ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field);
	public abstract ClassFunction constructClassFunction(Method method);
	public abstract ClassConstructor constructClassConstructor(Constructor<?> constructor);
}

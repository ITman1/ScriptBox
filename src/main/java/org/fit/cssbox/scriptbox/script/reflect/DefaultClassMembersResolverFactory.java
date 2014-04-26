/**
 * DefaultClassMembersResolverFactory.java
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
 * Default implementation of the class members resolver factory.
 * Class members resolver created by this factory includes 
 * all members of the wrapped class.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DefaultClassMembersResolverFactory implements ClassMembersResolverFactory {
	class DefaultClassMembersResolver extends ClassMembersResolver {
		
		public DefaultClassMembersResolver(Class<?> clazz) {
			super(clazz);
		}
		
		@Override
		public boolean isObjectGetter(Method method) {
			return ClassFunction.isObjectGetterMethod(clazz, method);
		}
	
		@Override
		public boolean isGetter(Method method) {
			return ClassField.isGetter(method);
		}
		
		@Override
		public boolean isSetter(Method method) {
			return ClassField.isSetter(method);
		}
		
		@Override
		public boolean isFunction(Method method) {
			return ClassFunction.isFunction(clazz, method);
		}
		
		@Override
		public boolean isField(Field field) {
			return ClassField.isField(field);
		}
		
		@Override
		public String extractFieldNameFromGetter(Method method) {
			return ClassField.extractFieldNameFromGetter(method);
		}
		
		@Override
		public String extractFieldNameFromSetter(Method method) {
			return ClassField.extractFieldNameFromSetter(method);
		}
		
		@Override
		public String extractFieldName(Field field) {
			return ClassField.extractFieldName(field);
		}
		
		@Override
		public String extractFunctionName(Method method) {
			return ClassFunction.extractFunctionName(method);
		}
		
		@Override
		public ClassField constructClassField(Method objectFieldGetter, Method objectFieldSetter, Field field) {
			return new ClassField(clazz, objectFieldGetter, objectFieldSetter, field);
		}
		
		@Override
		public ClassFunction constructClassFunction(Method method) {
			return new ClassFunction(clazz, method);
		}

		@Override
		public boolean isConstructor(Constructor<?> constructor) {
			return ClassConstructor.isConstructor(constructor);
		}

		@Override
		public ClassConstructor constructClassConstructor(Constructor<?> constructor) {
			return new ClassConstructor(clazz, constructor);
		}
	}

	@Override
	public ClassMembersResolver create(Class<?> clazz) {
		return new DefaultClassMembersResolver(clazz);
	}	
}

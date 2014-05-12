/**
 * DefaultClassMembers.java
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * Default implementation of the class members.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DefaultClassMembers extends ClassMembers {
	protected Map<String, Set<ClassMember<?>>> members;
	protected Set<ClassConstructor> classConstructors;
	
	protected Set<String> cachedMemberNames;
	protected Set<String> cachedEnumerableMemberNames;
	
	protected ClassFunction objectGetter;
	
	/**
	 * Constructs class members which are resolved using the passed members resolver.
	 * 
	 * @param membersResolver Members resolver used to resolve the class members.
	 */
	public DefaultClassMembers(ClassMembersResolver membersResolver) {
		super(membersResolver);
		
		this.members = new HashMap<String, Set<ClassMember<?>>>();
		this.classConstructors = new HashSet<ClassConstructor>();
		
		inspectClassFields();
		inspectClassFunctions();
		inspectClassConstructors();
	}
	
	/**
	 * Constructs class members which are resolved using the default members resolver - 
	 * created by {@link DefaultClassMembersResolverFactory}.
	 * 
	 * @param clazz Class of which members should be resolved.
	 */
	public DefaultClassMembers(Class<?> clazz) {
		this(ClassMembers.createDefaultClassMembersResolver(clazz));
	}
	
	@Override
	public ClassFunction getObjectGetter() {
		return objectGetter;
	}
	
	@Override
	public boolean hasMemberWithName(String name) {
		return members.containsKey(name);
	}
	
	@Override
	public Set<ClassMember<?>> getMembersByName(String name) {
		return members.get(name);
	}
	
	@Override
	public Set<ClassConstructor> getConstructors() {
		return classConstructors;
	}
	
	@Override
	public Set<Entry<String, Set<ClassMember<?>>>> getNamedMemberEtrySet() {
		return members.entrySet();
	}
	
	@Override
	public Set<String> getMemberNames() {
		return (cachedMemberNames != null)? cachedMemberNames : (cachedMemberNames = members.keySet());
	}
	
	@Override
	public Set<String> getEnumerableMemberNames() {
		if (cachedEnumerableMemberNames == null) {
			cachedEnumerableMemberNames = new HashSet<String>();
			
			for (Entry<String, Set<ClassMember<?>>> entry : members.entrySet()) {
				String name = entry.getKey();
				Set<ClassMember<?>> members = entry.getValue();
				if (members.size() > 0) {
					ClassMember<?> firstMember = members.iterator().next();
					if (firstMember.hasOption(ClassMember.ENUMERABLE)) {
						cachedEnumerableMemberNames.add(name);
					}
				}
			}
		}
		
		return cachedEnumerableMemberNames;
	}
	
	/**
	 * Walks through all class fields and and adds them as a new class field members.
	 */
	protected void inspectClassFields() {
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		Map<String, Field> fields = new HashMap<String, Field>();
		
		for (Method method : clazz.getMethods()){		
			boolean isGetter = false;
			boolean isSetter = false;
			
			if (isGetter = membersResolver.isGetter(method)) {
			} else if (isSetter = membersResolver.isSetter(method)) {
			} else {
				continue;
			}
			
			if (isGetter) {
				String fieldName = membersResolver.extractFieldNameFromGetter(method);
				getters.put(fieldName, method);
			} else if (isSetter) {
				String fieldName = membersResolver.extractFieldNameFromSetter(method);
				setters.put(fieldName, method);
			}
		}
			
		for (Field field : clazz.getFields()) {
			if (!membersResolver.isField(field)) {
				continue;
			}
			
			String fieldName = membersResolver.extractFieldName(field);
			fields.put(fieldName, field);
		}
		
		for (Map.Entry<String, Method> getterEntry : getters.entrySet()) {
			String fieldName = getterEntry.getKey();
			Method classFieldGetter = getterEntry.getValue();
			Method classFieldSetter = setters.get(fieldName);
			Field field = fields.get(fieldName);
			
			setters.remove(fieldName);
			fields.remove(fieldName);
			
			ClassField classField = membersResolver.constructClassField(classFieldGetter, classFieldSetter, field);
			defineClassMember(fieldName, classField);
		}
		
		for (Map.Entry<String, Method> setterEntry : setters.entrySet()) {
			String fieldName = setterEntry.getKey();
			Method classFieldSetter = setterEntry.getValue();
			Field field = fields.get(fieldName);
			
			fields.remove(fieldName);

			ClassField classField = membersResolver.constructClassField(null, classFieldSetter, field);
			defineClassMember(fieldName, classField);
		}
		
		for (Map.Entry<String, Field> setterEntry : fields.entrySet()) {
			String fieldName = setterEntry.getKey();
			Field field = setterEntry.getValue();

			ClassField classField = membersResolver.constructClassField(null, null, field);
			defineClassMember(fieldName, classField);
		}
	}
	
	/**
	 * Walks through all class constructors and and adds them as a new class constructor members.
	 */
	protected void inspectClassConstructors() {
 		Constructor<?>[] constructors = clazz.getConstructors();
 		
		for (Constructor<?> constructor : constructors) {	
			boolean isConstructor = membersResolver.isConstructor(constructor);

			if (!isConstructor) {
				continue;
			}
					
			ClassConstructor classConstructor = membersResolver.constructClassConstructor(constructor);
			
			if (isConstructor) {
				defineClassConstructor(classConstructor);
			}
		}
	}
	
	/**
	 * Walks through all class methods and and adds them as a new class function members.
	 */
	protected void inspectClassFunctions() {
 		Method[] methods = clazz.getMethods();
 		
		for (Method method : methods) {	
			boolean isFunction = membersResolver.isFunction(method);
			boolean isObjectGetter = membersResolver.isObjectGetter(method);
			
			if (!isFunction && !isObjectGetter) {
				continue;
			}
		
			
			ClassFunction classFunction = membersResolver.constructClassFunction(method);
			
			if (isFunction) {
				String methodName = membersResolver.extractFunctionName(method);
				defineClassMember(methodName, classFunction);
			} else if (isObjectGetter) {
				defineObjectGetter(classFunction);
			}			
		}
	}
	
	/**
	 * Adds new constructor into the set of constructors.
	 * 
	 * @param classConstructor New constructor to be added into the list of constructors.
	 */
	protected void defineClassConstructor(ClassConstructor classConstructor) {
		classConstructors.add(classConstructor);
	}
	
	/**
	 * Adds new class member into map of class members.
	 * @param name Name of the class member.
	 * @param member Member to be added into the members map.
	 */
	protected void defineClassMember(String name, ClassMember<?> member) {
		if (!members.containsKey(name)) {
			members.put(name, new HashSet<ClassMember<?>>());
		}
		
		Set<ClassMember<?>> memberSet = members.get(name);
		memberSet.add(member);
	}
	
	/**
	 * Stores the function as a object getter.
	 * 
	 * @param member New object getter to stored.
	 */
	protected void defineObjectGetter(ClassFunction member) {
		objectGetter = member;
	}
}

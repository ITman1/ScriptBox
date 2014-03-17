package org.fit.cssbox.scriptbox.script.javascript.java.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class DefaultClassMembers extends ClassMembers {
	protected Map<String, Set<ClassMember<?>>> members;
	protected Set<String> cachedMemberNames;
	protected Set<String> cachedEnumerableMemberNames;
	
	protected ClassFunction objectGetter;
	
	public DefaultClassMembers(ClassMembersResolver membersResolver) {
		super(membersResolver);
		this.members = new HashMap<String, Set<ClassMember<?>>>();
		inspectClassFields();
		inspectClassFunctions();
	}
	
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
	
	protected void defineClassMember(String name, ClassMember<?> member) {
		if (!members.containsKey(name)) {
			members.put(name, new HashSet<ClassMember<?>>());
		}
		
		Set<ClassMember<?>> memberSet = members.get(name);
		memberSet.add(member);
	}
	
	protected void defineObjectGetter(ClassFunction member) {
		objectGetter = member;
	}
}

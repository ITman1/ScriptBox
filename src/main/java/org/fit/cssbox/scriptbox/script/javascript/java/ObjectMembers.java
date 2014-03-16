package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ObjectMembers {

	private static final long serialVersionUID = 6918558556769476746L;
	
	protected Object object;
	protected Class<?> objectType;
	protected ObjectMembersResolver membersResolver;
	protected Map<String, Set<ObjectMember<?>>> members;
	protected Set<String> cachedMemberNames;
	protected Set<String> cachedEnumerableMemberNames;
	
	public ObjectMembers(ObjectMembersResolver membersResolver) {
		this.object = membersResolver.getObject();
		this.objectType = membersResolver.getObjectType();
		this.membersResolver = membersResolver;
		this.members = new HashMap<String, Set<ObjectMember<?>>>();
		inspectObjectFields();
		inspectObjectFunctions();
	}
	
	public ObjectMembers(Object object) {
		this(new DefaultObjectMembersResolver(object));
	}
	
	public Class<?> getObjectType() {
		return objectType;
	}
	
	public Object getObject() {
		return object;
	}
	
	public boolean hasMemberWithName(String name) {
		return members.containsKey(name);
	}
	
	public Set<ObjectMember<?>> getMembersByName(String name) {
		return members.get(name);
	}
	
	public Set<Entry<String, Set<ObjectMember<?>>>> getNamedMemberEtrySet() {
		return members.entrySet();
	}
	
	public Set<String> getMemberNames() {
		return (cachedMemberNames != null)? cachedMemberNames : (cachedMemberNames = members.keySet());
	}
	
	public Set<String> getEnumerableMemberNames() {
		if (cachedEnumerableMemberNames == null) {
			cachedEnumerableMemberNames = new HashSet<String>();
			
			for (Entry<String, Set<ObjectMember<?>>> entry : members.entrySet()) {
				String name = entry.getKey();
				Set<ObjectMember<?>> members = entry.getValue();
				if (members.size() > 0) {
					ObjectMember<?> firstMember = members.iterator().next();
					if (firstMember.hasOption(ObjectMember.ENUMERABLE)) {
						cachedEnumerableMemberNames.add(name);
					}
				}
			}
		}
		
		return cachedEnumerableMemberNames;
	}
	
	protected void inspectObjectFields() {
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		Map<String, Field> fields = new HashMap<String, Field>();
		
		for (Method method : objectType.getMethods()){		
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
			
		for (Field field : objectType.getFields()) {
			if (!membersResolver.isField(field)) {
				continue;
			}
			
			String fieldName = membersResolver.extractFieldName(field);
			fields.put(fieldName, field);
		}
		
		for (Map.Entry<String, Method> getterEntry : getters.entrySet()) {
			String fieldName = getterEntry.getKey();
			Method objectFieldGetter = getterEntry.getValue();
			Method objectFieldSetter = setters.get(fieldName);
			Field field = fields.get(fieldName);
			
			setters.remove(fieldName);
			fields.remove(fieldName);
			
			ObjectField objectField = membersResolver.constructObjectField(objectFieldGetter, objectFieldSetter, field);
			defineObjectMember(fieldName, objectField);
		}
		
		for (Map.Entry<String, Method> setterEntry : setters.entrySet()) {
			String fieldName = setterEntry.getKey();
			Method objectFieldSetter = setterEntry.getValue();
			Field field = fields.get(fieldName);
			
			fields.remove(fieldName);

			ObjectField objectField = membersResolver.constructObjectField(null, objectFieldSetter, field);
			defineObjectMember(fieldName, objectField);
		}
		
		for (Map.Entry<String, Field> setterEntry : fields.entrySet()) {
			String fieldName = setterEntry.getKey();
			Field field = setterEntry.getValue();

			ObjectField objectField = membersResolver.constructObjectField(null, null, field);
			defineObjectMember(fieldName, objectField);
		}
	}
	
	protected void inspectObjectFunctions() {
 		Method[] methods = objectType.getMethods();
 		
		for (Method method : methods) {	
			boolean isFunction = membersResolver.isFunction(method);
			if (!isFunction) {
				continue;
			}
		
			String methodName = membersResolver.extractFunctionName(method);
			ObjectFunction objectFunction = membersResolver.constructObjectFunction(method);
			
			defineObjectMember(methodName, objectFunction);
		}
	}
	
	protected void defineObjectMember(String name, ObjectMember<?> member) {
		if (!members.containsKey(name)) {
			members.put(name, new HashSet<ObjectMember<?>>());
		}
		
		Set<ObjectMember<?>> memberSet = members.get(name);
		memberSet.add(member);
	}
}

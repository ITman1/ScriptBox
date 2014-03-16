package org.fit.cssbox.scriptbox.script.javascript.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.mozilla.javascript.ScriptableObject;

public class ObjectImplementor {
	protected Object implementedObject;
	protected Class<?> implementedObjectType;
	protected BrowserScriptEngine scriptEngine;
	protected ObjectMembers objectMembers;
	protected Set<String> definedProperties;
	
	public ObjectImplementor(ObjectMembers objectMembers, BrowserScriptEngine scriptEngine) {
		this.implementedObject = objectMembers.getObject();
		this.implementedObjectType = objectMembers.getObjectType();
		this.objectMembers = objectMembers;
		this.scriptEngine = scriptEngine;
		this.definedProperties = new HashSet<String>();
	}
	
	public ObjectImplementor(Object implementedObject, BrowserScriptEngine scriptEngine) {
		this(new ObjectMembers(implementedObject), scriptEngine);
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public Set<String> getDefinedProperties() {
		return Collections.unmodifiableSet(definedProperties);
	}

	public void implementObject(ScriptableObject destinationScope) {
		for (Map.Entry<String, Set<ObjectMember<?>>> entry : objectMembers.getNamedMemberEtrySet()) {
			String memberName = entry.getKey();
			Set<ObjectMember<?>> members = entry.getValue();
			if (members.isEmpty()) {
				continue;
			}
			
			ObjectMember<?> firstMember = members.iterator().next();
			
			if (members.size() > 1) {
				for (ObjectMember<?> member : members) {
					if (member instanceof ObjectFunction) {
						defineObjectFunction(destinationScope, memberName, (ObjectFunction)member);
					} else {
						throw new FieldException("Field cannot have same name as function!");
					}
				}
			} else if (firstMember instanceof ObjectField) {
				defineObjectField(destinationScope, memberName, (ObjectField)firstMember);
			} else if (firstMember instanceof ObjectFunction) {
				defineObjectFunction(destinationScope, memberName, (ObjectFunction)firstMember);
			}
		}
	}
	
	public void removeObject(ScriptableObject destinationScope) {
		for (String property : definedProperties) {
			destinationScope.delete(property);
		}
		
		definedProperties.clear();
	}
	
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ObjectFunction objectFunction) {
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedProperties.add(methodName);
	}
	
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ObjectField objectField) {
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedProperties.add(fieldName);
	}
		

}

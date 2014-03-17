package org.fit.cssbox.scriptbox.script.javascript.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembers;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ObjectMembers;
import org.mozilla.javascript.ScriptableObject;

public class ObjectImplementor {
	protected Object implementedObject;
	protected Class<?> implementedObjectType;
	protected BrowserScriptEngine scriptEngine;
	protected ObjectMembers objectMembers;
	protected Set<String> definedProperties;
	
	public ObjectImplementor(ObjectMembers objectMembers, BrowserScriptEngine scriptEngine) {
		this.objectMembers = objectMembers;
		this.implementedObject = objectMembers.getObject();
		this.implementedObjectType = this.implementedObject.getClass();
		this.scriptEngine = scriptEngine;
		this.definedProperties = new HashSet<String>();
	}
	
	public ObjectImplementor(Object implementedObject, BrowserScriptEngine scriptEngine) {
		this(ObjectMembers.getObjectMembers(implementedObject), scriptEngine);
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public ClassMembers getObjectMembers() {
		return objectMembers;
	}
	
	public Set<String> getDefinedProperties() {
		return Collections.unmodifiableSet(definedProperties);
	}
	
	public Set<String> getEnumerableProperties() {
		return objectMembers.getEnumerableMemberNames();
	}

	public void implementObject(ScriptableObject destinationScope) {
		for (Map.Entry<String, Set<ClassMember<?>>> entry : objectMembers.getNamedMemberEtrySet()) {
			String memberName = entry.getKey();
			Set<ClassMember<?>> members = entry.getValue();
			if (members.isEmpty()) {
				continue;
			}
			
			ClassMember<?> firstMember = members.iterator().next();
			
			if (members.size() > 1) {
				for (ClassMember<?> member : members) {
					if (member instanceof ClassFunction) {
						defineObjectFunction(destinationScope, memberName, (ClassFunction)member);
					} else {
						throw new FieldException("Field cannot have same name as function!");
					}
				}
			} else if (firstMember instanceof ClassField) {
				defineObjectField(destinationScope, memberName, (ClassField)firstMember);
			} else if (firstMember instanceof ClassFunction) {
				defineObjectFunction(destinationScope, memberName, (ClassFunction)firstMember);
			}
		}
	}
	
	public void removeObject(ScriptableObject destinationScope) {
		for (String property : definedProperties) {
			destinationScope.delete(property);
		}
		
		definedProperties.clear();
	}
	
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ClassFunction classFunction) {
		ObjectFunction objectFunction = new ObjectFunction(implementedObject, classFunction);
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedProperties.add(methodName);
	}
	
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ClassField classField) {
		ObjectField objectField = new ObjectField(implementedObject, classField);
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedProperties.add(fieldName);
	}
		

}

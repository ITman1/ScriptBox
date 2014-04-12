/**
 * ObjectImplementor.java
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

package org.fit.cssbox.scriptbox.script.javascript.java;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;
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
	
	public ObjectImplementor(ObjectMembers objectMembers, JavaScriptEngine scriptEngine) {
		this.objectMembers = objectMembers;
		this.implementedObject = objectMembers.getObject();
		this.implementedObjectType = this.implementedObject.getClass();
		this.scriptEngine = scriptEngine;
		this.definedProperties = new HashSet<String>();
	}
	
	public ObjectImplementor(Object implementedObject, JavaScriptEngine scriptEngine) {
		this(getObjectMembers(implementedObject, scriptEngine), scriptEngine);
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public ObjectMembers getObjectMembers() {
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
		
	protected static ObjectMembers getObjectMembers(Object object, JavaScriptEngine engine) {
		ClassMembersResolverFactory resolverFactory = engine.getClassMembersResolverFactory();
		ObjectMembers objectMembers = ObjectMembers.getObjectMembers(object, resolverFactory);
		return objectMembers;
	}
}

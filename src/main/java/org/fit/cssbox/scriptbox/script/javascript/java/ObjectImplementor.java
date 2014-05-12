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
import org.fit.cssbox.scriptbox.script.exceptions.FieldException;
import org.fit.cssbox.scriptbox.script.reflect.ClassField;
import org.fit.cssbox.scriptbox.script.reflect.ClassFunction;
import org.fit.cssbox.scriptbox.script.reflect.ClassMember;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.ObjectFunction;
import org.fit.cssbox.scriptbox.script.reflect.DefaultObjectMembers;
import org.mozilla.javascript.ScriptableObject;

/**
 * Represents class that enables implementing the specific 
 * Java native object into ScriptableObject.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ObjectImplementor {
	protected Object implementedObject;
	protected Class<?> implementedObjectType;
	protected DefaultObjectMembers objectMembers;
	protected Set<String> definedProperties;
	
	/**
	 * Constructs object implementor for the passed object members.
	 * 
	 * @param objectMembers Object members that should be implemented.
	 * @param scriptEngine Script engine that owns this implementor.
	 */
	public ObjectImplementor(DefaultObjectMembers objectMembers, BrowserScriptEngine scriptEngine) {
		this.objectMembers = objectMembers;
		this.implementedObject = objectMembers.getObject();
		this.implementedObjectType = this.implementedObject.getClass();
		this.definedProperties = new HashSet<String>();
	}
	
	/**
	 * Constructs object implementor for the passed object.
	 * 
	 * @param implementedObject Object that should be implemented by this implementor.
	 * @param scriptEngine Script engine that owns this implementor.
	 */
	public ObjectImplementor(Object implementedObject, BrowserScriptEngine scriptEngine) {
		this(getObjectMembers(implementedObject, scriptEngine), scriptEngine);
	}
	
	/**
	 * Returns implemented object.
	 * 
	 * @return Implemented object.
	 */
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	/**
	 * Returns members of the implemented object.
	 * 
	 * @return Members of the implemented object.
	 */
	public DefaultObjectMembers getObjectMembers() {
		return objectMembers;
	}
	
	/**
	 * Returns all defined properties.
	 * 
	 * @return All defined properties by this implementor.
	 */
	public Set<String> getDefinedProperties() {
		return Collections.unmodifiableSet(definedProperties);
	}
	
	/**
	 * Returns all enumerable properties defined by this implementor.
	 * 
	 * @return All enumerable properties defined by this implementor.
	 */
	public Set<String> getEnumerableProperties() {
		return objectMembers.getEnumerableMemberNames();
	}

	/**
	 * Implements object into passed scope.
	 * 
	 * @param destinationScope Scope where should be implemented the wrapped object.
	 */
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
	
	/**
	 * Un-implements the wrapped object from the passed scope.
	 * 
	 * @param destinationScope Scope where should be removed the implemented object.
	 */
	public void removeObject(ScriptableObject destinationScope) {
		for (String property : definedProperties) {
			destinationScope.delete(property);
		}
		
		definedProperties.clear();
	}
	
	/**
	 * Defines new function in the passed scope.
	 * 
	 * @param destinationScope Scope where to put the function.
	 * @param methodName Name of the function.
	 * @param classFunction Function member wrapper.
	 */
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ClassFunction classFunction) {
		ObjectFunction objectFunction = new ObjectFunction(implementedObject, classFunction);
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedProperties.add(methodName);
	}
	
	/**
	 * Defines new field in the passed scope.
	 * 
	 * @param destinationScope Scope where to put the field.
	 * @param fieldName Name of the field.
	 * @param classField Field member wrapper.
	 */
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ClassField classField) {
		ObjectJSField objectField = new ObjectJSField(implementedObject, classField);
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedProperties.add(fieldName);
	}
		
	/**
	 * Returns object members for the passed object.
	 * 
	 * @param object Object of which object members should be resolved.
	 * @param engine Script engine that should be used for the resolving.
	 * @return Resolved object members.
	 */
	protected static DefaultObjectMembers getObjectMembers(Object object, BrowserScriptEngine engine) {
		ClassMembersResolverFactory resolverFactory = engine.getClassMembersResolverFactory();
		DefaultObjectMembers objectMembers = DefaultObjectMembers.getObjectMembers(object, resolverFactory);
		return objectMembers;
	}
}

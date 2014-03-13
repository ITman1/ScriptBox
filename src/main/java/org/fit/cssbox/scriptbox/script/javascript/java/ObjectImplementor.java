package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.FieldException;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.ScriptableObject;

public class ObjectImplementor {
	protected Object implementedObject;
	protected Class<?> implementedObjectType;
	protected BrowserScriptEngine scriptEngine;
	protected Set<String> definedFieldProperties;
	protected Set<String> definedFunctionProperties;
	
	public ObjectImplementor(Object implementedObject, BrowserScriptEngine scriptEngine) {
		this.implementedObject = implementedObject;
		this.implementedObjectType = implementedObject.getClass();
		this.scriptEngine = scriptEngine;
		this.definedFieldProperties = new HashSet<String>();
		this.definedFunctionProperties = new HashSet<String>();
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public Set<String> getDefinedFieldProperties() {
		return Collections.unmodifiableSet(definedFieldProperties);
	}
	
	public Set<String> getDefinedFunctionProperties() {
		return Collections.unmodifiableSet(definedFunctionProperties);
	}
	
	public void implementObject(ScriptableObject destinationScope) {
		defineObjectProperties(destinationScope);
		defineObjectFunctions(destinationScope);
	}
	
	public void removeObject(ScriptableObject destinationScope) {
		for (String property : definedFieldProperties) {
			destinationScope.delete(property);
		}
		
		for (String property : definedFunctionProperties) {
			destinationScope.delete(property);
		}
		
		definedFieldProperties.clear();
		definedFunctionProperties.clear();
	}
	
	protected void defineObjectFunctions(ScriptableObject destinationScope) {
 		Class<?> objectClass = implementedObject.getClass();
 		Method[] methods = objectClass.getMethods();
 		
		for (Method method : methods) {	
			boolean isFunction = isFunction(method);
			if (!isFunction) {
				continue;
			}
		
			String methodName = extractFunctionName(method);
			
			ObjectFunction objectFunction = new ObjectFunction(implementedObject, method);
			defineObjectFunction(destinationScope, methodName, objectFunction);
		}
	}
	
	protected void defineObjectProperties(ScriptableObject destinationScope) {
 		Class<?> objectClass = implementedObject.getClass();
 		
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		for (Method method : objectClass.getMethods()){		
			boolean isGetter = false;
			boolean isSetter = false;
			
			if (isGetter = isGetter(method)) {
			} else if (isSetter = isSetter(method)) {
			} else {
				continue;
			}
			
			if (isGetter) {
				String fieldName = extractFieldNameFromGetter(method);
				getters.put(fieldName, method);
			} else if (isSetter) {
				String fieldName = extractFieldNameFromSetter(method);
				setters.put(fieldName, method);
			}
		}
		
		for (Map.Entry<String, Method> getterEntry : getters.entrySet()) {
			String fieldName = getterEntry.getKey();
			Method objectFieldGetter = getterEntry.getValue();
			Method objectFieldSetter = setters.get(fieldName);
			
			setters.remove(fieldName);
			
			ObjectField objectField = new ObjectField(implementedObject, objectFieldGetter, objectFieldSetter);
			defineObjectField(destinationScope, fieldName, objectField);
		}
		
		for (Map.Entry<String, Method> setterEntry : setters.entrySet()) {
			String fieldName = setterEntry.getKey();
			Method objectFieldSetter = setterEntry.getValue();

			ObjectField objectField = new ObjectField(implementedObject, null, objectFieldSetter);
			defineObjectField(destinationScope, fieldName, objectField);
		}
		
		Field[] fields = objectClass.getFields();
		
		for (Field field : fields) {
			if (!isProperty(field)) {
				continue;
			}
			String fieldName = field.getName();
			
			if (!destinationScope.has(fieldName, destinationScope)) {
				ObjectField objectField = new ObjectField(implementedObject, field);
				defineObjectField(destinationScope, fieldName, objectField);
			}
		}
	}
	
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ObjectFunction objectFunction) {
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedFunctionProperties.add(methodName);
	}
	
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ObjectField objectField) {
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedFieldProperties.add(fieldName);
	}
	
	protected boolean isObjectGetter(Method method) {
		return ObjectFunction.isObjectGetterMethod(implementedObjectType, method);
	}
	
	protected boolean isGetter(Method method) {
		return ObjectField.isGetter(method);
	}
	
	protected boolean isSetter(Method method) {
		return ObjectField.isSetter(method);
	}
	
	protected boolean isFunction(Method method) {
		Method toStringMethod = null;
		try {
			toStringMethod = implementedObject.getClass().getMethod("toString");
		} catch (Exception e) {
		}
		return !method.equals(toStringMethod) && !isGetter(method) && !isSetter(method) && !isObjectGetter(method);
	}
	
	protected boolean isProperty(Field field) {
		return true;
	}
	
	protected String extractFieldNameFromGetter(Method method) {
		return ObjectField.extractFieldNameFromGetter(method);
	}
	
	protected String extractFieldNameFromSetter(Method method) {
		return ObjectField.extractFieldNameFromSetter(method);
	}
	
	protected String extractFunctionName(Method method) {
		return method.getName();
	}
}

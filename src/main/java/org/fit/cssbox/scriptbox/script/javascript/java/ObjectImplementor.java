package org.fit.cssbox.scriptbox.script.javascript.java;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
	protected Set<String> definedProperties;
	
	public ObjectImplementor(Object implementedObject, BrowserScriptEngine scriptEngine) {
		this.implementedObject = implementedObject;
		this.implementedObjectType = implementedObject.getClass();
		this.scriptEngine = scriptEngine;
		this.definedProperties = new HashSet<String>();
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public Set<String> getDefinedProperties() {
		return Collections.unmodifiableSet(definedProperties);
	}

	public void implementObject(ScriptableObject destinationScope) {
		defineObjectProperties(destinationScope);
		defineObjectFunctions(destinationScope);
	}
	
	public void removeObject(ScriptableObject destinationScope) {
		for (String property : definedProperties) {
			destinationScope.delete(property);
		}
		
		definedProperties.clear();
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
			
			ObjectFunction objectFunction = constructObjectFunction(method);
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
			
			ObjectField objectField = constructObjectField(objectFieldGetter, objectFieldSetter, null);
			defineObjectField(destinationScope, fieldName, objectField);
		}
		
		for (Map.Entry<String, Method> setterEntry : setters.entrySet()) {
			String fieldName = setterEntry.getKey();
			Method objectFieldSetter = setterEntry.getValue();

			ObjectField objectField = constructObjectField(null, objectFieldSetter, null);
			defineObjectField(destinationScope, fieldName, objectField);
		}
		
		Field[] fields = objectClass.getFields();
		
		for (Field field : fields) {
			if (!isProperty(field)) {
				continue;
			}
			String fieldName = field.getName();
			
			if (!destinationScope.has(fieldName, destinationScope)) {
				ObjectField objectField = constructObjectField(null, null, field);
				defineObjectField(destinationScope, fieldName, objectField);
			}
		}
	}
	
	protected ObjectFunction constructObjectFunction(Method method) {
		return new ObjectFunction(implementedObject, method);
	}
	
	protected ObjectField constructObjectField(Method objectFieldGetter, Method objectFieldSetter, Field objectField) {
		Class<?> objectClass = implementedObject.getClass();
		String fieldName = null;
		
		if (objectFieldGetter != null) {
			fieldName = extractFieldNameFromGetter(objectFieldGetter);
		} else if (objectFieldSetter != null) {
			fieldName = extractFieldNameFromSetter(objectFieldGetter);
		} else {
			fieldName = objectField.getName();
		}
		
		if (objectField == null) {
			try {
				objectField = objectClass.getField(fieldName);
			} catch (Exception e) {}
		}
		
		return new ObjectField(implementedObject, objectFieldGetter, objectFieldSetter, objectField);
	}
	
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ObjectFunction objectFunction) {
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedProperties.add(methodName);
	}
	
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ObjectField objectField) {
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedProperties.add(fieldName);
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
		return !isObjectGetter(method);// && !isGetter(method) && !isSetter(method);
	}
	
	protected boolean isProperty(Field field) {
		return !Modifier.isStatic(field.getModifiers());
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

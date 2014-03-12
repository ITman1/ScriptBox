package org.fit.cssbox.scriptbox.script.javascript.object;

import java.lang.reflect.Method;
import java.util.HashMap;
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
 		
		for (Method method : objectClass.getMethods()) {	
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
	}
	
	protected void defineObjectFunction(ScriptableObject destinationScope, String methodName, ObjectFunction objectFunction) {
		ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
		definedProperties.add(methodName);
	}
	
	protected void defineObjectField(ScriptableObject destinationScope, String fieldName, ObjectField objectField) {
		ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
		definedProperties.add(fieldName);
	}
	
	protected boolean isGetter(Method method) {
		String getterName =  method.getName();
		return getterName.startsWith("get");
	}
	
	protected boolean isSetter(Method method) {
		String setterName =  method.getName();
		return setterName.startsWith("set");
	}
	
	protected boolean isFunction(Method method) {
		Method toStringMethod = null;
		try {
			toStringMethod = implementedObject.getClass().getMethod("toString");
		} catch (Exception e) {
		}
		return !method.equals(toStringMethod) && !isGetter(method) && !isSetter(method);
	}
	
	protected String extractFieldNameFromGetter(Method method) {
		String getterName =  method.getName();
		Character fourthCharacter = getterName.charAt(3);
		if (getterName.startsWith("get") && getterName.length() > 3 && Character.isUpperCase(fourthCharacter)) {
			return (fourthCharacter + "").toLowerCase() + getterName.substring(4);
		} else {
			throw new FieldException("Invalid getter name! Getter method should start with 'get' and next character should be upper case!");
		}
	}
	
	protected String extractFieldNameFromSetter(Method method) {
		String setterName =  method.getName();
		Character fourthCharacter = setterName.charAt(3);
		if (setterName.startsWith("set") && setterName.length() > 3 && Character.isUpperCase(fourthCharacter)) {
			return (fourthCharacter + "").toLowerCase() + setterName.substring(4);
		} else {
			throw new FieldException("Invalid setter name! Getter method should start with 'set' and next character should be upper case!");
		}
	}
	
	protected String extractFunctionName(Method method) {
		return method.getName();
	}
}

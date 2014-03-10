package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.fit.cssbox.scriptbox.script.ScriptSetter;
import org.mozilla.javascript.ScriptableObject;

public class ObjectImplementor {
	private Object implementedObject;
	private ScriptableObject destinationScope;
	private BrowserScriptEngine browserScriptEngine;
	private Set<String> definedProperties;
	
	public ObjectImplementor(Object implementedObject, ScriptableObject destinationScope, BrowserScriptEngine browserScriptEngine) {
		this.implementedObject = implementedObject;
		this.destinationScope = destinationScope;
		this.browserScriptEngine = browserScriptEngine;
		this.definedProperties = new HashSet<String>();
		
		implementObject();
	}
	
	public Object getImplementedObject() {
		return implementedObject;
	}
	
	public ScriptableObject getDestinationScope() {
		return destinationScope;
	}
	
	public void implementObject() {
		defineObjectProperties();
		defineObjectFunctions();
	}
	
	public void removeObject() {
		for (String property : definedProperties) {
			destinationScope.delete(property);
		}
		definedProperties.clear();
	}
	
	private void defineObjectFunctions() {
 		Class<?> objectClass = implementedObject.getClass();
 		
		for (Method method : objectClass.getMethods()) {	
			Annotation functionAnnotation = method.getAnnotation(ScriptFunction.class);   
			
			if (functionAnnotation != null) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(functionAnnotation, browserScriptEngine);
				if (!isSupported) {
					continue;
				}
				
				String methodName = method.getName();
				ObjectFunction objectFunction = new ObjectFunction(implementedObject, method);
				
				ObjectScriptable.defineObjectFunction(destinationScope, methodName, objectFunction);
				definedProperties.add(methodName);
			}
		}
	}
	
	private void defineObjectProperties() {
 		Class<?> objectClass = implementedObject.getClass();
 		
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		for (Method method : objectClass.getMethods()){		
			Annotation annotation = null;
			if ((annotation = method.getAnnotation(ScriptGetter.class)) != null) {
			} else if ((annotation = method.getAnnotation(ScriptSetter.class)) != null) {
			}
			
			if (annotation != null) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(annotation, browserScriptEngine);
				if (!isSupported) {
					continue;
				}
				String methodName = method.getName();
				if (annotation instanceof ScriptGetter) {
					String fieldName = ScriptAnnotation.getFieldFromGetterName(methodName);
					getters.put(fieldName, method);
				} else if (annotation instanceof ScriptSetter) {
					String fieldName = ScriptAnnotation.getFieldFromSetterName(methodName);
					setters.put(fieldName, method);
				}
			}
		}
		
		for (Map.Entry<String, Method> getterEntry : getters.entrySet()) {
			String fieldName = getterEntry.getKey();
			Method objectFieldGetter = getterEntry.getValue();
			Method objectFieldSetter = setters.get(fieldName);
			
			setters.remove(fieldName);
			ObjectField objectField = new ObjectField(implementedObject, objectFieldGetter, objectFieldSetter);
			ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
			definedProperties.add(fieldName);
		}
		
		for (Map.Entry<String, Method> setterEntry : setters.entrySet()) {
			String fieldName = setterEntry.getKey();
			Method objectFieldSetter = setterEntry.getValue();

			ObjectField objectField = new ObjectField(implementedObject, null, objectFieldSetter);
			ObjectScriptable.defineObjectField(destinationScope, fieldName, objectField);
			definedProperties.add(fieldName);
		}
	}
}

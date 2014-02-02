package org.fit.cssbox.scriptbox.script;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.fit.cssbox.scriptbox.script.exceptions.DocumentScriptException;

public class DocumentScriptEngineManager {
	static private DocumentScriptEngineManager instance;
	
	private Map<String, Set<DocumentScriptEngineFactory>> engineMimeTypes;

	private Object runnerClass;
	
	private DocumentScriptEngineManager() {
		engineMimeTypes = new HashMap<String, Set<DocumentScriptEngineFactory>>();
	}
	
	public static synchronized DocumentScriptEngineManager getInstance() {
		if (instance == null) {
			instance = new DocumentScriptEngineManager();
		}
		
		return instance;
	}
	
	public void registerDocumentScriptEngine(Class<? extends DocumentScriptEngineFactory> factoryClass) throws DocumentScriptException {	
		synchronized (this) {

			DocumentScriptEngineFactory factory = instantizeFactory(factoryClass);
			List<String> mimeTypes = factory.getMimeTypes();
			
			for (String mimeType : mimeTypes) {
				Set<DocumentScriptEngineFactory> factories = engineMimeTypes.get(mimeType);
				
				if (factories == null) {
					factories = new HashSet<DocumentScriptEngineFactory>();
				}
				
				factories.add(factory);
				engineMimeTypes.put(mimeType, factories);		
			}
			
		}
	}
	
	public void unregisterDocumentScriptEngine(Class<? extends DocumentScriptEngineFactory> runnerClass) {
		synchronized (this) {
			Set<Entry<String, Set<DocumentScriptEngineFactory>>> factoriesEntrySet = engineMimeTypes.entrySet();
			
			List<String> mimeTypesToRemove = new ArrayList<String>();
			for (Entry<String, Set<DocumentScriptEngineFactory>> entry : factoriesEntrySet) {
				Set<DocumentScriptEngineFactory> factories = entry.getValue();
				DocumentScriptEngineFactory factoryToRemove = null;
				
				for (DocumentScriptEngineFactory factory : factories) {
					if (factory.getClass().equals(runnerClass)) {
						factoryToRemove = factory;
					}
				}
				
				if (factories.size() == 1 && factoryToRemove != null) {
					mimeTypesToRemove.add(entry.getKey());
				}
				
				factories.remove(factoryToRemove);
			}
		}
	}
	
	public Set<DocumentScriptEngineFactory> getEnginefactoriesByMimeType(String mimeType) {
		Set<DocumentScriptEngineFactory> factories = engineMimeTypes.get(mimeType);
		
		return (factories == null)? null : Collections.unmodifiableSet(factories);
	}
	
	public DocumentScriptEngineFactory getEngineByMimeType(String mimeType) {
		Set<DocumentScriptEngineFactory> factories = engineMimeTypes.get(mimeType);
		
		return (factories == null)? null : factories.iterator().next();
	}
	
	public boolean isSupported(String mimeType) {
		return engineMimeTypes.containsKey(mimeType);
	}
	
	private DocumentScriptEngineFactory instantizeFactory(Class<? extends DocumentScriptEngineFactory> factoryClass) throws DocumentScriptException {
		DocumentScriptEngineFactory factory = null;
		
		try {
			factory = factoryClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new DocumentScriptException("Unable to instantize runner from engine runner class!", e.getCause());
		}
		
		return factory;
	}
}

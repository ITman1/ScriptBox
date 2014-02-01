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
import org.w3c.dom.Document;

public class DocumentScriptEngineManager {
	static private DocumentScriptEngineManager instance;
	
	private Map<String, Set<DocumentScriptRunner>> engineMimeTypes;
	
	private DocumentScriptEngineManager() {}
	
	public static synchronized DocumentScriptEngineManager getInstance() {
		if (instance == null) {
			instance = new DocumentScriptEngineManager();
			instance.engineMimeTypes = new HashMap<String, Set<DocumentScriptRunner>>();
		}
		
		return instance;
	}
	
	public void registerDocumentScriptEngine(Class<? extends DocumentScriptRunner> runnerClass) throws DocumentScriptException {	
		synchronized (this) {

			DocumentScriptRunner runner = instantizeFactory(runnerClass);
			List<String> mimeTypes = runner.getMimeTypes();
			
			for (String mimeType : mimeTypes) {
				Set<DocumentScriptRunner> runners = engineMimeTypes.get(mimeType);
				
				if (runners == null) {
					runners = new HashSet<DocumentScriptRunner>();
				}
				
				runners.add(runner);
				engineMimeTypes.put(mimeType, runners);		
			}
			
		}
	}
	
	public void unregisterDocumentScriptEngine(Class<? extends DocumentScriptRunner> runnerClass) {
		synchronized (this) {
			Set<Entry<String, Set<DocumentScriptRunner>>> runnersEntrySet = engineMimeTypes.entrySet();
			
			List<String> mimeTypesToRemove = new ArrayList<String>();
			for (Entry<String, Set<DocumentScriptRunner>> entry : runnersEntrySet) {
				Set<DocumentScriptRunner> runners = entry.getValue();
				DocumentScriptRunner runnerToRemove = null;
				
				for (DocumentScriptRunner runner : runners) {
					if (runner.getClass().equals(runnerClass)) {
						runnerToRemove = runner;
					}
				}
				
				if (runners.size() == 1 && runnerToRemove != null) {
					mimeTypesToRemove.add(entry.getKey());
				}
				
				runners.remove(runnerToRemove);
			}
		}
	}
	
	public Set<DocumentScriptRunner> getEnginerunnersByMimeType(String mimeType) {
		Set<DocumentScriptRunner> runners = engineMimeTypes.get(mimeType);
		
		return (runners == null)? null : Collections.unmodifiableSet(runners);
	}
	
	public DocumentScriptRunner getEngineByMimeType(String mimeType, Document document) {
		Set<DocumentScriptRunner> runners = engineMimeTypes.get(mimeType);
		
		return (runners == null)? null : runners.iterator().next();
	}
	
	private DocumentScriptRunner instantizeFactory(Class<? extends DocumentScriptRunner> runnerClass) throws DocumentScriptException {
		DocumentScriptRunner runner = null;
		
		try {
			runnerClass.getConstructor().newInstance();
		} catch (Exception e) {
			throw new DocumentScriptException("Unable to instantize runner from engine runner class!", e.getCause());
		}
		
		return runner;
	}
}

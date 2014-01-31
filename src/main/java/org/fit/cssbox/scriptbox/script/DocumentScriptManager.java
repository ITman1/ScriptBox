package org.fit.cssbox.scriptbox.script;

public class DocumentScriptManager {
	static private DocumentScriptManager instance;
	
	private DocumentScriptManager() {}
	
	public static DocumentScriptManager getInstance() {
		if (instance == null) {
			instance = new DocumentScriptManager();
		}
		
		return instance;
	}
}

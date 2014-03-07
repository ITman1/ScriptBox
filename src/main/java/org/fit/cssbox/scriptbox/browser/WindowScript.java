package org.fit.cssbox.scriptbox.browser;

import java.io.Reader;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.dom.Html5ScriptElementImpl;
import org.fit.cssbox.scriptbox.script.MimeScript;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.w3c.dom.Document;

public class WindowScript extends MimeScript {
	private Html5ScriptElementImpl scriptElement;
	
	public WindowScript(WindowScriptSettings windowSettings, Html5ScriptElementImpl scriptElement) {
		this.scriptElement = scriptElement;
		Document document = scriptElement.getOwnerDocument();
		
		if (document instanceof Html5DocumentImpl) {
			Html5DocumentImpl documentImpl = (Html5DocumentImpl)document;
			Window window = documentImpl.getWindow();
			scriptSettings = window.getScriptSettings();
		} else {
			// TODO: Throw exception?
		}
	}
	
	@Override
	protected boolean prepareRunCallback(ScriptSettings settings) {
		Object globalObject = scriptSettings.getGlobalObject();
		if (globalObject instanceof Window) {
			Window window = (Window)globalObject;
			Html5DocumentImpl windowDocument = window.getDocumentImpl();
			if (!windowDocument.isFullyActive()) {
				return false;
			}
		}
		return super.prepareRunCallback(settings);
	}
	
	@Override
	public String getMimeType() {
		return scriptElement.getMimeType();
	}

	@Override
	protected Reader getCodeEntryPoint() {
		return scriptElement.getExecutableScript();
	}
}

package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptSettings;

public class SecureRhinoScriptEngineFactory extends BrowserScriptEngineFactory {

    private static List<String> mimeTypes;
	
    static {       
        /* http://tools.ietf.org/html/rfc4329#page-9 */
        mimeTypes = new ArrayList<String>(4);
        mimeTypes.add("application/javascript");
        mimeTypes.add("application/ecmascript");
        mimeTypes.add("text/javascript");
        mimeTypes.add("text/ecmascript");
        mimeTypes = Collections.unmodifiableList(mimeTypes);
    }
	
	@Override
	public List<String> getExplicitlySupportedMimeTypes() {
		return mimeTypes;
	}

	@Override
	public BrowserScriptEngine getBrowserScriptEngine(ScriptSettings scriptSettings) {
		return new SecureRhinoScriptEngine(scriptSettings);
	}

}

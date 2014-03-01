package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngine;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngineFactory;

public class SecureRhinoScriptEngineFactory extends DocumentScriptEngineFactory {

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
	public DocumentScriptEngine getDocumentScriptEngine(Html5DocumentImpl documentContext) {
		// TODO Auto-generated method stub
		return null;
	}

}

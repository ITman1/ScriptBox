package org.fit.cssbox.scriptbox.script;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.misc.MimeContentFactoryBase;

public abstract class DocumentScriptEngineFactory extends MimeContentFactoryBase<DocumentScriptEngine> {
	public abstract DocumentScriptEngine getDocumentScriptEngine(Html5DocumentImpl documentContext);
	
	@Override
	public DocumentScriptEngine getContent(Object... args) {
		if (args.length == 1 && args[0] instanceof Html5DocumentImpl) {
			return getDocumentScriptEngine((Html5DocumentImpl)args[0]);
		}
		
		return null;
	}
}

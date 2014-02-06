package org.fit.cssbox.scriptbox.browser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.net.URI;

import org.fit.cssbox.scriptbox.document.script.ScriptableDocument;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngine;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/*
 * See: http://www.w3.org/html/wg/drafts/html/master/browsers.html
 */
public class BrowsingContext {
	private boolean _destroyed;
	private URI _baseURI;
	
	private BrowsingContext _parentContext;
	private Set<BrowsingContext> _childContexts;
	private WebBrowser _browser;
	private Map<Class<? extends DocumentScriptEngine>, DocumentScriptEngine> _scriptEngines;
	private WindowProxy _windowProxy;
	private SessionHistory _sessionHistory;
	
	private BrowsingContext(BrowsingContext parentContext, WebBrowser browser) {

		this._scriptEngines = new HashMap<Class<? extends DocumentScriptEngine>, DocumentScriptEngine>();
		this._childContexts = new HashSet<BrowsingContext>();
		this._parentContext = parentContext;
		this._browser = browser;
		this._sessionHistory = new SessionHistory();
		
		this._windowProxy = new WindowProxy(browser);
	}
	
	public static BrowsingContext createContext(WebBrowser browser) {
		return new BrowsingContext( null, browser);
	}
		
	public BrowsingContext createChildContext(ScriptableDocument document) {
		BrowsingContext childContext = new BrowsingContext(this, _browser);
		_childContexts.add(childContext);
		return childContext;
	}

	public void destroyContext() {
		_destroyed = true;
	}
	
	/*
	 * At any time, one Document in each browsing context is designated the active document. 
	 */
	public ScriptableDocument getActiveDocument() {
		if (_destroyed) {
			return null;
		} else {
			return _sessionHistory.getActiveDocument();
		}
	}
	
	/*
	 * If a browsing context has a parent browsing context, 
	 * then that is its creator browsing context. 
	 */
	public BrowsingContext getCreatorContext() {
		return _parentContext;
	}
	
	/*
	 * If a browsing context A has a creator browsing context, then the Document 
	 * that was the active document of that creator browsing context at the 
	 * time A was created is the creator Document.
	 */
	public ScriptableDocument getCreatorDocument() {
		if (_parentContext != null) {
			return _parentContext.getActiveDocument();
		} else {
			return null;
		}
	}
	
	public Collection<BrowsingContext> getChildrenContexts() {
		return _childContexts;
	}
	
	/*
	 * The transitive closure of parent browsing contexts for a nested 
	 * browsing context gives the list of ancestor browsing contexts
	 * The list of the descendant browsing contexts of a Document 
	 */
	public Collection<BrowsingContext> getAllDescendantContexts() {
		List<BrowsingContext> contextList = new ArrayList<BrowsingContext>();
		
		for (BrowsingContext childContext : _childContexts) {
			contextList.add(childContext);
			contextList.addAll(childContext.getChildrenContexts());
		}
		
		return contextList;
	}
	
	public void addDocumentScriptEngine(DocumentScriptEngine scriptEngine) {
		_scriptEngines.put(scriptEngine.getClass(), scriptEngine);
	}
	
	public void getDocumentScriptEngine(Class<? extends DocumentScriptEngine> engineClass) {
		_scriptEngines.get(engineClass);
	}
	
	public URI getBaseURI() {
		return _baseURI;
	}
	
	public void setBaseURI(URI baseURI) {
		this._baseURI = baseURI;
	}
	
	/*
	 * Scripting is enabled in a browsing context 
	 * when all of the following conditions are true
	 */
	public boolean scriptingEnabled() {
		boolean isEnabled = true;
		
		isEnabled = isEnabled && _browser.scriptsSupported();
		isEnabled = isEnabled && _browser.scriptsEnabled(getBaseURI());
		
		// TODO: The browsing context's active document's active sandboxing 
		// flag set does not have its sandboxed scripts browsing context flag set.
		
		return isEnabled;
	}
	
	/*
	 * Scripting is enabled for a node if the Document object of the node 
	 * (the node itself, if it is itself a Document object) has 
	 * an associated browsing context, and scripting is enabled in that browsing context.
	 */
	public static boolean scriptingEnabled(Node node) {
		boolean isEnabled = true;
		
		Document document = node.getOwnerDocument();
		
		if (document instanceof ScriptableDocument) {
			ScriptableDocument scriptableDocument = (ScriptableDocument)document;
			BrowsingContext context = scriptableDocument.getBrowsingContext();
			
			isEnabled = isEnabled && context != null;
			isEnabled = isEnabled && context.scriptingEnabled();
		} else {
			isEnabled = false;
		}
		
		return isEnabled;
	}
	
	/*
	 * A browsing context A is said to be an ancestor of a browsing context B 
	 * if there exists a browsing context A' that is a child browsing context 
	 * of A and that is itself an ancestor of B, or if the browsing context A 
	 * is the parent browsing context of B.
	 */
	public boolean isAncestorOf(BrowsingContext childContext) {
		BrowsingContext ancestorContext = childContext;
		
		while (ancestorContext.getCreatorContext() != null) {
			ancestorContext = ancestorContext.getCreatorContext();
			
			if (ancestorContext == this) {
				return true;
			}
		}
		
		return false;
	}
	
	/*
	 * A browsing context that is not a nested browsing context has no parent browsing context, 
	 * and is the top-level browsing context of all the browsing contexts for which it 
	 * is an ancestor browsing context.
	 */
	public boolean isTopLevelBrowsingContext() {
		return _parentContext == null;
	}

}

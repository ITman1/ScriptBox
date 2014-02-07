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
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngine;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/*
 * See: http://www.w3.org/html/wg/drafts/html/master/browsers.html
 * TODO: A nested browsing context can be put into a delaying load events mode. This is used when 
 * it is navigated, to delay the load event of the browsing context container before the new Document is created.
 */
public class BrowsingContext {
	private boolean _destroyed;
	private URI _baseURI;
	
	private BrowsingContext _parentContext;
	private Set<BrowsingContext> _childContexts;
	private BrowsingUnit _browsingUnit;
	private Map<Class<? extends DocumentScriptEngine>, DocumentScriptEngine> _scriptEngines;
	private WindowProxy _windowProxy;
	private SessionHistory _sessionHistory;
	private BrowsingContext _openerBrowsingContext;
	private ScriptableDocument _creatorDocument;
	
	// Every top-level browsing context has a popup sandboxing flag set, 
	private Set<SandboxingFlag> _popupSandboxingFlagSet;
	
	// Every nested browsing context has an iframe sandboxing flag set
	private Set<SandboxingFlag> _iframeSandboxingFlagSet;
	
	private BrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, BrowsingContext openerBrowsingContext, boolean seamless) {
		this._parentContext = parentContext;
		this._browsingUnit = browsingUnit;
		this._openerBrowsingContext = openerBrowsingContext;
		
		this._scriptEngines = new HashMap<Class<? extends DocumentScriptEngine>, DocumentScriptEngine>();
		this._childContexts = new HashSet<BrowsingContext>();
		this._sessionHistory = new SessionHistory(this);
		this._windowProxy = new WindowProxy(this);
		
		BrowsingContext creatorContext = getCreatorContext();
		if (creatorContext != null) {
			this._creatorDocument = creatorContext.getActiveDocument();
		}
		
		if (parentContext != null) {
			this._iframeSandboxingFlagSet = new HashSet<SandboxingFlag>();
		} else {
			this._popupSandboxingFlagSet = new HashSet<SandboxingFlag>();
		}
		
		if (seamless) {
			_iframeSandboxingFlagSet.add(SandboxingFlag.SEAMLESS_IFRAMES_FLAG);
		}
	}
	
	public static BrowsingContext createTopLevelContext(BrowsingUnit browsingUnit) {
		return new BrowsingContext( null, browsingUnit, null, false);
	}
	
	public static BrowsingContext createAuxiliaryContext(BrowsingContext openerBrowsingContext) {
		return new BrowsingContext( null, openerBrowsingContext._browsingUnit, null, false);
	}
		
	public BrowsingContext createNestedContext(ScriptableDocument document) {
		return createNestedContext(document, false);
	}
	
	public BrowsingContext createNestedContext(ScriptableDocument document, boolean seamless) {
		BrowsingContext childContext = new BrowsingContext(this, _browsingUnit, null, seamless);

		
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
	 * A browsing context can have a creator browsing context, the 
	 * browsing context that was responsible for its creation. 
	 */
	public BrowsingContext getCreatorContext() {
		// If a browsing context has a parent browsing context, 
		// then that is its creator browsing context. 
		if (_parentContext != null) {
			return _parentContext;
		} 
		// if the browsing context has an opener browsing context, 
		// then that is its creator browsing context.
		else if (_openerBrowsingContext != null) {
			return _openerBrowsingContext;
		} 
		// Otherwise, the browsing context has no creator browsing context.
		else {
			return null;
		}
	}
	
	public boolean hasCreatorContext() {
		return getCreatorContext() != null;
	}
	
	public boolean hasCreatorDocument() {
		return getCreatorDocument() != null;
	}
	
	/*
	 * If a browsing context A has a creator browsing context, then the Document 
	 * that was the active document of that creator browsing context at the 
	 * time A was created is the creator Document.
	 */
	public ScriptableDocument getCreatorDocument() {
		return _creatorDocument;
	}
	
	
	/*
	 * An auxiliary browsing context has an opener browsing context, which is the browsing 
	 * context from which the auxiliary browsing context was created.
	 */
	public BrowsingContext getOpenerContext() {
		return _openerBrowsingContext;
	}
	
	public BrowsingContext getTopLevelContext() {
		BrowsingContext topLevelContext = this;
		
		while (topLevelContext.getCreatorContext() != null) {
			topLevelContext = topLevelContext.getCreatorContext();
		}
		
		return topLevelContext;
	}
		
	public Collection<BrowsingContext> getNestedContexts() {
		return _childContexts;
	}
	
	/*
	 * The transitive closure of parent browsing contexts for a nested 
	 * browsing context gives the list of ancestor browsing contexts
	 * The list of the descendant browsing contexts of a Document 
	 */
	public Collection<BrowsingContext> getDescendantContexts() {
		List<BrowsingContext> contextList = new ArrayList<BrowsingContext>();
		
		for (BrowsingContext childContext : _childContexts) {
			contextList.add(childContext);
			contextList.addAll(childContext.getNestedContexts());
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
	
	public WindowProxy getWindowProxy() {
		return _windowProxy;
	}
	
	/*
	 * Scripting is enabled in a browsing context 
	 * when all of the following conditions are true
	 */
	public boolean scriptingEnabled() {
		boolean isEnabled = true;
		
		isEnabled = isEnabled && _browsingUnit.getUserAgent().scriptsSupported();
		isEnabled = isEnabled && _browsingUnit.getUserAgent().scriptsEnabled(getBaseURI());
		
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
	public boolean isAncestorOf(BrowsingContext context) {
		BrowsingContext ancestorContext = context;
		
		while (ancestorContext.getCreatorContext() != null) {
			ancestorContext = ancestorContext.getCreatorContext();
			
			if (ancestorContext == this) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isNestedIn(BrowsingContext context) {		
		return context.isAncestorOf(this);
	}
	
	public boolean isAuxiliaryBrowsingContext() {		
		return _openerBrowsingContext != null;
	}
	
	/*
	 * A browsing context that is not a nested browsing context has no parent browsing context, 
	 * and is the top-level browsing context of all the browsing contexts for which it 
	 * is an ancestor browsing context.
	 */
	public boolean isTopLevelBrowsingContext() {
		return _parentContext == null;
	}
	
	public boolean isNestedBrowsingContext() {
		return _parentContext != null;
	}
	
	/*
	 * The document family of a browsing context consists of the union of all the Document objects in 
	 * that browsing context's session history and the document families of all those Document objects. 
	 */
	public Collection<ScriptableDocument> getDocumentFamily() {
		Set<ScriptableDocument> family = new HashSet<ScriptableDocument>();
		Collection<SessionHistoryEntry> sessionEntries = _sessionHistory.getAllEntries();
		
		for (SessionHistoryEntry entry : sessionEntries) {
			ScriptableDocument document = entry.getDocument();
			family.add(document);
			family.addAll(document.getDocumentFamily());
		}
		
		return family;
	}

	/*
	 * FIXME: Rename local variable to something more clear than a, b, c
	 */
	public boolean isFamiliarWith(BrowsingContext context) {
		BrowsingContext a = this;
		BrowsingContext b = context;
		
		DocumentOrigin aOrigin = a.getActiveDocument().getOriginContainer().getOrigin();
		DocumentOrigin bOrigin = b.getActiveDocument().getOriginContainer().getOrigin();
		
		if (aOrigin.equals(bOrigin)) {
			return true;
		}
		
		if (a.isNestedBrowsingContext() && a.getTopLevelContext() == b) {
			return true;
		}
		
		if (b.isAuxiliaryBrowsingContext() && a.isFamiliarWith(b.getOpenerContext())) {
			return true;
		}
		
		if (!b.isTopLevelBrowsingContext()) {
			BrowsingContext c = b;
			
			while (c.getCreatorContext() != null) {
				c = c.getCreatorContext();
				
				DocumentOrigin cOrigin = c.getActiveDocument().getOriginContainer().getOrigin();
				
				if (aOrigin.equals(cOrigin)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * FIXME: Rename local variable to something more clear than a, b, c
	 */
	public boolean isAllowedToNavigate(BrowsingContext context) {
		BrowsingContext a = this;
		BrowsingContext b = context;
		
		if (a != b && !a.isAncestorOf(b) && !b.isTopLevelBrowsingContext() && a.getActiveDocument().
				getActiveSandboxingFlagSet().contains(SandboxingFlag.NAVIGATION_BROWSING_CONTEXT_FLAG)) {
			return false;
		}
		
		if (b.isTopLevelBrowsingContext() && b.isAllowedToNavigate(a) && a.getActiveDocument().
				getActiveSandboxingFlagSet().contains(SandboxingFlag.TOPLEVEL_NAVIGATION_BROWSING_CONTEXT_FLAG)) {
			return false;
		}
		
		/*
		 * TODO: Otherwise, if B is a top-level browsing context, and is neither A 
		 * nor one of the ancestor browsing contexts of A, and A's Document's active 
		 * sandboxing flag set has its sandboxed navigation browsing context flag set, 
		 * and A is not the one permitted sandboxed navigator of B, then abort these steps negatively.
		 */
		//if (b.isTopLevelBrowsingContext() && a != b && !b.isAncestorOf(a) && a.getActiveDocument().)
		
		return true;
	}
	
	public Set<SandboxingFlag> getPopupSandboxingFlagSet() {
		return _popupSandboxingFlagSet;
	}
	
	public Set<SandboxingFlag> getIframeSandboxingFlagSet() {
		return _iframeSandboxingFlagSet;
	}
}

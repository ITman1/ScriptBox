package org.fit.cssbox.scriptbox.browser;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.script.DocumentScriptEngine;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * See: http://www.w3.org/html/wg/drafts/html/master/browsers.html
 * TODO: A nested browsing context can be put into a delaying load events mode. This is used when 
 * it is navigated, to delay the load event of the browsing context container before the new Document is created.
 */
public class BrowsingContext {
	public static final String BLANK_KEYWORD =  "_blank";
	public static final String SELF_KEYWORD = "_self";
	public static final String PARENT_KEYWORD = "_parent";
	public static final String TOP_KEYWORD = "_top";
	
	protected boolean destroyed;
	protected URI baseURI;
	
	protected BrowsingContext parentContext;
	protected Html5DocumentImpl creatorDocument;
	protected Set<BrowsingContext> childContexts;
	protected BrowsingUnit browsingUnit;
	protected Map<Class<? extends DocumentScriptEngine>, DocumentScriptEngine> scriptEngines;
	protected WindowProxy windowProxy;
	protected Element container;
	protected SessionHistory sessionHistory;
	protected NavigationController navigationController;
			
	protected String contextName;
	
	protected BrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, String contextName, Element container) {
		this.parentContext = parentContext;
		this.browsingUnit = browsingUnit;
		this.contextName = contextName;
		this.container = container;
		
		this.scriptEngines = new HashMap<Class<? extends DocumentScriptEngine>, DocumentScriptEngine>();
		this.childContexts = new HashSet<BrowsingContext>();
		this.sessionHistory = new SessionHistory(this);
		this.windowProxy = new WindowProxy(this);
		this.navigationController = new NavigationController(this);
		
		BrowsingContext creatorContext = getCreatorContext();
		if (creatorContext != null) {
			this.creatorDocument = creatorContext.getActiveDocument();
		}
	}
	
	public BrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, null);
	}
	
	public BrowsingContext(BrowsingUnit browsingUnit, String name) {
		this(null, browsingUnit, name, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document) {
		return createNestedContext(document, null, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, String name) {
		return createNestedContext(document, name, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, Element container) {
		return createNestedContext(document, null, container);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, String name, Element container) {
		BrowsingContext childContext = new BrowsingContext(this, null, name, container);
		childContexts.add(childContext);
		return childContext;
	}

	public void destroyContext() {
		destroyed = true;
	}
	
	/*
	 * At any time, one Document in each browsing context is designated the active document. 
	 */
	public Html5DocumentImpl getActiveDocument() {
		if (destroyed) {
			return null;
		} else {
			SessionHistoryEntry entry = sessionHistory.getCurrentEntry();
			if (entry != null) {
				return entry.getDocument();
			} else {
				return null;
			}
		}
	}
	
	public boolean hasCreatorDocument() {
		return getCreatorDocument() != null;
	}
	
	/*
	 * If a browsing context A has a creator browsing context, then the Document 
	 * that was the active document of that creator browsing context at the 
	 * time A was created is the creator Document.
	 */
	public Html5DocumentImpl getCreatorDocument() {
		return creatorDocument;
	}
	
	/*
	 * A browsing context can have a creator browsing context, the 
	 * browsing context that was responsible for its creation. 
	 */
	public BrowsingContext getCreatorContext() {
		// If a browsing context has a parent browsing context, 
		// then that is its creator browsing context. 
		if (parentContext != null) {
			return parentContext;
		} 
		// Otherwise, the browsing context has no creator browsing context.
		else {
			return null;
		}
	}
	
	public boolean hasCreatorContext() {
		return getCreatorContext() != null;
	}
		
	public BrowsingContext getParentContext() {
		return parentContext;
	}
	
	public boolean hasParentContext() {
		return parentContext != null;
	}
		
	public BrowsingContext getTopLevelContext() {
		BrowsingContext topLevelContext = this;
		
		while (topLevelContext.getParentContext() != null) {
			topLevelContext = topLevelContext.getParentContext();
		}
		
		return topLevelContext;
	}
		
	public Collection<BrowsingContext> getNestedContexts() {
		return childContexts;
	}
	
	/*
	 * The transitive closure of parent browsing contexts for a nested 
	 * browsing context gives the list of ancestor browsing contexts
	 * The list of the descendant browsing contexts of a Document 
	 */
	public Collection<BrowsingContext> getDescendantContexts() {
		List<BrowsingContext> contextList = new ArrayList<BrowsingContext>();
		
		for (BrowsingContext childContext : childContexts) {
			contextList.add(childContext);
			contextList.addAll(childContext.getNestedContexts());
		}
		
		return contextList;
	}
	
	public void addDocumentScriptEngine(DocumentScriptEngine scriptEngine) {
		scriptEngines.put(scriptEngine.getClass(), scriptEngine);
	}
	
	public void getDocumentScriptEngine(Class<? extends DocumentScriptEngine> engineClass) {
		scriptEngines.get(engineClass);
	}
	
	public URI getBaseURI() {
		return baseURI;
	}
	
	public void setBaseURI(URI baseURI) {
		this.baseURI = baseURI;
	}
	
	public WindowProxy getWindowProxy() {
		return windowProxy;
	}
	
	/*
	 * Scripting is enabled in a browsing context 
	 * when all of the following conditions are true
	 */
	public boolean scriptingEnabled() {
		boolean isEnabled = true;
		
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsSupported();
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsEnabled(getBaseURI());
		
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
		
		if (document instanceof Html5DocumentImpl) {
			Html5DocumentImpl scriptableDocument = (Html5DocumentImpl)document;
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
		
		while (ancestorContext.getParentContext() != null) {
			ancestorContext = ancestorContext.getParentContext();
			
			if (ancestorContext == this) {
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isNestedIn(BrowsingContext context) {		
		return context.isAncestorOf(this);
	}
	
	/*
	 * A browsing context that is not a nested browsing context has no parent browsing context, 
	 * and is the top-level browsing context of all the browsing contexts for which it 
	 * is an ancestor browsing context.
	 */
	public boolean isTopLevelBrowsingContext() {
		return parentContext == null;
	}
	
	public boolean isNestedBrowsingContext() {
		return parentContext != null;
	}
	
	/*
	 * The document family of a browsing context consists of the union of all the Document objects in 
	 * that browsing context's session history and the document families of all those Document objects. 
	 */
	public Collection<Html5DocumentImpl> getDocumentFamily() {
		Set<Html5DocumentImpl> family = new HashSet<Html5DocumentImpl>();
		Collection<SessionHistoryEntry> sessionEntries = sessionHistory.getSessionHistoryEntries();
		
		for (SessionHistoryEntry entry : sessionEntries) {
			Html5DocumentImpl document = entry.getDocument();
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
				
		if (!b.isTopLevelBrowsingContext()) {
			BrowsingContext c = b;
			
			while (c.getParentContext() != null) {
				c = c.getParentContext();
				
				DocumentOrigin cOrigin = c.getActiveDocument().getOriginContainer().getOrigin();
				
				if (aOrigin.equals(cOrigin)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	public BrowsingContext getFirstFamiliar(String name) {
		Set<BrowsingContext> contexts = browsingUnit.getUserAgent().getBrowsingContextsByName(name);
		
		for (BrowsingContext context : contexts) {
			if (isFamiliarWith(context)) {
				return context;
			}
		}
		
		return null;
	}
	
	public BrowsingContext chooseBrowsingContextByName(String name) {
		BrowsingContext context = null;
		
		if (name == null) {
			return this;
		} else if (name.isEmpty()) {
			return this;
		} else if (name.equals(SELF_KEYWORD)) {
			return this;
		} else if (name.equals(PARENT_KEYWORD)) {
			if (!hasParentContext()) {
				return this;
			} else {
				return parentContext;
			}
		} else if (name.equals(TOP_KEYWORD)) {
			return getTopLevelContext();
		} else if (!name.equals(BLANK_KEYWORD) && (context = getFirstFamiliar(name)) != null) {
			return context;
		} else {
			/*
			 * TODO: If the algorithm is not allowed to show a popup and the user agent 
			 * has been configured to not show popups (i.e. the user agent has a "popup blocker" enabled)
			 */
			Html5DocumentImpl activeDocument = getActiveDocument();
			
			if (activeDocument != null && activeDocument.getActiveSandboxingFlagSet().contains(SandboxingFlag.AUXILARY_NAVIGATION_BROWSING_CONTEXT_FLAG)) {
				// FIXME?: The user agent may offer to create a new top-level browsing context or reuse an existing top-level browsing context. 
				return null;
			}
			
			/*
			 * TODO: If the user agent has been configured such that in this instance it will 
			 * create a new browsing context, and the browsing context is being 
			 * requested as part of following a hyperlink whose link types include the noreferrer keyword
			 */
			
			/*
			 * TODO: If the user agent has been configured such that in this instance it will create a new 
			 * browsing context, and the noreferrer keyword doesn't apply
			 */
			
			/*
			 * TODO: If the user agent has been configured such that in this instance 
			 * it will reuse the current browsing context
			 */
			
			/*
			 * FIXME: Replace for null and implement above TODOs.
			 */
			return browsingUnit.getUserAgent().createBrowsingUnit().getWindowBrowsingContext();
		}
	}
	
	public String getName() {
		return contextName;
	}
	
	public void setName(String name) {
		this.contextName = name;
	}
	
	public SessionHistory getSesstionHistory() {
		return sessionHistory;
	}
	
	public BrowsingUnit getBrowsingUnit() {
		BrowsingContext ancestorContext = this;
		
		do {
			if (ancestorContext.browsingUnit != null) {
				break;
			}
			
			ancestorContext = ancestorContext.getParentContext();
		} while (ancestorContext != null);
		
		if (ancestorContext == null) {
			return null;
		} else {
			return ancestorContext.browsingUnit;
		}
	}
	
	public EventLoop getEventLoop() {
		return getBrowsingUnit().getEventLoop();
	}
	
	// FIXME: Implement.
	/*public boolean isNavigating() {
		return false;
	}*/
	
	// FIXME: Implement.
	/*public boolean unloadDocumentRunning() {
		return false;
	}*/
	
	public boolean scrollToFragment(String fragment) {
		return false;
	}
	
	public Element getContainer() {
		return container;
	}
	
	public NavigationController getNavigationController() {
		return navigationController;
	}
}

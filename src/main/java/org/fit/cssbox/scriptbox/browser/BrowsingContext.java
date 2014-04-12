/**
 * BrowsingContext.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.browser;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * See: http://www.w3.org/html/wg/drafts/html/master/browsers.html
 * TODO: A nested browsing context can be put into a delaying load events mode. This is used when 
 * it is navigated, to delay the load event of the browsing context container before the new Document is created.
 */
public class BrowsingContext {
	public static final String DEFAULT_NAME =  "";
	
	public static final String BLANK_KEYWORD =  "_blank";
	public static final String SELF_KEYWORD = "_self";
	public static final String PARENT_KEYWORD = "_parent";
	public static final String TOP_KEYWORD = "_top";
	
	protected boolean discarded;
	protected URL baseURI;
	
	protected BrowsingContext parentContext;
	protected Html5DocumentImpl creatorDocument;
	protected List<BrowsingContext> childContexts;
	protected BrowsingUnit browsingUnit;
	protected WindowProxy windowProxy;
	protected Element container;
	protected SessionHistory sessionHistory;
	protected NavigationController navigationController;
	protected Location location;
	protected History history;
			
	protected String contextName;
	
	protected BrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, String contextName, Element container) {
		this.parentContext = parentContext;
		this.browsingUnit = browsingUnit;
		this.contextName = contextName;
		this.container = container;
		
		this.childContexts = new ArrayList<BrowsingContext>();
		this.sessionHistory = new SessionHistory(this);
		this.windowProxy = new WindowProxy(this);
		this.navigationController = new NavigationController(this);
		this.location = new Location(this);
		this.history = new History(this);
		
		BrowsingContext creatorContext = getCreatorContext();
		if (creatorContext != null) {
			this.creatorDocument = creatorContext.getActiveDocument();
		}
	}
	
	public BrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, DEFAULT_NAME);
	}
		
	public BrowsingContext(BrowsingUnit browsingUnit, String name) {
		this(null, browsingUnit, name, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document) {
		return createNestedContext(document, DEFAULT_NAME, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, String name) {
		return createNestedContext(document, name, null);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, Element container) {
		return createNestedContext(document, DEFAULT_NAME, container);
	}
	
	public BrowsingContext createNestedContext(Html5DocumentImpl document, String name, Element container) {
		BrowsingContext childContext = new BrowsingContext(this, null, name, container);
		addChildContext(childContext);
		return childContext;
	}

	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#closing-browsing-contexts
	 */
	public synchronized void close() {
		Html5DocumentImpl document = getActiveDocument();
		
		if (document != null) {
			boolean unloadDocument = document.promptToUnload();
			
			if (unloadDocument) {
				document.unload(false);
			}
			
			// TODO: Here could be fired event that context has been closed and UI could react to this
			
			discard();
		}
	}
	
	/*
	 * When a browsing context is discarded, the strong reference from the user agent itself to the browsing context must be severed, 
	 * and all the Document objects for all the entries in the browsing context's session history must be discarded as well.
	 */
	public synchronized void discard() {	
		if (!discarded) {
			navigationController.cancelAllNavigationAttempts();
			sessionHistory.discard();
			
			if (parentContext != null) {
				parentContext.removeChildContext(this);
			}
			
			browsingUnit = null;
			parentContext = null;
			sessionHistory = null;
			
			discarded = true;
		}
	}
	
	public synchronized boolean isDiscarded() {
		return discarded;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#discard-a-document
	 */
	public synchronized void discardActiveDocument() {
		Html5DocumentImpl document = getActiveDocument();
		
		if (document != null) {
			document.setSalvageableFlag(false);
			document.runUnloadingDocumentCleanupSteps();
			document.abort();
			
			EventLoop eventLoop = getEventLoop();
			eventLoop.removeAllTasksWithDocument(document);
			
			Collection<BrowsingContext> childBrowsingContexts = getNestedContexts();
			for (BrowsingContext childBrowsingContext : childBrowsingContexts) {
				childBrowsingContext.discard();
			}
		}
	}
	
	/*
	 * At any time, one Document in each browsing context is designated the active document. 
	 */
	public Html5DocumentImpl getActiveDocument() {
		if (discarded) {
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
		
	public URL getBaseURL() {
		return baseURI;
	}
	
	public void setBaseURI(URL baseURI) {
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
		BrowsingUnit browsingUnit = getBrowsingUnit();
		boolean isEnabled = true;
		
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsSupported();
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsEnabled(getBaseURL());
		
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
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#familiar-with
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
		BrowsingUnit browsingUnit = getBrowsingUnit();
		Set<BrowsingContext> contexts = browsingUnit.getUserAgent().getBrowsingContextsByName(name);
		
		for (BrowsingContext context : contexts) {
			if (isFamiliarWith(context)) {
				return context;
			}
		}
		
		return null;
	}
	
	/* http://www.w3.org/html/wg/drafts/html/CR/browsers.html#valid-browsing-context-name */
	public boolean isValidBrowsingContextName(String name) {
		return !name.startsWith("_");
	}
	
	/* http://www.w3.org/html/wg/drafts/html/CR/browsers.html#valid-browsing-context-name-or-keyword */
	public boolean isValidBrowsingContextNameOrKeyword(String name) {
		return name.equalsIgnoreCase(SELF_KEYWORD) || 
				name.equalsIgnoreCase(PARENT_KEYWORD) || 
				name.equalsIgnoreCase(TOP_KEYWORD) || 
				name.equalsIgnoreCase(BLANK_KEYWORD) || 
				isValidBrowsingContextName(name);
	}
	
	public boolean isBlankBrowsingContext(String name) {
		if (name == null) {
		} else if (name.isEmpty()) {
		} else if (name.equalsIgnoreCase(SELF_KEYWORD)) {
		} else if (name.equalsIgnoreCase(PARENT_KEYWORD)) {
		} else if (name.equalsIgnoreCase(TOP_KEYWORD)) {
		} else if (!name.equalsIgnoreCase(BLANK_KEYWORD) && (getFirstFamiliar(name) != null)) {
		} else {
			return true;
		}
		
		return false;
	}
	
	/* http://www.w3.org/html/wg/drafts/html/CR/browsers.html#the-rules-for-choosing-a-browsing-context-given-a-browsing-context-name */
	public BrowsingContext chooseBrowsingContextByName(String name) {
		BrowsingUnit browsingUnit = getBrowsingUnit();
		BrowsingContext context = null;
		
		if (name == null) {
			return this;
		} else if (name.isEmpty()) {
			return this;
		} else if (name.equalsIgnoreCase(SELF_KEYWORD)) {
			return this;
		} else if (name.equalsIgnoreCase(PARENT_KEYWORD)) {
			if (!hasParentContext()) {
				return this;
			} else {
				return parentContext;
			}
		} else if (name.equalsIgnoreCase(TOP_KEYWORD)) {
			return getTopLevelContext();
		} else if (!name.equalsIgnoreCase(BLANK_KEYWORD) && (context = getFirstFamiliar(name)) != null) {
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
			return browsingUnit.getUserAgent().openBrowsingUnit().getWindowBrowsingContext();
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
		BrowsingUnit browsingUnit = getBrowsingUnit();
		return (browsingUnit != null)? browsingUnit.getEventLoop() : null;
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
		BrowsingUnit browsingUnit = getBrowsingUnit();
		UserAgent agent = browsingUnit.getUserAgent();
		ScrollBarsProp scrollbars = agent.getScrollbars();
		
		return scrollbars.scrollToFragment(fragment);
	}
	
	public Element getContainer() {
		return container;
	}
	
	public NavigationController getNavigationController() {
		return navigationController;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public History getHistory() {
		return history;
	}
	
	protected void removeChildContext(BrowsingContext child) {
		childContexts.remove(child);
	}
	
	protected void addChildContext(BrowsingContext child) {
		childContexts.add(child);
	}
}

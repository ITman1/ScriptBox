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
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.NavigationController;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.fit.cssbox.scriptbox.ui.ScrollBarsProp;
import org.fit.cssbox.scriptbox.window.WindowProxy;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Represents class of the environment in which Document is presented to user.
 * 
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#browsing-context">Browsing context</a>
 */
public class BrowsingContext {
	public static final String DEFAULT_NAME =  "";
	
	public static final String BLANK_KEYWORD =  "_blank";
	public static final String SELF_KEYWORD = "_self";
	public static final String PARENT_KEYWORD = "_parent";
	public static final String TOP_KEYWORD = "_top";
	
	protected boolean discarded;
	
	protected String contextName;
	protected BrowsingContext parentContext;
	protected Html5DocumentImpl creatorDocument;
	protected List<BrowsingContext> childContexts;
	protected BrowsingUnit browsingUnit;
	protected WindowProxy windowProxy;
	protected Element container;
	protected SessionHistory sessionHistory;
	protected NavigationController navigationController;
	protected Set<BrowsingContextListener> listeners;		
		
	/**
	 * Constructs new top-level or nested browsing context.
	 * 
	 * @param parentContext If null is passed then this browsing context will be top-level, otherwise nested.
	 * @param browsingUnit Browsing unit which collects all browsing contexts.
	 * @param contextName Name of the new browsing context.
	 * @param container Element which wraps/contains this browsing context.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#browsing-context">Browsing context</a>
	 */
	protected BrowsingContext(BrowsingContext parentContext, BrowsingUnit browsingUnit, String contextName, Element container) {
		this.parentContext = parentContext;
		this.browsingUnit = browsingUnit;
		this.contextName = contextName;
		this.container = container;
		
		this.listeners = new HashSet<BrowsingContextListener>();
		this.childContexts = new ArrayList<BrowsingContext>();
		this.sessionHistory = new SessionHistory(this);
		this.windowProxy = new WindowProxy(this);
		this.navigationController = new NavigationController(this);
		
		BrowsingContext creatorContext = getCreatorContext();
		if (creatorContext != null) {
			this.creatorDocument = creatorContext.getActiveDocument();
		}
	}
	
	/**
	 * Constructs new top-level browsing context with default name.
	 * 
	 * @param browsingUnit Browsing unit which collects all browsing contexts.
	 * @see #BrowsingContext(BrowsingContext, BrowsingUnit, String, Element)
	 */
	public BrowsingContext(BrowsingUnit browsingUnit) {
		this(browsingUnit, DEFAULT_NAME);
	}
		
	/**
	 * Constructs new top-level browsing context.
	 * 
	 * @param browsingUnit Browsing unit which collects all browsing contexts.
	 * @param contextName Name of the new browsing context.
	 * @see #BrowsingContext(BrowsingContext, BrowsingUnit, String, Element)
	 */
	public BrowsingContext(BrowsingUnit browsingUnit, String name) {
		this(null, browsingUnit, name, null);
	}
	
	/**
	 * Constructs simple nested browsing context with no container.
	 * 
	 * @param name Name of the new browsing context.
	 * @return New nested browsing context.
	 * @see #createNestedContext(String, Element)
	 */
	public BrowsingContext createNestedContext(String name) {
		return createNestedContext(name, null);
	}
	
	/**
	 * Constructs simple nested browsing context with empty name.
	 * 
	 * @param container Element which wraps/contains this browsing context.
	 * @return New nested browsing context.
	 * @see #createNestedContext(String, Element)
	 */
	public BrowsingContext createNestedContext(Element container) {
		return createNestedContext(DEFAULT_NAME, container);
	}
	
	/**
	 * Constructs simple nested browsing context.
	 * 
	 * @param name Name of the new browsing context.
	 * @param container Element which wraps/contains this browsing context.
	 * @return New nested browsing context.
	 */
	public BrowsingContext createNestedContext(String name, Element container) {
		BrowsingContext childContext = new BrowsingContext(this, null, name, container);
		addChildContext(childContext);
		return childContext;
	}

	/**
	 * Closes this browsing context.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#closing-browsing-contexts">Closing browsing contexts</a>
	 */
	public synchronized void close() {
		Html5DocumentImpl document = getActiveDocument();
		
		if (document != null) {
			boolean unloadDocument = document.promptToUnload();
			
			if (unloadDocument) {
				document.unload(false);
			}
			
			discard();
		}
	}
	
	/**
	 * Registers event listener to this browsing context.
	 * 
	 * @param listener New event listener to be registered.
	 */
	public void addListener(BrowsingContextListener listener) {
		listeners.add(listener);
	}
	
	/**
	 * Removes event listener to this browsing context.
	 * 
	 * @param listener Event listener to be removed.
	 */
	public void removeListener(BrowsingContextListener listener) {
		listeners.remove(listener);
	}
	
	/**
	 * Discards this browsing context.
	 * 
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#a-browsing-context-is-discarded">Discarded browsing context</a>
	 * @see #isDiscarded()
	 */
	public void discard() {	
		if (!discarded) {
			/*
			 * When a browsing context is discarded, the strong reference from the user agent itself to the browsing context must be severed, 
			 * and all the Document objects for all the entries in the browsing context's session history must be discarded as well.
			 */
			synchronized (this) {
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
			
			fireBrowsingContextDestroyed();
		}
	}
	
	/**
	 * Tests whether this browsing context was discarded.
	 * 
	 * @return True if this browsing context was discarded otherwise false.
	 * @see #discard()
	 */
	public synchronized boolean isDiscarded() {
		return discarded;
	}
	
	/**
	 * Discards active document of this browsing context.
	 * 
	 * @see #discardDocument(Html5DocumentImpl)
	 */
	public synchronized void discardActiveDocument() {
		Html5DocumentImpl document = getActiveDocument();
		discardDocument(document);
	}

	/**
	 * Discards the document of this browsing context.
	 * 
	 * @param document Document which should be discarded.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#discard-a-document">Discard a document</a>
	 */
	public synchronized void discardDocument(Html5DocumentImpl document) {
		if (document.getBrowsingContext() != this) {
			return;
		}
		
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
	
	/**
	 * Returns Document which is designated as the active document for this browsing context.
	 * 
	 * @return Document which is designated as the active document for this browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#active-document">Active document</a>
	 */
	public Html5DocumentImpl getActiveDocument() {
		if (discarded) {
			return null;
		} else if (sessionHistory != null) {
			SessionHistoryEntry entry = sessionHistory.getCurrentEntry();
			if (entry != null) {
				return entry.getDocument();
			}
		}
		
		return null;
	}
	
	/**
	 * Tests whether creator document exists.
	 * 
	 * @return True if there is creator document.
	 * @see #getCreatorDocument()
	 */
	public boolean hasCreatorDocument() {
		return getCreatorDocument() != null;
	}
	
	/**
	 * Returns Document that was the active document of that creator browsing context at the time when this context was created. 
	 * 
	 * @return Document that was the active document of that creator browsing context at the time when this context was created.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#creator-document">Creator document</a>
	 * @see #hasCreatorDocument()
	 */
	public Html5DocumentImpl getCreatorDocument() {
		return creatorDocument;
	}
	
	/**
	 * Returns browsing context that was responsible for creation of this browsing context. 
	 * 
	 * @return Browsing context that was responsible for creation of this browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#creator-browsing-context">Creator browsing context</a>
	 * @see #hasCreatorContext()
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
	
	/**
	 * Tests whether creator browsing context exists.
	 * 
	 * @return True if there is creator browsing context.
	 * @see #getCreatorContext()
	 */
	public boolean hasCreatorContext() {
		return getCreatorContext() != null;
	}
	
	/**
	 * Returns parent browsing context.
	 * 
	 * @return Parent browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#parent-browsing-context">Parent browsing context</a>
	 * @see #hasParentContext()
	 */
	public BrowsingContext getParentContext() {
		return parentContext;
	}
	
	/**
	 * Tests whether parent browsing context exists.
	 * 
	 * @return True if there is creator browsing context.
	 * @see #getParentContext()
	 */
	public boolean hasParentContext() {
		return parentContext != null;
	}

	/**
	 * Returns browsing context container.
	 * 
	 * @return Browsing context container.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#browsing-context-container">Browsing context container</a>
	 */
	public Element getContainer() {
		return container;
	}
	
	/**
	 * Returns top-level browsing context.
	 * 
	 * @return Top-level browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#top-level-browsing-context">Top-level browsing context</a>
	 */
	public BrowsingContext getTopLevelContext() {
		BrowsingContext topLevelContext = this;
		
		while (topLevelContext.getParentContext() != null) {
			topLevelContext = topLevelContext.getParentContext();
		}
		
		return topLevelContext;
	}
	
	/**
	 * Returns nested browsing contexts.
	 * 
	 * @return Collection of the nested browsing contexts.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#nested-browsing-context">Nested browsing contexts</a>
	 */
	public Collection<BrowsingContext> getNestedContexts() {
		return childContexts;
	}
	
	/**
	 * Returns descendant browsing contexts - the transitive closure 
	 * of parent browsing contexts for a nested browsing context.
	 * 
	 * @return Collection of descendant browsing contexts of this browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#list-of-the-descendant-browsing-contexts">List of the descendant browsing contexts </a>
	 */
	public Collection<BrowsingContext> getDescendantContexts() {
		List<BrowsingContext> contextList = new ArrayList<BrowsingContext>();
		
		for (BrowsingContext childContext : childContexts) {
			contextList.add(childContext);
			contextList.addAll(childContext.getNestedContexts());
		}
		
		return contextList;
	}
		
	/**
	 * Returns Window proxy object for this browsing context.
	 * 
	 * @return Window proxy object for this browsing context.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#windowproxy">Window proxy </a>
	 */
	public WindowProxy getWindowProxy() {
		return windowProxy;
	}
	
	/**
	 * Tests whether this browsing context is script closable.
	 * 
	 * @return True if this browsing context is script closable, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#script-closable">Script closable </a>
	 */
	public boolean isScriptClosable() {
		List<SessionHistoryEntry> entries = sessionHistory.getSessionHistoryEntries();
		
		if (!entries.isEmpty()) {
			Html5DocumentImpl document = null;
			for (SessionHistoryEntry entry : entries) {
				Html5DocumentImpl currDocument = entry.getDocument();
				if (document != null && currDocument != null && document != currDocument) {
					return false;
				}
				document = currDocument;
			}
		}
		
		return true;
	}
	
	/**
	 * Tests whether this browsing context has scripting enabled.
	 * 
	 * @return True if this browsing context has scripting enabled, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#concept-bc-script">Scripting enabled for browsing context</a>
	 */
	public boolean scriptingEnabled() {	
		Html5DocumentImpl activeDocument = getActiveDocument();
		URL baseAddress = activeDocument.getBaseAddress();
		BrowsingUnit browsingUnit = getBrowsingUnit();
		boolean isEnabled = true;
		
		/*
		 * Scripting is enabled when all of the following conditions are true
		 */
		
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsSupported();
		isEnabled = isEnabled && browsingUnit.getUserAgent().scriptsEnabled(baseAddress);
		
		// TODO: The browsing context's active document's active sandboxing 
		// flag set does not have its sandboxed scripts browsing context flag set.
		
		return isEnabled;
	}
	
	/**
	 * Tests whether passed node has scripting enabled.
	 * 
	 * @param node Node against which is test performed.
	 * @return True if passed node has scripting enabled, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#concept-n-script">Scripting enabled for a node</a>
	 */
	public static boolean scriptingEnabled(Node node) {
		boolean isEnabled = true;
		
		Document document = node.getOwnerDocument();
		
		/* 
		 * Scripting is enabled for a node if the Document object of the node 
		 * (the node itself, if it is itself a Document object) has 
		 * an associated browsing context, and scripting is enabled in that browsing context.
		 */
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
	
	/**
	 * Tests whether this browsing context is ancestor of the passed browsing context.
	 * 
	 * @param context Browsing context against which is test performed.
	 * @return True if this browsing context is ancestor of the passed browsing context, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#ancestor-browsing-context">Ancestor browsing context</a>
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
	
	/**
	 * Tests whether this browsing context is nested inside the passed browsing context.
	 * 
	 * @param context Browsing context against which is test performed.
	 * @return True if this browsing context is nested inside the passed browsing context, otherwise false.
	 * @see #isAncestorOf(BrowsingContext)
	 */
	public boolean isNestedIn(BrowsingContext context) {		
		return context.isAncestorOf(this);
	}
	
	/**
	 * Tests whether is this browsing context the top-level browsing context.
	 * 
	 * @return True if this browsing context the top-level browsing context, otherwise false.
	 * @see #getTopLevelContext()
	 */
	public boolean isTopLevelBrowsingContext() {
		return parentContext == null;
	}
	
	/**
	 * Tests whether is this browsing context the nested browsing context of this browsing context.
	 * 
	 * @return True if this browsing context the nested browsing context of this browsing context, otherwise false.
	 * @see #getNestedContexts()
	 */
	public boolean isNestedBrowsingContext() {
		return parentContext != null;
	}
	

	/**
	 * Returns document family of this browsing context.
	 * 
	 * @return Collection of documents which belongs to one family.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#document-family">Document family </a>
	 */
	public Collection<Html5DocumentImpl> getDocumentFamily() {
		Set<Html5DocumentImpl> family = new HashSet<Html5DocumentImpl>();
		Collection<SessionHistoryEntry> sessionEntries = sessionHistory.getSessionHistoryEntries();
		
		/*
		 * The document family of a browsing context consists of the union of all the Document objects in 
		 * that browsing context's session history and the document families of all those Document objects. 
		 */
		for (SessionHistoryEntry entry : sessionEntries) {
			Html5DocumentImpl document = entry.getDocument();
			family.add(document);
			family.addAll(document.getDocumentFamily());
		}
		
		return family;
	}

	/**
	 * Tests whether is this browsing context allowed to navigate the destination browsing context.
	 * 
	 * @param destinationBrowsingContext Browsing context which is designed as a destination browsing context.
	 * @return True if this browsing context is allowed to navigate the destination browsing context, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#allowed-to-navigate">Allowed to navigate</a>
	 */
	public boolean isAllowedToNavigate(BrowsingContext destinationBrowsingContext) {
		BrowsingContext a = this;
		BrowsingContext b = destinationBrowsingContext;
		
		if (a != b && !a.isAncestorOf(b) && !b.isTopLevelBrowsingContext() && a.getActiveDocument().
				getActiveSandboxingFlagSet().contains(SandboxingFlag.NAVIGATION_BROWSING_CONTEXT_FLAG)) {
			return false;
		}
		
		if (b.isTopLevelBrowsingContext() && b.isAncestorOf(a) && a.getActiveDocument().
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

	/**
	 * Tests whether this browsing context is familiar with the passed browsing context.
	 * 
	 * @param context Browsing context against which is test performed.
	 * @return True if this browsing context is familiar with the passed browsing context, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#familiar-with">Familiar with</a>
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

	/**
	 * Tests whether is passed string the valid browsing context name.
	 * 
	 * @param name Browsing context name which is tested.
	 * @return True if is passed string the valid browsing context name, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#valid-browsing-context-name">Valid browsing context name</a>
	 */
	public static boolean isValidBrowsingContextName(String name) {
		return !name.startsWith("_");
	}
	
	/**
	 * Tests whether is passed string the valid browsing context name or keyword.
	 * 
	 * @param name Browsing context name which is tested.
	 * @return True if is passed string the valid browsing context name or keyword, otherwise false.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#valid-browsing-context-name-or-keyword">Valid browsing context name or keyword</a>
	 */
	public static boolean isValidBrowsingContextNameOrKeyword(String name) {
		return name.equalsIgnoreCase(SELF_KEYWORD) || 
				name.equalsIgnoreCase(PARENT_KEYWORD) || 
				name.equalsIgnoreCase(TOP_KEYWORD) || 
				name.equalsIgnoreCase(BLANK_KEYWORD) || 
				isValidBrowsingContextName(name);
	}
	
	/**
	 * Tests whether the passed browsing context name or keyword marks the blank browsing context.
	 * 
	 * @param name Browsing context name or keyword which is tested.
	 * @return True if is passed browsing context name or keyword results in creating blank browsing context, otherwise false.
	 * @see #chooseBrowsingContextByName(String)
	 */
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

	/**
	 * Chooses browsing context for a given browsing context name or keyword.
	 * 
	 * @param name Browsing context name or keyword.
	 * @return Resulted browsing context for a given browsing context name or keyword.
	 * @see #isBlankBrowsingContext(String)
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#the-rules-for-choosing-a-browsing-context-given-a-browsing-context-name">Choosing browsing context given a browsing context name or keyword</a>
	 */
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
	
	
	/**
	 * Returns browsing context name.
	 * 
	 * @return Browsing context name.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#browsing-context-name">Browsing context name</a>
	 */
	public String getName() {
		return contextName;
	}
	
	/**
	 * Sets browsing context name.
	 * 
	 * @param name New browsing context name.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#browsing-context-name">Browsing context name</a>
	 */
	public void setName(String name) {
		this.contextName = name;
	}
	
	/**
	 * Returns session history.
	 * 
	 * @return Session history.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#session-history">Session history</a>
	 */
	public SessionHistory getSesstionHistory() {
		return sessionHistory;
	}
	
	/**
	 * Returns browsing unit.
	 * 
	 * @return Browsing unit.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#unit-of-related-browsing-contexts">Unit of related browsing contexts</a>
	 */
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
	
	/**
	 * Returns event loop.
	 * 
	 * @return Event loop.
	 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/webappapis.html#event-loop">The event loop</a>
	 */
	public EventLoop getEventLoop() {
		BrowsingUnit browsingUnit = getBrowsingUnit();
		return (browsingUnit != null)? browsingUnit.getEventLoop() : null;
	}
	
	/**
	 * Tries to scroll to fragment.
	 * 
	 * @param fragment Fragment where to scroll the document view.
	 * @return True if scroll was successful, otherwise false.
	 */
	public boolean scrollToFragment(String fragment) {
		BrowsingUnit browsingUnit = getBrowsingUnit();
		ScrollBarsProp scrollbars = browsingUnit.getScrollbars();
		
		return scrollbars.scrollToFragment(fragment);
	}
	
	/**
	 * Returns navigation controller for this browsing context.
	 * 
	 * @return Navigation controller.
	 */
	public NavigationController getNavigationController() {
		return navigationController;
	}
	
	/**
	 * Method which should be called for removing a child context from this browsing context.
	 * 
	 * @param child Child browsing context which should be removed.
	 */
	protected void removeChildContext(BrowsingContext child) {
		synchronized (this) {
			childContexts.remove(child);
		}

		fireBrowsingContextRemoved(child);
	}
	
	/**
	 * Method which should be called for adding a child context from this browsing context.
	 * 
	 * @param child Child browsing context which should be added.
	 */
	protected void addChildContext(BrowsingContext child) {
		synchronized (this) {
			childContexts.add(child);
		}
		
		fireBrowsingContextInserted(child);
	}
	
	private BrowsingContext getFirstFamiliar(String name) {
		BrowsingUnit browsingUnit = getBrowsingUnit();
		Set<BrowsingContext> contexts = browsingUnit.getUserAgent().getBrowsingContextsByName(name);
		
		for (BrowsingContext context : contexts) {
			if (isFamiliarWith(context)) {
				return context;
			}
		}
		
		return null;
	}
	
	private void fireBrowsingContextInserted(BrowsingContext context) {
		BrowsingContextEvent event = new BrowsingContextEvent(this, BrowsingContextEvent.EventType.INSERTED, context);
		Set<BrowsingContextListener> listenersCopy = new HashSet<BrowsingContextListener>(listeners);
		
		for (BrowsingContextListener listener : listenersCopy) {
			listener.onBrowsingContextEvent(event);
		}
		
		if (parentContext != null) {
			parentContext.fireBrowsingContextInserted(context);
		}
	}
	
	private void fireBrowsingContextRemoved(BrowsingContext context) {
		BrowsingContextEvent event = new BrowsingContextEvent(this, BrowsingContextEvent.EventType.REMOVED, context);
		Set<BrowsingContextListener> listenersCopy = new HashSet<BrowsingContextListener>(listeners);
		
		for (BrowsingContextListener listener : listenersCopy) {
			listener.onBrowsingContextEvent(event);
		}
		
		if (parentContext != null) {
			parentContext.fireBrowsingContextRemoved(context);
		}
	}
	
	private void fireBrowsingContextDestroyed() {
		BrowsingContextEvent event = new BrowsingContextEvent(this, BrowsingContextEvent.EventType.DESTROYED, null);
		Set<BrowsingContextListener> listenersCopy = new HashSet<BrowsingContextListener>(listeners);
		
		for (BrowsingContextListener listener : listenersCopy) {
			listener.onBrowsingContextEvent(event);
		}
		
		if (parentContext != null) {
			parentContext.fireBrowsingContextDestroyed();
		}
	}
}

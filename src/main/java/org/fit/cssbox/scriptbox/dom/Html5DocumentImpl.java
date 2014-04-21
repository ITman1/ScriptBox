/**
 * Html5DocumentImpl.java
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

package org.fit.cssbox.scriptbox.dom;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.html.dom.HTMLDocumentImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.events.EventImpl;
import org.apache.xerces.dom.events.MutationEventImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.document.script.ScriptableDocumentParser;
import org.fit.cssbox.scriptbox.dom.Html5DocumentEvent.EventType;
import org.fit.cssbox.scriptbox.dom.events.EventTarget;
import org.fit.cssbox.scriptbox.events.EventLoop;
import org.fit.cssbox.scriptbox.events.Task;
import org.fit.cssbox.scriptbox.history.History;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.navigation.Location;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.fit.cssbox.scriptbox.security.origins.Origin;
import org.fit.cssbox.scriptbox.security.origins.OriginContainer;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper;
import org.fit.cssbox.scriptbox.url.URLUtilsHelper.UrlComponent;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.html.HTMLBaseElement;
import org.w3c.dom.html.HTMLElement;
import org.w3c.dom.views.AbstractView;
import org.w3c.dom.views.DocumentView;

public class Html5DocumentImpl extends HTMLDocumentImpl implements EventTarget, DocumentView {
	public enum DocumentReadiness {
		LOADING,
		INTERACTIVE,
		COMPLETE
	}
	
	final public static String DEFAULT_URL_ADDRESS = "about:blank";
	final public static URL DEFAULT_URL;
	static {	   
		URL defaultURL = null;
		try {
			defaultURL = new URL(DEFAULT_URL_ADDRESS); // FIXME: about is not supported!
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		DEFAULT_URL = defaultURL;
	}
	final public static String JAVASCRIPT_SCHEME_NAME = "javascript";
	final public static String DATA_SCHEME_NAME = "data";

	private static final long serialVersionUID = -352261593104316623L;
	private static final String SCRIPT_TAG_NAME = "script";
	private static final String IFRAME_TAG_NAME = "iframe";

	private static List<String> SERVER_BASED_SCHEMES;
	static {	   
		SERVER_BASED_SCHEMES = new ArrayList<String>(3);
		SERVER_BASED_SCHEMES.add("http");
		SERVER_BASED_SCHEMES.add("https");
		SERVER_BASED_SCHEMES.add("file");
	}

	private BrowsingContext _browsingContext;
	
	@SuppressWarnings("unused")
	private int _ignoreDestructiveWritesCounter;
	
	// Every Document has an active sandboxing flag set
	private Set<SandboxingFlag> _activeSandboxingFlagSet;
	
	/*
	 * FIXME: This should be stored inside SessionHistory. For simplification it is here.
	 * Each Document object in a browsing context's session history is associated 
	 * with a unique History object which must all model the same underlying session history.
	 */
	protected History _history;
	protected Location _location;
	
	private OriginContainer<DocumentOrigin> _originContainer;
	private URL _address;
	private String _referrer;
	private Window _window;
	private SessionHistoryEntry _latestEntry;
	private boolean _fullscreenEnabledFlag;
	private String _contentType;
	private DocumentReadiness _documentReadiness;
	private ScriptableDocumentParser _parser;
	private Task unloadTask;
	
	private boolean _salvageableFlag;
	private boolean _firedUnloadFlag;
	private boolean _pageShowingFlag;
	
	private Set<Html5DocumentEventListener> listeners;
	
	private EventListener documentEventListener = new EventListener() {
		
		@Override
		public void handleEvent(Event evt) {
			String eventType = evt.getType();
			org.w3c.dom.events.EventTarget target = evt.getTarget();
			
			if (!(evt instanceof MutationEventImpl)) {
				return;
			}
			
			Html5ScriptElementImpl script = null;
			
			MutationEventImpl mutationEvent = (MutationEventImpl)evt;
			if (eventType.equals(MutationEventImpl.DOM_ATTR_MODIFIED)) {
				String prevValue = mutationEvent.getPrevValue();
				String attrName = mutationEvent.getAttrName();
				
				if (target instanceof Html5ScriptElementImpl && attrName.equalsIgnoreCase("src") && prevValue == null) {
					script = (Html5ScriptElementImpl)target;
				}
			} else if (eventType.equals(MutationEventImpl.DOM_NODE_INSERTED)) {
				Node relatedNode = mutationEvent.getRelatedNode();
				
				if (target instanceof Html5ScriptElementImpl) {
					script = (Html5ScriptElementImpl)target;
				} else if (relatedNode instanceof Html5ScriptElementImpl) {
					script = (Html5ScriptElementImpl)relatedNode;
				}		
			}
			
			if (script != null && !script.isParserInserted()) {
				script.prepareScript();
			}
		}
	};
	
	private Html5DocumentImpl(BrowsingContext browsingContext, URL address, Set<SandboxingFlag> sandboxingFlagSet, String referrer, boolean createWindow, String contentType, ScriptableDocumentParser parser) {	
		listeners = new HashSet<Html5DocumentEventListener>();
		
		_salvageableFlag = true;
		_firedUnloadFlag = false;
		_pageShowingFlag = false;
		_ignoreDestructiveWritesCounter = 0;
		
		_browsingContext = browsingContext;
		_address = address;
		_referrer = referrer;
		_contentType = contentType;
		_documentReadiness = (parser == null)? DocumentReadiness.COMPLETE : DocumentReadiness.LOADING;
		_activeSandboxingFlagSet = new HashSet<SandboxingFlag>();
		_parser = parser;
		
		if (sandboxingFlagSet != null) {
			_activeSandboxingFlagSet.addAll(sandboxingFlagSet);
		}
		
		DocumentOrigin documentOrigin = null;
		DocumentOrigin effectiveScriptOrigin = null;
		
		if (_activeSandboxingFlagSet.contains(SandboxingFlag.ORIGIN_BROWSING_CONTEXT_FLAG)) {
			documentOrigin = DocumentOrigin.createUnique(this);
			effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
		} else if (address != null && (SERVER_BASED_SCHEMES.contains(address.getProtocol()) || address.getProtocol().equals(JAVASCRIPT_SCHEME_NAME))) {
			UrlOrigin addressOrigin = new UrlOrigin(address);
			documentOrigin = DocumentOrigin.create(this, addressOrigin);
			effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
		} else if (address != null && address.getProtocol().equals(DATA_SCHEME_NAME)) {
			// TODO: If a Document was generated from a data: URL found in another Document or in a script
		} else if (address != null && address.equals(DEFAULT_URL)) {
			Html5DocumentImpl creatorDocument = browsingContext.getCreatorDocument();
			if (creatorDocument != null) {
				OriginContainer<?> originContainer = creatorDocument.getOriginContainer();
				documentOrigin = DocumentOrigin.create(this, originContainer.getOrigin());
				effectiveScriptOrigin = DocumentOrigin.create(this, originContainer.getEffectiveScriptOrigin());
			} else {
				documentOrigin = DocumentOrigin.createUnique(this);
				effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
			}
		} 
		/*
		 * TODO:
		 * else if a Document is an iframe srcdoc document
		 * else if a Document was obtained in some other manner
		 */

		_originContainer = new OriginContainer<DocumentOrigin>(documentOrigin, effectiveScriptOrigin);
		
		if (createWindow) {
			_window = new Window(this);
		}
		
		_history = new History(this);
		_location = new Location(this);
		
		addEventListener(MutationEventImpl.DOM_NODE_INSERTED, documentEventListener);
		addEventListener(MutationEventImpl.DOM_ATTR_MODIFIED, documentEventListener);
	}
	
	public static Html5DocumentImpl createDocument(BrowsingContext browsingContext, URL address, Html5DocumentImpl recycleWindowDocument, String contentType) {
		return createDocument(browsingContext, address, recycleWindowDocument, contentType, null);
	}
	
	public static Html5DocumentImpl createDocument(BrowsingContext browsingContext, URL address, Html5DocumentImpl recycleWindowDocument, String contentType, ScriptableDocumentParser parser) {
		Html5DocumentImpl document = null;

		if (recycleWindowDocument != null) {
			document = new Html5DocumentImpl(browsingContext, address, null, null, false, contentType, parser);
			document._window = recycleWindowDocument._window;
			document._window.setDocumentImpl(document);
		} else {
			document = new Html5DocumentImpl(browsingContext, address, null, null, true, contentType, parser);
		}
		
		return document;
	}
	
	public static Html5DocumentImpl createBlankDocument(BrowsingContext browsingContext) {
		String refferer = null;
		
		if (browsingContext.hasCreatorDocument()) {
			refferer = browsingContext.getCreatorDocument().getURL();
		}
		
		Html5DocumentImpl document = new Html5DocumentImpl(browsingContext, DEFAULT_URL, null, refferer, true, "text/html", null);

		document.setInputEncoding("UTF-8");
		
		Element htmlElement = document.createElement("html");
		Element headElement = document.createElement("head");
		Element bodyElement = document.createElement("body");
		
		htmlElement.appendChild(headElement);
		htmlElement.appendChild(bodyElement);
		document.appendChild(htmlElement);
		
		return document;
	}
	
	public static Html5DocumentImpl createSandboxedDocument(BrowsingContext browsingContext) {
		Set<SandboxingFlag> sandboxingFlagSet = new HashSet<SandboxingFlag>();
		Html5DocumentImpl document = new Html5DocumentImpl(browsingContext, DEFAULT_URL, sandboxingFlagSet, null, true, "text/html", null);
		
		return document;
	}
	
	@ScriptFunction
	@Override
	public Text createTextNode(String data) {
		return super.createTextNode(data);
	}
	
	@ScriptFunction
	@Override
	public synchronized Element getElementById(String elementId) {
		// TODO Auto-generated method stub
		return super.getElementById(elementId);
	}
	
	@ScriptFunction
	@Override
	public Event createEvent(String type) throws DOMException {
		return super.createEvent(type);
	}
	
	@ScriptFunction
	@Override
	public Element createElement( String tagName) throws DOMException {
		tagName = tagName.toLowerCase(Locale.ENGLISH);
		
		if (tagName.equals(SCRIPT_TAG_NAME)) {
			return new Html5ScriptElementImpl(this, tagName);
		} else if (tagName.equals(IFRAME_TAG_NAME)) {
			return new Html5IFrameElementImpl(this, tagName);
		}
		
		return super.createElement(tagName);
	}
	
	public BrowsingContext getBrowsingContext() {
		return _browsingContext;
	}
	
	public HTMLBaseElement getBaseElement() {
		// FIXME: We should not wait for completeness, but try to get base address from actual loaded elements - proper synchronized section is needed.
		if (_documentReadiness == DocumentReadiness.COMPLETE) {
			HTMLElement head = getHead();
			NodeList baseElements = head.getElementsByTagName("base");
			
			if (baseElements.getLength() > 0) {
				Node baseElement = baseElements.item(0);
				
				if (baseElement instanceof HTMLBaseElement) {
					return (HTMLBaseElement)baseElement;
				}
			}
		}
		
		return null;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#fallback-base-url
	 */
	public URL getFallbackBaseAddress() {
		
		/* 1) If the Document is an iframe srcdoc document, then return the document base URL of the 
		 * Document's browsing context's browsing context container's Document and abort these steps.
		 */
		
		IFrameBrowsingContext iframeContext = (_browsingContext instanceof IFrameBrowsingContext)? (IFrameBrowsingContext)_browsingContext : null;
		Element _iframeElement = (iframeContext != null)? iframeContext.getContainer() : null;
		Html5IFrameElementImpl iframeElement = (_iframeElement instanceof Html5IFrameElementImpl)? (Html5IFrameElementImpl)_iframeElement : null;
		if (iframeElement != null && iframeElement.getSrcdoc() != null) {
			Document _iframeDocument = iframeElement.getOwnerDocument();
			Html5DocumentImpl iframeDocument = (_iframeDocument instanceof Html5DocumentImpl)? (Html5DocumentImpl)_iframeDocument : null;
			
			if (iframeDocument != null) {
				URL url = iframeDocument.getBaseAddress();
				return url;
			}
		} 
		
		/*
		 * 2) If the document's address is about:blank, and the Document's browsing context has a creator 
		 * browsing context, then return the document base URL of the creator Document, and abort these steps.
		 */
		
		Html5DocumentImpl creatorDocument = _browsingContext.getCreatorDocument();
		if (_address != null && _address.equals(DEFAULT_URL) && creatorDocument != null) {
			URL url = creatorDocument.getBaseAddress();
			return url;
		}
		
		/*
		 * 3) Return the document's address.
		 */
		return _address;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/infrastructure.html#document-base-url
	 */
	public URL getBaseAddress() {
		HTMLBaseElement baseElement = getBaseElement();
		URL baseURL = null;
		try {
			baseURL = (baseElement != null)? new URL(baseElement.getHref()) : null;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		/*
		 * 1) If there is no base element that has an href attribute in the Document, 
		 * then the document base URL is the Document's fallback base URL
		 */
		if (baseURL == null) {
			return getFallbackBaseAddress();
		}
		
		/*
		 * TODO?: Implement frozen URL?
		 * http://www.w3.org/html/wg/drafts/html/CR/document-metadata.html#frozen-base-url
		 */
		/*
		 * 2) Otherwise, the document base URL is the frozen base URL of the first 
		 * base element in the Document that has an href attribute, in tree order.
		 */
		return baseURL;

	}
	
	/*
	 * A Document is said to be fully active when it is the active document of its 
	 * browsing context, and either its browsing context is a top-level browsing 
	 * context, or it has a parent browsing context and the Document through 
	 * which it is nested is itself fully active.
	 */
	public boolean isFullyActive() {
		boolean fullyActive = true;
		BrowsingContext parentContext = _browsingContext.getParentContext();
		
		fullyActive = fullyActive && _browsingContext.getActiveDocument() == this;
		fullyActive = fullyActive && _browsingContext.isTopLevelBrowsingContext();
		
		if (parentContext != null) {
			fullyActive = fullyActive || (parentContext != null && parentContext.getActiveDocument().isFullyActive());
		}

		return fullyActive;
	}
	
	public boolean isActiveDocument() {
		return _browsingContext.getActiveDocument() == this;
	}
	
	/*
	 * The document family of a Document object consists of the union of all 
	 * the document families of the browsing contexts that are nested through the Document object.
	 */
	public Collection<Html5DocumentImpl> getDocumentFamily() {
		Set<Html5DocumentImpl> family = new HashSet<Html5DocumentImpl>();
		Collection<BrowsingContext> nestedContexts = _browsingContext.getNestedContexts();
		
		for (BrowsingContext context : nestedContexts) {
			family.addAll(context.getDocumentFamily());
		}
		
		return family;
	}

	public Set<SandboxingFlag> getActiveSandboxingFlagSet() {
		return Collections.unmodifiableSet(_activeSandboxingFlagSet);
	}
	
	public void setActiveSandboxingFlags(Collection<SandboxingFlag> flags) {
		if (flags == null) {
			return;
		}
		
		for (SandboxingFlag flag : flags) {
			setActiveSandboxingFlag(flag);
		}
	}
	
	public void setActiveSandboxingFlag(SandboxingFlag flag) {
		_activeSandboxingFlagSet.add(flag);
		
		DocumentOrigin documentOrigin = null;
		DocumentOrigin effectiveScriptOrigin = null;
		
		if (flag.equals(SandboxingFlag.ORIGIN_BROWSING_CONTEXT_FLAG)) {
			documentOrigin = DocumentOrigin.createUnique(this);
			effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
		}
		
		_originContainer = new OriginContainer<DocumentOrigin>(documentOrigin, effectiveScriptOrigin);
	}
	
	public void setLatestEntry(SessionHistoryEntry entry) {
		_latestEntry = entry;
	}
	
	public SessionHistoryEntry getLatestEntry() {
		return _latestEntry;
	}
	
	public void setAddress(URL address) {
		_address = address;
		
		_location.onAddressChanged();
		
		fireJointSessionHistoryEvent(EventType.ADDRESS_CHANGED);
	}
	
	public URL getAddress() {
		return _address;
	}
	
	@Override
	public String getURL() {
		return _address.toExternalForm();
	}
	
	@Override
	public String getReferrer() {
		return _referrer;
	}
	
	public void setAddressFragment(String fragment) {
		_address = URLUtilsHelper.setComponent(_address, UrlComponent.REF, fragment);
	}
	
	public OriginContainer<DocumentOrigin> getOriginContainer() {
		return _originContainer;
	}
	
	public Origin<?> getOrigin() {
		return _originContainer.getOrigin();
	}
	
	public Origin<?> getEffectiveScriptOrigin() {
		return _originContainer.getEffectiveScriptOrigin();
	}
	
	public void implementSandboxing() {
		if (_browsingContext instanceof WindowBrowsingContext) {
			setActiveSandboxingFlags(((WindowBrowsingContext)_browsingContext).getPopupSandboxingFlagSet());
		}
		
		if (_browsingContext instanceof IFrameBrowsingContext) {
			setActiveSandboxingFlags(((IFrameBrowsingContext)_browsingContext).getIframeSandboxingFlagSet());
		}
		
		if (_browsingContext.isNestedBrowsingContext()) {
			setActiveSandboxingFlags(_browsingContext.getCreatorContext().getActiveDocument().getActiveSandboxingFlagSet());
		}

		//TODO: The flags set on the Document's resource's forced sandboxing flag set, if it has one.
	} 
	
	public boolean promptToUnload() {
		return true;
	}
	
	public boolean isPromptToUnloadRunning() {
		return false;
	}
	
	public boolean isUnloadRunning() {
		return false;
	}
	
	/*
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#unload-a-document
	 */
	public synchronized void unload(boolean recycle) {
		unloadTask = getEventLoop().getRunningTask();
		
		/*
		 * TODO: Implement
		 */
		
		unloadTask = null;
	}
	
	public synchronized Task getUnloadTask() {
		return unloadTask;
	}
	
	public Window getWindow() {
		return _window;
	}
	
	public boolean isFullscreenEnabledFlag() {
		return _fullscreenEnabledFlag;
	} 
	
	public void setEnableFullscreenFlag(boolean value) {
		_fullscreenEnabledFlag = value;
	}
		
	public String getContentType() {
		return _contentType;
	}
	
	public DocumentReadiness getDocumentReadiness() {
		return _documentReadiness;
	}
	
	public void setDocumentReadiness(DocumentReadiness readiness) {
		_documentReadiness = readiness;
	}
	
	public boolean hasDefaultAddress() {
		return _address == null || _address.equals(DEFAULT_URL);
	}
	
	public ScriptableDocumentParser getParser() {
		return _parser;
	}
	
	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener) {
		addEventListener(type, listener, false);
	}
	
	@ScriptFunction
	@Override
	public void addEventListener(String type, EventListener listener, boolean useCapture) {
		super.addEventListener(type, listener, useCapture);
	}

	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener) {
		removeEventListener(type, listener, false);
	}
	
	@ScriptFunction
	@Override
	public void removeEventListener(String type, EventListener listener, boolean useCapture) {
		super.removeEventListener(type, listener, useCapture);
	}
	
	@ScriptFunction
	public boolean dispatchEvent(Event event) {
		return super.dispatchEvent(event);
	}
	
	/* See:http://www.w3.org/html/wg/drafts/html/CR/browsers.html#discard-a-document
	 * TODO: Implement
	 */
	public void discard() {
		_browsingContext = null;
	}
	
	public EventLoop getEventLoop() {
		return _browsingContext.getEventLoop();
	}
	
	public String getParserSource() {
		return (_parser != null)? _parser.getParserSource() : null;
	}
	
	@Override
	protected void addEventListener(NodeImpl node, String type, EventListener listener, boolean useCapture) {
		super.addEventListener(node, type, listener, useCapture);
	}

	@Override
	protected boolean dispatchEvent(NodeImpl node, Event event) {
		if (!(event instanceof EventImpl)) {
			return false;
		}
		EventImpl evt = (EventImpl)event;
		
		// Initialize
		evt.target = node;
		evt.stopPropagation = false;
		evt.preventDefault = false;
		
		// Window capture phase
		evt.currentTarget = _window;
		evt.eventPhase = Event.CAPTURING_PHASE;
		_window.dispatchEventFromDocument(evt);
		
		if (!evt.stopPropagation) {
			super.dispatchEvent(node, evt);
		}
		
		// Window bubble phase
		if (!evt.stopPropagation && evt.bubbles) {
			evt.currentTarget = _window;
			evt.eventPhase = Event.BUBBLING_PHASE;
			_window.dispatchEventFromDocument(evt);
		}

		return evt.preventDefault;
	}

	@Override
	public AbstractView getDefaultView() {
		return (_browsingContext != null)? _browsingContext.getWindowProxy() : null;
	}

	/*
	 * TODO:
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#unloading-document-cleanup-steps
	 */
	public void runUnloadingDocumentCleanupSteps() {
		
	}
	
	/*
	 * TODO:
	 * http://www.w3.org/html/wg/drafts/html/CR/browsers.html#abort-a-document
	 */
	@Override
	public void abort() {
		if (_browsingContext != null) {
			Collection<BrowsingContext> contexts = _browsingContext.getNestedContexts();
			
			for (BrowsingContext context : contexts) {
				Html5DocumentImpl activeDocument = context.getActiveDocument();
				activeDocument.abort();
				
				if (!activeDocument.isSalvageableFlag()) {
					_salvageableFlag = false;
				}
			}
			
			/*
			 * TODO:
			 * Cancel any instances of the fetch algorithm in the context of this Document, 
			 * discarding any tasks queued for them, and discarding any further data received 
			 * from the network for them. If this resulted in any instances of the fetch algorithm 
			 * being canceled or any queued tasks or any network data getting discarded, then set the 
			 * Document's salvageable state to false.
			 */
			
			if (_parser != null && _parser.isActive()) {
				_parser.abort();
				_salvageableFlag = false;
			}
		} else {
			_salvageableFlag = false;
		}
		

	}

	public boolean isSalvageableFlag() {
		return _salvageableFlag;
	}

	public void setSalvageableFlag(boolean salvageableFlag) {
		this._salvageableFlag = salvageableFlag;
	}

	public boolean isFiredUnloadFlag() {
		return _firedUnloadFlag;
	}

	public void setFiredUnloadFlag(boolean firedUnloadFlag) {
		this._firedUnloadFlag = firedUnloadFlag;
	}

	public boolean isPageShowingFlag() {
		return _pageShowingFlag;
	}

	public void setPageShowingFlag(boolean pageShowingFlag) {
		this._pageShowingFlag = pageShowingFlag;
	}
	
	public synchronized void incrementIgnoreDestructiveWritesCounter() {
		_ignoreDestructiveWritesCounter++;
	}
	
	public synchronized void decrementIgnoreDestructiveWritesCounter() {
		_ignoreDestructiveWritesCounter--;
	}
	
	@ScriptFunction
	@Override
	public String toString() {
		return super.toString();
	}
	
	public History getHistory() {
		return _history;
	}
	
	public Location getLocation() {
		return _location;
	}
	
	/*
	 * https://dvcs.w3.org/hg/fullscreen/raw-file/tip/Overview.html#dom-document-exitfullscreen
	 */
	public void exitFullscreen() {
		
	}
	
	/*
	 * https://dvcs.w3.org/hg/fullscreen/raw-file/tip/Overview.html#fully-exit-fullscreen
	 */
	public void fullyExitFullscreen() {
		
	}
	
	protected void fireJointSessionHistoryEvent(EventType eventType) {
		if (listeners.isEmpty()) {
			return;
		}
		
		Html5DocumentEvent event = new Html5DocumentEvent(this, eventType);
		Set<Html5DocumentEventListener> listenersCopy = new HashSet<Html5DocumentEventListener>(listeners);
		
		for (Html5DocumentEventListener listener : listenersCopy) {
			listener.onDocumentEvent(event);
		}
	}
}

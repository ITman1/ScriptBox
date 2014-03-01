package org.fit.cssbox.scriptbox.dom;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriBuilderException;

import org.apache.html.dom.HTMLDocumentImpl;
import org.fit.cssbox.scriptbox.browser.BrowsingContext;
import org.fit.cssbox.scriptbox.browser.IFrameBrowsingContext;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.browser.WindowBrowsingContext;
import org.fit.cssbox.scriptbox.history.SessionHistory;
import org.fit.cssbox.scriptbox.history.SessionHistoryEntry;
import org.fit.cssbox.scriptbox.misc.UrlUtils;
import org.fit.cssbox.scriptbox.misc.UrlUtils.UrlComponent;
import org.fit.cssbox.scriptbox.security.SandboxingFlag;
import org.fit.cssbox.scriptbox.security.origins.DocumentOrigin;
import org.fit.cssbox.scriptbox.security.origins.OriginContainer;
import org.fit.cssbox.scriptbox.security.origins.UrlOrigin;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;

public class Html5DocumentImpl extends HTMLDocumentImpl {
	final public static String DEFAULT_URL_ADDRESS = "about:blank";
	private static URL _DEFAULT_URL;
    static {       
    	try {
    		_DEFAULT_URL = new URL(DEFAULT_URL_ADDRESS);
		} catch (MalformedURLException e) {
		}
    }
	final public static URL DEFAULT_URL = _DEFAULT_URL;
	final public static String JAVASCRIPT_SCHEME_NAME = "javascript";
	final public static String DATA_SCHEME_NAME = "data";

	private static final long serialVersionUID = -352261593104316623L;
	private static final String SCRIPT_TAG_NAME = "script";

	private static List<String> SERVER_BASED_SCHEMES;
    static {       
    	SERVER_BASED_SCHEMES = new ArrayList<String>(3);
    	SERVER_BASED_SCHEMES.add("http");
    	SERVER_BASED_SCHEMES.add("https");
    	SERVER_BASED_SCHEMES.add("file");
    }

	private BrowsingContext _browsingContext;
	
	// Every Document has an active sandboxing flag set
	private Set<SandboxingFlag> _activeSandboxingFlagSet;
	
	private OriginContainer<DocumentOrigin> _originContainer;
	private URL _address;
	private String _referrer;
	private Window _window;
	private SessionHistoryEntry _latestEntry;
	private boolean _fullscreenEnabledFlag;
	private String _contentType;
	
	private Html5DocumentImpl(BrowsingContext browsingContext, URL address, Set<SandboxingFlag> sandboxingFlagSet, String referrer, boolean createWindow) {
		_browsingContext = browsingContext;
		_address = address;
		_referrer = referrer;
		
		if (sandboxingFlagSet != null) {
			_activeSandboxingFlagSet = new HashSet<SandboxingFlag>();
		} else {
			_activeSandboxingFlagSet.addAll(sandboxingFlagSet);
		}
		
		DocumentOrigin documentOrigin = null;
		DocumentOrigin effectiveScriptOrigin = null;
		
		if (sandboxingFlagSet.contains(SandboxingFlag.ORIGIN_BROWSING_CONTEXT_FLAG)) {
			documentOrigin = DocumentOrigin.createUnique(this);
			effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
		} else if (address != null && SERVER_BASED_SCHEMES.contains(address.getProtocol())) {
			UrlOrigin addressOrigin = new UrlOrigin(address);
			documentOrigin = DocumentOrigin.create(this, addressOrigin);
			effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
		} else if (address != null && address.getProtocol().equals(DATA_SCHEME_NAME)) {
			// TODO: If a Document was generated from a data: URL found in another Document or in a script
		} else if (address != null && address.toExternalForm().equals(DEFAULT_URL)) {
			Html5DocumentImpl creatorDocument = browsingContext.getCreatorDocument();
			if (creatorDocument != null) {
				OriginContainer<?> originContainer = creatorDocument.getOriginContainer();
				documentOrigin = DocumentOrigin.create(this, originContainer.getOrigin());
				effectiveScriptOrigin = DocumentOrigin.create(this, originContainer.getEffectiveScriptOrigin());
			} else {
				documentOrigin = DocumentOrigin.createUnique(this);
				effectiveScriptOrigin = DocumentOrigin.create(this, documentOrigin);
			}
		} else if (address != null && address.getProtocol().equals(JAVASCRIPT_SCHEME_NAME)) {
			// TODO: If a Document was created as part of the processing for javascript: URLs
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
	}
	
	public static Html5DocumentImpl createDocument(BrowsingContext browsingContext, URL address, Html5DocumentImpl recycleWindowDocument) {
		Html5DocumentImpl document = null;

		if (recycleWindowDocument != null) {
			document = new Html5DocumentImpl(browsingContext, address, null, null, false);
			document._window = recycleWindowDocument._window;
		} else {
			document = new Html5DocumentImpl(browsingContext, address, null, null, true);
		}
		
		return document;
	}
	
	public static Html5DocumentImpl createBlankDocument(BrowsingContext browsingContext) {
		String refferer = null;
		
		if (browsingContext.hasCreatorDocument()) {
			refferer = browsingContext.getCreatorDocument().getURL();
		}
		
		Html5DocumentImpl document = new Html5DocumentImpl(browsingContext, DEFAULT_URL, null, refferer, true);

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
		Html5DocumentImpl document = new Html5DocumentImpl(browsingContext, DEFAULT_URL, sandboxingFlagSet, null, true);
		
		return document;
	}
	
	@Override
	public Element createElement( String tagName ) throws DOMException {
		tagName = tagName.toLowerCase(Locale.ENGLISH);
		
		if ( tagName.equals(SCRIPT_TAG_NAME)) {
			Html5ScriptElementImpl scriptElement = new Html5ScriptElementImpl(this, tagName);
			return scriptElement;
		}
		
		return super.createElement(tagName);
	}
	
	public BrowsingContext getBrowsingContext() {
		return _browsingContext;
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
		_address = UrlUtils.setComponent(_address, UrlComponent.REF, fragment);
	}
	
	public OriginContainer<DocumentOrigin> getOriginContainer() {
		return _originContainer;
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
	
	public void unload(boolean recycle) {
		
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
	
	public void setContentType(String contentType) {
		_contentType = contentType;
	}
	
	public String getContentType() {
		return _contentType;
	}
}

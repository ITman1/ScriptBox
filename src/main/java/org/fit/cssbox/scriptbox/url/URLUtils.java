package org.fit.cssbox.scriptbox.url;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.fit.cssbox.scriptbox.dom.DOMException;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;

/*
 * http://url.spec.whatwg.org/#urlutils
 */
/**
 * Class implementing script visible method for the URL manipulation.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://url.spec.whatwg.org/#urlutils">Interface URLUtils</a>
 */
public class URLUtils {

	protected String input;
	protected URLSearchParams queryObject;
	protected ParserURL url;
	
	protected ParserURL baseUrl;
		
	/**
	 * Returns query encoding.
	 * 
	 * @return Query encoding.
	 */
	public String getQueryEncoding() {
		return "UTF-8";
	}
	
	/**
	 * Returns associated URL.
	 * 
	 * @return Associated URL.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-href">URL href</a>
	 */
	@ScriptGetter
	public String getHref() {
		if (url == null) {
			return input;
		} else {
			return url.serialize();
		}
	}
	
	/**
	 * Sets associated URL.
	 * 
	 * @param scheme New associated URL.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-href">URL href</a>
	 */
	@ScriptSetter
	public void setHref(String href) {
		setInput(href);
		
		Object contextObject = getContextObject();
		if (contextObject.getClass().equals(org.fit.cssbox.scriptbox.url.URL.class) && url == null) {
			throwTypeErrorException("\"" + href + "\" is not a valid URL.");
		}
		
		preUpdateSteps(href);
	}
	
	/**
	 * TODO:
	 * Returns URL origin.
	 * 
	 * @return URL origin.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-origin">ORigin</a>
	 */
	@ScriptGetter
	public String getOrigin() {
		if (url == null) {
			return "";
		}
		
		return "";
	}
	
	/**
	 * Returns protocol component.
	 * 
	 * @return Protocol component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-protocol">Protocol</a>
	 */
	@ScriptGetter
	public String getProtocol() {
		if (url == null) {
			return ":";
		}
		
		return url.getScheme() + ":";
	}
	
	/**
	 * Sets protocol component.
	 * 
	 * @param scheme New protocol component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-protocol">Protocol</a>
	 */
	@ScriptSetter
	public void setProtocol(String protocol) {
		if (url == null) {
			return;
		}
		
		BasicURLParser.getInstance().parse(protocol + ":", null, null, url, BasicURLParser.State.SCHEME_START);
		
		preUpdateSteps(null);
	}

	/**
	 * Returns username component.
	 * 
	 * @return Username component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-username">Username</a>
	 */
	@ScriptGetter
	public String getUsername() {
		if (url == null) {
			return "";
		}
		
		return url.getUsername();
	}
	
	/**
	 * Sets username component.
	 * 
	 * @param username New username component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-username">Username</a>
	 */
	@ScriptSetter
	public void setUsername(String username) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		url.setUsername("");
		
		try {
			username = URLEncoder.encode(username, "UTF-8");
			url.setUsername(username);
		} catch (UnsupportedEncodingException e) {
		}
				
		preUpdateSteps(null);
	}
	
	/**
	 * Returns password component.
	 * 
	 * @return Password component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-password">Password</a>
	 */
	@ScriptGetter
	public String getPassword() {
		if (url == null) {
			return "";
		}
		
		return url.getPassword();
	}
	
	/**
	 * Sets password component.
	 * 
	 * @param password New password component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-password">Password</a>
	 */
	@ScriptSetter
	public void setPassword(String password) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		if (password != null && password.isEmpty()) {
			url.setPassword(null);
			preUpdateSteps(null);
			return;
		}
		
		url.setPassword("");
		
		try {
			password = URLEncoder.encode(password, "UTF-8");
			url.setPassword(password);
		} catch (UnsupportedEncodingException e) {
		}
				
		preUpdateSteps(null);
	}
	
	/**
	 * Returns host component.
	 * 
	 * @return Host component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-host">Host</a>
	 */
	@ScriptGetter
	public String getHost() {
		if (url == null) {
			return "";
		}
		
		String port = url.getPort();
		
		if (port.isEmpty()) {
			return url.serializedHost();
		} else {
			return url.serializedHost() + ":" + port;
		}
	}
	
	/**
	 * Sets host component.
	 * 
	 * @param host New host component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-host">Host</a>
	 */
	@ScriptSetter
	public void setHost(String host) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		BasicURLParser.getInstance().parse(host, null, null, url, BasicURLParser.State.HOST);
		
		preUpdateSteps(null);
	}
	
	/**
	 * Returns host name.
	 * 
	 * @return Host name.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-hostname">Host</a>
	 */
	@ScriptGetter
	public String getHostname() {
		if (url == null) {
			return "";
		}
		
		return url.serializedHost();
	}
	
	/**
	 * Sets host name.
	 * 
	 * @param host New host name.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-hostname">Host</a>
	 */
	@ScriptSetter
	public void setHostname(String hostname) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		BasicURLParser.getInstance().parse(hostname, null, null, url, BasicURLParser.State.HOSTNAME);
		
		preUpdateSteps(null);
	}
	
	/**
	 * Returns port component.
	 * 
	 * @return Port component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-port">Port</a>
	 */
	@ScriptGetter
	public String getPort() {
		if (url == null) {
			return "";
		}
		
		return url.getPort();
	}
	
	/**
	 * Sets port component.
	 * 
	 * @param port New port component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-port">Port</a>
	 */
	@ScriptSetter
	public void setPort(String port) {
		if (url == null || !url.isRelativeFlag() || url.getScheme().equalsIgnoreCase("file")) {
			return;
		}
		
		BasicURLParser.getInstance().parse(port, null, null, url, BasicURLParser.State.PORT);
		
		preUpdateSteps(null);
	}
	
	/**
	 * Returns path name.
	 * 
	 * @return Path name.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-pathname">Path name</a>
	 */
	@ScriptGetter
	public String getPathname() {
		if (url == null) {
			return "";
		}
		
		if (!url.isRelativeFlag()) {
			return url.getSchemeData();
		}
		
		List<String> pathList = url.getPath();
		
		return "/" + StringUtils.join(pathList, "/");
	}
	
	/**
	 * Sets path name.
	 * 
	 * @param host New path name.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-pathname">Path name</a>
	 */
	@ScriptSetter
	public void setPathname(String path) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		List<String> pathList = new ArrayList<String>();
		
		url.setPath(pathList);
		
		BasicURLParser.getInstance().parse(path, null, null, url, BasicURLParser.State.RELATIVE_PATH_START);
		
		preUpdateSteps(null);
	}
	
	/**
	 * Returns search.
	 * 
	 * @return Search.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-search">Search</a>
	 */
	@ScriptGetter
	public String getSearch() {
		if (url == null) {
			return "";
		}
		
		String query = url.getQuery();
		if (query == null || query.isEmpty()) {
			return "";
		}
		
		return "?" + query;
	}
	
	/**
	 * Sets search.
	 * 
	 * @param host New search.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-search">Search</a>
	 */
	@ScriptSetter
	public void setSearch(String search) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		if (search == null || search.isEmpty()) {
			queryObject.clear();
			queryObject.updateSteps();
			return;
		}
		
		String input = (search.length() > 0 && search.charAt(0) == '?')? search.substring(1) : search;
	
		url.setQuery("");
		
		BasicURLParser.getInstance().parse(input, null, getQueryEncoding(), url, BasicURLParser.State.QUERY);
		
		String query = url.getQuery();
		queryObject.setQuery(query);
		queryObject.updateSteps();
	}
	
	/*
	 * TODO: Implement search params
	 * http://url.spec.whatwg.org/#dom-url-searchparams
	 */
	/**
	 * Returns search parameters.
	 * 
	 * @return Search parameters.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-searchparams">Search parameters</a>
	 */
	@ScriptGetter
	public URLSearchParams getSearchParams() {
		return null;
	}
	
	/*
	 * TODO: Implement search params
	 * http://url.spec.whatwg.org/#dom-url-searchparams
	 */
	/**
	 * Sets search parameters.
	 * 
	 * @param host New search parameters.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-searchparams">Search parameters</a>
	 */
	@ScriptSetter
	public void setSearchParams(URLSearchParams href) {
		
	}
	
	/**
	 * Returns hash component.
	 * 
	 * @return Hash component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-hash">Hash</a>
	 */
	@ScriptGetter
	public String getHash() {
		if (url == null) {
			return "";
		}
		
		String fragment = url.getFragment();
		if (fragment == null || fragment.isEmpty()) {
			return "";
		}
		
		
		return "#" + fragment;
	}
	
	/**
	 * Sets hash component.
	 * 
	 * @param fragment New hash component.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url-hash">Hash</a>
	 */
	@ScriptSetter
	public void setHash(String fragment) {
		if (url == null || url.getScheme().equalsIgnoreCase("javascript")) {
			return;
		}
		
		if (fragment == null || fragment.isEmpty()) {
			url.setFragment(null);
			preUpdateSteps(null);
			return;
		}
		
		String input = (fragment.length() > 0 && fragment.charAt(0) == '#')? fragment.substring(1) : fragment;
		
		url.setFragment("");
		
		BasicURLParser.getInstance().parse(input, null, null, url, BasicURLParser.State.FRAGMENT);
		
		preUpdateSteps(null);
	}
	
	/**
	 * Sets new URL as current URL.
	 * 
	 * @param newInput Serialized new URL.
	 * @see <a href="http://url.spec.whatwg.org/#concept-uu-set-the-input">Set the input</a>
	 */
	protected void setInput(String newInput) {
		url = null;
		input = newInput;
		
		ParserURL base = getBase();
		String queryEncoding = getQueryEncoding();
		url = URLParser.getInstance().parse(input, base, queryEncoding);
		
		if (url != null && url.isRelativeFlag()) {
			if (queryObject == null) {
				queryObject = new URLSearchParams();
			}
			String query = url.getQuery();
			queryObject.setQuery(query);
		}
		
		if (url == null && queryObject != null) {
			queryObject.clear();
		}
	}

	/**
	 * Returns associated base.
	 * 
	 * @return Appropriate base URL for this object.
	 * @see <a href="http://url.spec.whatwg.org/#concept-uu-get-the-base">Get the base</a>
	 */
	protected ParserURL getBase() {
		return baseUrl;
	}
	
	/**
	 * Sets associated base.
	 * 
	 * @param baseUrl Sets appropriate base URL for this object.
	 * @see <a href="http://url.spec.whatwg.org/#concept-uu-get-the-base">Get the base</a>
	 */
	protected void setBaseUrl(ParserURL baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/**
	 * Runs pre-update steps for this object.
	 * 
	 * @param value New URL value that has been set.
	 * @see <a href="http://url.spec.whatwg.org/#pre-update-steps">Pre-update steps</a>
	 */
	public void preUpdateSteps(String value) {
		if (value == null) {
			value = url.serialize();
		}
		
		updateSteps(value);
	}
	
	/**
	 * Runs update steps for this object.
	 * 
	 * @param value New URL value that has been set.
	 * @see <a href=http://url.spec.whatwg.org/#concept-uu-update">Update steps</a>
	 */
	public void updateSteps(String value) {
	}
	
	/**
	 * Returns context object.
	 * 
	 * @return Context object.
	 * @see <a href="http://dom.spec.whatwg.org/#context-object">Context object</a>
	 */
	protected Object getContextObject() {
		return this;
	}
	
	/**
	 * Throws type error exception.
	 */
	protected void throwTypeErrorException() {
		throwTypeErrorException("SyntaxError");
	}
	
	/**
	 * Throws type error exception.
	 * 
	 * @param message Message.
	 */
	protected void throwTypeErrorException(String message) {
		throw new DOMException(DOMException.SYNTAX_ERR, message);
	}
}

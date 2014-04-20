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
public class URLUtils {

	protected String input;
	protected URLSearchParams queryObject;
	protected ParserURL url;
	
	protected ParserURL baseUrl;
		
	public String getQueryEncoding() {
		return "UTF-8";
	}
	
	/*
	 * http://url.spec.whatwg.org/#dom-url-href
	 */
	@ScriptGetter
	public String getHref() {
		if (url == null) {
			return input;
		} else {
			return url.serialize();
		}
	}
	
	/*
	 * http://url.spec.whatwg.org/#dom-url-href
	 */
	@ScriptSetter
	public void setHref(String href) {
		setInput(href);
		
		Object contextObject = getContextObject();
		if (contextObject.getClass().equals(org.fit.cssbox.scriptbox.url.URL.class) && url == null) {
			throwTypeErrorException();
		}
		
		preUpdateSteps(href);
	}
	
	/*
	 * TODO: Implement
	 * http://url.spec.whatwg.org/#dom-url-origin
	 */
	@ScriptGetter
	public String getOrigin() {
		if (url == null) {
			return "";
		}
		
		return "";
	}
	
	/*
	 * http://url.spec.whatwg.org/#dom-url-protocol
	 */
	@ScriptGetter
	public String getProtocol() {
		if (url == null) {
			return ":";
		}
		
		return url.getScheme() + ":";
	}
	
	/*
	 * http://url.spec.whatwg.org/#dom-url-protocol
	 */
	@ScriptSetter
	public void setProtocol(String protocol) {
		if (url == null) {
			return;
		}
		
		BasicURLParser.getInstance().parse(protocol + ":", null, null, url, BasicURLParser.State.SCHEME_START);
		
		preUpdateSteps(null);
	}

	/*
	 * http://url.spec.whatwg.org/#dom-url-username
	 */
	@ScriptGetter
	public String getUsername() {
		if (url == null) {
			return "";
		}
		
		return url.getUsername();
	}
	
	/*
	 * http://url.spec.whatwg.org/#dom-url-username
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
	
	@ScriptGetter
	public String getPassword() {
		if (url == null) {
			return "";
		}
		
		return url.getPassword();
	}
	
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
	
	@ScriptSetter
	public void setHost(String host) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		BasicURLParser.getInstance().parse(host, null, null, url, BasicURLParser.State.HOST);
		
		preUpdateSteps(null);
	}
	
	@ScriptGetter
	public String getHostname() {
		if (url == null) {
			return "";
		}
		
		return url.serializedHost();
	}
	
	@ScriptSetter
	public void setHostname(String hostname) {
		if (url == null || !url.isRelativeFlag()) {
			return;
		}
		
		BasicURLParser.getInstance().parse(hostname, null, null, url, BasicURLParser.State.HOSTNAME);
		
		preUpdateSteps(null);
	}
	
	@ScriptGetter
	public String getPort() {
		if (url == null) {
			return "";
		}
		
		return url.getPort();
	}
	
	@ScriptSetter
	public void setPort(String port) {
		if (url == null || !url.isRelativeFlag() || url.getScheme().equalsIgnoreCase("file")) {
			return;
		}
		
		BasicURLParser.getInstance().parse(port, null, null, url, BasicURLParser.State.PORT);
		
		preUpdateSteps(null);
	}
	
	@ScriptGetter
	public String getPathname() {
		if (url == null) {
			return "";
		}
		
		if (!url.isRelativeFlag()) {
			return url.getSchemeData();
		}
		
		List<String> pathList = url.getPath();
		
		return StringUtils.join(pathList, "/");
	}
	
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
	@ScriptGetter
	public URLSearchParams getSearchParams() {
		return null;
	}
	
	/*
	 * TODO: Implement search params
	 * http://url.spec.whatwg.org/#dom-url-searchparams
	 */
	@ScriptSetter
	public void setSearchParams(URLSearchParams href) {
		
	}
	
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
	
	/*
	 * http://url.spec.whatwg.org/#concept-uu-set-the-input
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

	/*
	 * http://url.spec.whatwg.org/#concept-uu-get-the-base
	 */
	protected ParserURL getBase() {
		return baseUrl;
	}
	
	/*
	 * http://url.spec.whatwg.org/#concept-uu-get-the-base
	 */
	protected void setBaseUrl(ParserURL baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	/*
	 * http://url.spec.whatwg.org/#pre-update-steps
	 */
	public void preUpdateSteps(String value) {
		if (value == null) {
			value = url.serialize();
		}
		
		updateSteps(value);
	}
	
	/*
	 * http://url.spec.whatwg.org/#concept-uu-update
	 */
	public void updateSteps(String value) {
	}
	
	protected Object getContextObject() {
		return this;
	}
	
	protected void throwTypeErrorException() {
		throw new DOMException(DOMException.SYNTAX_ERR, "SyntaxError");
	}
}

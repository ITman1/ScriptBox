package org.fit.cssbox.scriptbox.url;

import java.util.List;

/**
 * Abstract ParserURL class for URLs constructed by a parser.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://url.spec.whatwg.org/#concept-url">URL</a>
 */
public abstract class ParserURL {
	protected boolean relativeFlag;
	protected Object associatedObject;
	
	/**
	 * Returns scheme component.
	 * 
	 * @return Scheme component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-scheme">Scheme</a>
	 */
	public abstract String getScheme();
	
	/**
	 * Sets scheme component.
	 * 
	 * @param scheme New scheme component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-scheme">Scheme</a>
	 */
	public abstract void setScheme(String scheme);
	
	/**
	 * Returns scheme data component.
	 * 
	 * @return Scheme data component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-scheme-data">Scheme data</a>
	 */
	public abstract String getSchemeData();
	
	/**
	 * Sets scheme data component.
	 * 
	 * @param schemeData Scheme data component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-scheme-data">Scheme data</a>
	 */
	public abstract void setSchemeData(String schemeData);

	/**
	 * Returns username component.
	 * 
	 * @return Username component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-username">Username</a>
	 */
	public abstract String getUsername();
	
	/**
	 * Sets username component.
	 * 
	 * @param username New username component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-username">Username</a>
	 */
	public abstract void setUsername(String username);

	/**
	 * Returns password component.
	 * 
	 * @return Password component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-password">Password</a>
	 */
	public abstract String getPassword();
	
	/**
	 * Sets password component.
	 * 
	 * @param password New password component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-password">Password</a>
	 */
	public abstract void setPassword(String password);

	/**
	 * Returns host component.
	 * 
	 * @return Host component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-host">Host</a>
	 */
	public abstract String getHost();
	
	/**
	 * Sets host component.
	 * 
	 * @param host New host component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-host">Host</a>
	 */
	public abstract void setHost(String host);

	/**
	 * Returns port component.
	 * 
	 * @return Port component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-port">Port</a>
	 */
	public abstract String getPort();
	
	/**
	 * Sets port component.
	 * 
	 * @param port New port component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-port">Port</a>
	 */
	public abstract void setPort(String port);

	/**
	 * Returns path component.
	 * 
	 * @return Path component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-path">Path</a>
	 */
	public abstract List<String> getPath();
	
	/**
	 * Sets path component.
	 * 
	 * @param port New path component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-path">Path</a>
	 */
	public abstract void setPath(List<String> path);

	/**
	 * Returns query component.
	 * 
	 * @return Query component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-query">Query</a>
	 */
	public abstract String getQuery();
	
	/**
	 * Sets query component.
	 * 
	 * @param query New query component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-query">Query</a>
	 */
	public abstract void setQuery(String query);

	/**
	 * Returns fragment component.
	 * 
	 * @return Fragment component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-fragment">Fragment</a>
	 */
	public abstract String getFragment();
	
	/**
	 * Sets fragment component.
	 * 
	 * @param fragment New query component.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-fragment">Fragment</a>
	 */
	public abstract void setFragment(String fragment);

	/**
	 * Constructs new Parser URL.
	 */
	public ParserURL() {
		this.relativeFlag = true;
	}
	
	/**
	 * Tests whether has set relative flag.
	 * 
	 * @return True if has relative flag set to true, otherwise false.
	 * @see <a href="http://url.spec.whatwg.org/#relative-flag">Relative flag</a>
	 */
	public boolean isRelativeFlag() {
		return relativeFlag;
	}

	/**
	 * Sets relative flag.
	 * 
	 * @param relativeFlag New relative flag value.
	 * @see <a href="http://url.spec.whatwg.org/#relative-flag">Relative flag</a>
	 */
	public void setRelativeFlag(boolean relativeFlag) {
		this.relativeFlag = relativeFlag;
	}

	/**
	 * Returns associated object.
	 * 
	 * @return Associated object.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-object">Associated object</a>
	 */
	public Object getAssociatedObject() {
		return associatedObject;
	}

	/**
	 * Sets associated object.
	 * 
	 * @param associatedObject New associated object.
	 * @see <a href="http://url.spec.whatwg.org/#concept-url-object">Associated object</a>
	 */
	public void setAssociatedObject(Object associatedObject) {
		this.associatedObject = associatedObject;
	}
	
	/**
	 * Serializes this URL.
	 * 
	 * @return Serialized URL.
	 * @see <a href="@see <a href="http://url.spec.whatwg.org/#concept-url-object">Associated object</a>
	 */
	public abstract String serialize();
	
	/**
	 * Tests whether has relative scheme.
	 * 
	 * @return True if has relative scheme, otherwise false.
	 */
	public boolean isRelativeScheme() {
		String scheme = getScheme();
		return isRelativeScheme(scheme);
	}
	
	/**
	 * Returns serialized host.
	 * 
	 * @return Serialized host.
	 * @see <a href="http://url.spec.whatwg.org/#concept-host-serializer">Serialized host</a>
	 */
	public String serializedHost() {
		return getHost();
	}
	
	/**
	 * Tests whether is passed scheme the relative scheme.
	 * 
	 * @return True if is passed scheme the relative scheme, otherwise false.
	 * @see <a href="http://url.spec.whatwg.org/#relative-scheme">Relative scheme</a>
	 */
	public static boolean isRelativeScheme(String scheme) {
		return true;
	}
	
	/**
	 * Returns default port for a given scheme.
	 * 
	 * @return Default port.
	 * @see <a href="http://url.spec.whatwg.org/#default-port">Default port</a>
	 */
	public static String defaultPort(String scheme) {
		return "80";
	}
}

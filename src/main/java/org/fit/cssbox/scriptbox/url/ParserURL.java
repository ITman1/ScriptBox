package org.fit.cssbox.scriptbox.url;

import java.util.List;

/*
 * TODO:
 * http://url.spec.whatwg.org/#concept-url
 */
public abstract class ParserURL {
	protected boolean relativeFlag;
	protected Object associatedObject;
	
	public abstract String getScheme();
	public abstract void setScheme(String scheme);
	
	public abstract String getSchemeData();
	public abstract void setSchemeData(String schemeData);

	public abstract String getUsername();
	public abstract void setUsername(String username);

	public abstract String getPassword();
	public abstract void setPassword(String password);

	public abstract String getHost();
	public abstract void setHost(String host);

	public abstract String getPort();
	public abstract void setPort(String port);

	public abstract List<String> getPath();
	public abstract void setPath(List<String> path);

	public abstract String getQuery();
	public abstract void setQuery(String query);

	public abstract String getFragment();
	public abstract void setFragment(String fragment);

	public ParserURL() {
		this.relativeFlag = true;
	}
	
	public boolean isRelativeFlag() {
		return relativeFlag;
	}

	public void setRelativeFlag(boolean relativeFlag) {
		this.relativeFlag = relativeFlag;
	}

	public Object getAssociatedObject() {
		return associatedObject;
	}

	public void setAssociatedObject(Object associatedObject) {
		this.associatedObject = associatedObject;
	}
	
	public abstract String serialize();
	
	public boolean isRelativeScheme() {
		String scheme = getScheme();
		return isRelativeScheme(scheme);
	}
	
	/*
	 * TODO: Serialized host
	 * http://url.spec.whatwg.org/#concept-host-serializer
	 */
	public String serializedHost() {
		return getHost();
	}
	
	/*
	 * TODO:
	 * http://url.spec.whatwg.org/#relative-scheme
	 */
	public static boolean isRelativeScheme(String scheme) {
		return true;
	}
	
	/*
	 * TODO:
	 * http://url.spec.whatwg.org/#default-port
	 */
	public static String defaultPort(String scheme) {
		return "80";
	}
}

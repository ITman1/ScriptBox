package org.fit.cssbox.scriptbox.url;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/*
 * TODO: The basic URL parser 
 * http://url.spec.whatwg.org/#concept-basic-url-parser
 */
public class BasicURLParser {
	static private BasicURLParser instance;
	
	public enum State {
		SCHEME_START,
		SCHEME,
		SCHEME_DATA,
		NO_SCHEME,
		RELATIVE_OR_AUTHORITY,
		RELATIVE,
		RELATIVE_SLASH,
		AUTHORITY_FIRST_SLASH,
		AUTHORITY_SECOND_SLASH,
		AUTHORITY_IGNORE_SLASHES,
		AUTHORITY,
		FILE_HOST,
		HOST,
		HOSTNAME,
		PORT,
		RELATIVE_PATH_START,
		RELATIVE_PATH,
		QUERY,
		FRAGMENT
	}
	
	public static synchronized BasicURLParser getInstance() {
		if (instance == null) {
			instance = new BasicURLParser();
		}
		
		return instance;
	}
	
	public ParserURL parse(String input) {
		return parse(input, null, null, null, null);
	}
	
	public ParserURL parse(String input, ParserURL base) {
		return parse(input, base, null, null, null);
	}
	
	public ParserURL parse(String input, ParserURL base, String encodingOverride) {
		return parse(input, base, encodingOverride, null, null);
	}
	
	public ParserURL parse(String input, ParserURL base, String encodingOverride, ParserURL url) {
		return parse(input, base, encodingOverride, url, null);
	}
	
	/*
	 * TODO: Implement correctly - not just only wrap java.net.URL
	 */
	public ParserURL parse(String input, ParserURL base, String encodingOverride, ParserURL url, State stateOverride) {
		if (url == null) {
			try {
				java.net.URL netUrl = null;
				if (base != null) {
					java.net.URL context = new java.net.URL(base.serialize());
					netUrl = new java.net.URL(context, input);
				} else {
					netUrl = new java.net.URL(input);
				}
				
				return new WrappedURL(netUrl);
			} catch (MalformedURLException e) {
				return null;
			}
		} else if (stateOverride != null && url instanceof WrappedURL) {
			WrappedURL wrappedUrl = (WrappedURL)url;
			switch (stateOverride) {
				case SCHEME_START:
					wrappedUrl.setScheme(input.substring(0, input.length() - 1));
					break;
				case HOSTNAME:
					wrappedUrl.setHost(input);
					break;
				case PORT:
					wrappedUrl.setPort(input);
					break;
				case RELATIVE_PATH_START:
					String[] pathArr = input.split("/");
					List<String> pathList = new ArrayList<String>();
					
					for (String pathElem : pathArr) {
						pathList.add(pathElem);
					}
					wrappedUrl.setPath(pathList);
					break;
				case QUERY:
					wrappedUrl.setQuery(input);
					break;
				case FRAGMENT:
					wrappedUrl.setFragment(input);
					break;
				default:
					break;
			}
		}
		
		return null;

	}
}

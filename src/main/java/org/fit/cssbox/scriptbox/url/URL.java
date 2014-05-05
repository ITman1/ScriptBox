package org.fit.cssbox.scriptbox.url;

import org.fit.cssbox.scriptbox.script.annotation.ScriptConstructor;

/**
 * Represents URL interface visible in scripts.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://url.spec.whatwg.org/#url">URL</a>
 */
public class URL extends URLUtils {
	
	/*
	 * For scripting purposes (script inject).
	 */
	public URL() {}
	
	/**
	 * Constructs URL from given URL string and default base URL address.
	 * 
	 * @param url URL
	 * @see <a href="http://url.spec.whatwg.org/#dom-url">URL</a>
	 */
	@ScriptConstructor
	public URL(String url) {
		this(url, "about:blank");
	}
	
	/**
	 * Constructs URL from given URL string and base URL address.
	 * 
	 * @param url URL
	 * @param base URL base address.
	 * @see <a href="http://url.spec.whatwg.org/#dom-url">URL</a>
	 */
	@ScriptConstructor
	public URL(String url, String base) {
		// 1) Basic URL parse base and set base to the result of that algorithm. 
		ParserURL baseUrl = BasicURLParser.getInstance().parse(base);
		
		// 2) If base is failure, throw a TypeError exception. 
		if (baseUrl == null) {
			throwTypeErrorException();
		}
		
		// 3) Let result be a new URL object. (nothing to do - we are in constructor)
		
		// 4) Let result's get the base return base. 
		setBaseUrl(baseUrl);
		
		// 5) Run result's set the input for url. 
		setInput(url);
		
		// 6) If result's url is null, throw a TypeError exception
		if (super.url == null) {
			throwTypeErrorException("\"" + url + "\" is not a valid URL.");
		}
	}
}

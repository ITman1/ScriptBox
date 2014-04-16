package org.fit.cssbox.scriptbox.url;

import org.fit.cssbox.scriptbox.script.annotation.ScriptConstructor;

/*
 * http://url.spec.whatwg.org/#dom-url
 */
public class URL extends URLUtils {
	
	/*
	 * For scripting purposes (script inject).
	 */
	public URL() {}
	
	@ScriptConstructor
	public URL(String url) {
		this(url, "about:blank");
	}
	
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
			throwTypeErrorException();
		}
	}
}

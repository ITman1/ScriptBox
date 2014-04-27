package org.fit.cssbox.scriptbox.url;

/*
 * TODO:
 * http://url.spec.whatwg.org/#concept-url-parser
 */
/**
 * Represents URL parser - parser that takes a string input, URL base, 
 * encoding override and then parses into Parser URL.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://url.spec.whatwg.org/#concept-url-parser">URL parser</a>
 */
public class URLParser {
	
	static private URLParser instance;
	
	public static synchronized URLParser getInstance() {
		if (instance == null) {
			instance = new URLParser();
		}
		
		return instance;
	}
	
	public ParserURL parse(String input) {
		return parse(input, null, null);
	}
	
	public ParserURL parse(String input, ParserURL base) {
		return parse(input, base, null);
	}
	
	/*
	 * TODO: Implement correctly - not just only wrap java.net.URL
	 */
	public ParserURL parse(String input, ParserURL base, String encodingOverride) {
		
		// 1) Let url be the result of running the basic URL parser 
		ParserURL url = BasicURLParser.getInstance().parse(input, base, encodingOverride);

		// 2) If url is failure, return failure. 
		if (url == null) {
			return null;
		}
		
		/*
		 * TODO: 3), 4) and 6)
		 * If url's scheme is not "blob", return url.
		 * If url's scheme data is not in the blob URL store, return url. [FILEAPI]
		 * Set url's object to a structured clone of the entry in the blob URL store corresponding to url's scheme data. [HTML] 
		 */
		
		return url;
	}

}

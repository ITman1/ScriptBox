/**
 * Handler.java
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

package org.fit.cssbox.scriptbox.url.about;


import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * Handler that enables us to use ABOUT protocol. 
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class Handler extends URLStreamHandler {
	/**
	 * Default address which has every document set.
	 */
	final public static String DEFAULT_URL_ADDRESS = "about:blank";
	
	/**
	 * Default URL address which has every document set by default.
	 */
	final public static URL DEFAULT_URL;
	
	static {
		URL defaultURL = null;
		try {
			defaultURL = new URL(DEFAULT_URL_ADDRESS);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		DEFAULT_URL = defaultURL;
	}
	
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		return new UserURLConnection(url);
	}
	
	private static class UserURLConnection extends URLConnection {
		public UserURLConnection(URL url) {
			super(url);
		}
		
		@Override
		public void connect() throws IOException {
		}
		
		@Override
		public InputStream getInputStream() throws IOException {
			return null;
		}
	}
}
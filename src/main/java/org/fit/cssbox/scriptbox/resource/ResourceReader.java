/**
 * ResourceReader.java
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

package org.fit.cssbox.scriptbox.resource;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

/**
 * Wraps reader and adds information about its origin URL.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class ResourceReader extends Reader {
	private Reader reader;
	private URL url;
	
	public ResourceReader(URL url, Reader reader) {
		this.reader = reader;
		this.url = url;
	}
	
	public ResourceReader(Resource resource, Reader reader) {
		this(resource.getAddress(), reader);
	}

	public URL getURL() {
		return url;
	}
	
	@Override
	public int read(char[] cbuf, int off, int len) throws IOException {
		return reader.read(cbuf, off, len);
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}

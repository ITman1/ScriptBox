/**
 * MimeContentFactoryBase.java
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

package org.fit.cssbox.scriptbox.misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all factories which creates contents for the specific MIME types.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class MimeContentFactoryBase<E> {
    private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();;

    /**
     * Tests if is given MIME type supported by this factory.
     * 
     * @param mimeType MEME type that should be tested.
     * @return True if is given MIME type supported, otherwise false.
     */
	public boolean isSupported(String mimeType) {
		List<String> supportedMimeTypes = getExplicitlySupportedMimeTypes();
		
		mimeType = mimeType.trim();
		
		for (String supportedMimeType : supportedMimeTypes) {
			if (supportedMimeType.equals(mimeType)) {
				return true;
			}
		}
		
		return isImplicitlySupported(mimeType);
	}
	
	/**
	 * Returns list of explicitly supported MIME types - types that can be statically determined.
	 * 
	 * @return List of explicitly supported MIME types.
	 */
	public List<String> getExplicitlySupportedMimeTypes() {
		return EMPTY_STRING_LIST;
	}
	
	/**
	 * Tests whether is the given MIME type an implicitly supported or not.
	 * This method does not have include the explicitly supported types.
	 * 
	 * @param mimeType MIME type of support depends on the form of this MIME type.
	 * 
	 * @return True if is given MIME type implicitly supported, otherwise false.
	 */
	public boolean isImplicitlySupported(String mimeType) {
		return false;
	}
	
	/**
	 * Constructs content for a given arguments.
	 * 
	 * @param args Arguments for constructing of the content.
	 * @return New constructed content, if arguments are valid, otherwise null.
	 */
	public E getContent(Object ...args) {
		return null;
	}
}

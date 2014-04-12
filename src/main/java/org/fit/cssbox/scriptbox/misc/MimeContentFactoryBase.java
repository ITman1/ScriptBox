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

public class MimeContentFactoryBase<E> {
    private static final List<String> EMPTY_STRING_LIST = new ArrayList<String>();;

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
	
	/*
	 * Explicitly supported MIME types
	 */
	public List<String> getExplicitlySupportedMimeTypes() {
		return EMPTY_STRING_LIST;
	}
	
	/*
	 * Supported according to the form of the MIME type
	 * It does not have to include the explicitly supported types.
	 */
	public boolean isImplicitlySupported(String mimeType) {
		return false;
	}
	
	public E getContent(Object ...args) {
		return null;
	}
}

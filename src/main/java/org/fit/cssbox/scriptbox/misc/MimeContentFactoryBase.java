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

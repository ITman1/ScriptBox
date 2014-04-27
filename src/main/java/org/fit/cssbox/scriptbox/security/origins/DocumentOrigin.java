/**
 * DocumentOrigin.java
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

package org.fit.cssbox.scriptbox.security.origins;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;

/**
 * Class for creating Document origins.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#origin-0">Origin</a>
 */
public class DocumentOrigin extends Origin<Html5DocumentImpl> {
	private Object _uniqueIdentifier;
	
	private DocumentOrigin(Html5DocumentImpl originDocument, Origin<?> alias, Object uniqueIdentifier) {
		super(originDocument, alias);
		
		_uniqueIdentifier = uniqueIdentifier;
	}
	
	/**
	 * Creates origin for the given document which is aliased from the given alias.
	 * 
	 * @param originDocument Document that owns the origin.
	 * @param alias Alias which is used for resolving.
	 * @return New constructed Document origin.
	 */
	public static DocumentOrigin create(Html5DocumentImpl originDocument, Origin<?> alias) {
		return new DocumentOrigin(originDocument, alias, null);
	}
	
	/**
	 * Creates unique origin for the given document, but with a given alias.
	 * 
	 * @param originDocument Document that owns the origin.
	 * @param alias Alias which is used for resolving.
	 * @return New constructed Document origin.
	 */
	public static DocumentOrigin createUnique(Html5DocumentImpl originDocument, Origin<?> alias) {
		return new DocumentOrigin(originDocument, alias, new Object());
	}
	
	/**
	 * Creates origin for the given document.
	 * 
	 * @param originDocument Document that owns the origin.
	 * @return New constructed Document origin.
	 */
	public static DocumentOrigin create(Html5DocumentImpl originDocument) {
		return create(originDocument, null);
	}
	
	
	/**
	 * Creates unique origin for the given document.
	 * 
	 * @param originDocument Document that owns the origin.
	 * @return New constructed Document origin.
	 */
	public static DocumentOrigin createUnique(Html5DocumentImpl originDocument) {
		return createUnique(originDocument, null);
	}

	@Override
	protected int originHashCode() {
		return new HashCodeBuilder(32, 10).
			append(_uniqueIdentifier).
			toHashCode();
	}

	@Override
	protected boolean originEquals(Origin<?> origin) {
		if (origin == null)
			return false;
		if (origin == this)
			return true;
		if (!(origin instanceof DocumentOrigin))
			return false;

		DocumentOrigin rhs = (DocumentOrigin) origin;
		return new EqualsBuilder().
			append(_uniqueIdentifier, rhs._uniqueIdentifier).
			isEquals();
	}

}

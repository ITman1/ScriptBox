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

public class DocumentOrigin extends Origin<Html5DocumentImpl> {
	private Object _uniqueIdentifier;
	
	private DocumentOrigin(Html5DocumentImpl originDocument, Origin<?> alias, Object uniqueIdentifier) {
		super(originDocument, alias);
		
		_uniqueIdentifier = uniqueIdentifier;
	}
	
	public static DocumentOrigin create(Html5DocumentImpl originDocument, Origin<?> alias) {
		return new DocumentOrigin(originDocument, alias, null);
	}
	
	public static DocumentOrigin createUnique(Html5DocumentImpl originDocument, Origin<?> alias) {
		return new DocumentOrigin(originDocument, alias, new Object());
	}
	
	public static DocumentOrigin create(Html5DocumentImpl originDocument) {
		return create(originDocument, null);
	}
	
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
	protected boolean originEquals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DocumentOrigin))
			return false;

		DocumentOrigin rhs = (DocumentOrigin) obj;
		return new EqualsBuilder().
			append(_uniqueIdentifier, rhs._uniqueIdentifier).
			isEquals();
	}

}

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

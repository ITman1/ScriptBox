package org.fit.cssbox.scriptbox.security.origins;

public abstract class Origin<E> {
	protected Origin<?> alias;
	protected E originSource;
	
	public Origin(E originSource) {
		this(originSource, null);
	}
	
	public Origin(E originSource, Origin<?> alias) {
		this.alias = alias;
	}
	
	public E getOriginSource() {
		return originSource;
	}
	
	public Origin<?> getEffectiveOrigin() {
		if (alias != null) {
			return alias;
		} else {
			return this;
		}
	}
	
	@Override
	public int hashCode() {
		if (alias != null) {
			return alias.hashCode();
		} else {
			return originHashCode();
		}
	}	

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Origin<?>) {
			Origin<?> cmpOrigin = (Origin<?>)obj;
			
			if (alias != null) {
				return alias.equals(obj);
			} else if (cmpOrigin.alias != null) {
				return equals(cmpOrigin.alias);
			} else {
				return originEquals(obj);
			}
		}
		
		return false;
	}
	
	protected abstract int originHashCode();
	protected abstract boolean originEquals(Object obj);
}

/**
 * Origin.java
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

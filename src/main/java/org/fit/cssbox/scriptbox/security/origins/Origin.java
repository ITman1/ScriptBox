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

/**
 * Abstract class for creating origins. Now we know only Document 
 * origin - {@link DocumentOrigin} and URL origin - {@link UrlOrigin}.
 *
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 * @see <a href="http://www.w3.org/html/wg/drafts/html/CR/browsers.html#origin-0">Origin</a>
 */
public abstract class Origin<E> {
	protected Origin<?> alias;
	protected E originSource;
	
	/**
	 * Constructs origin.
	 * 
	 * @param originSource Source of the origin, e.g. URL or Document.
	 */
	public Origin(E originSource) {
		this(originSource, null);
	}
	
	/**
	 * Constructs origin.
	 * 
	 * @param originSource Source of the origin, e.g. URL or Document.
	 * @param alias Reference to another origin, which will be
	 *        used for resolving instead of this origin.
	 */
	public Origin(E originSource, Origin<?> alias) {
		this.alias = alias;
	}
	
	/**
	 * Returns associated origin source.
	 * 
	 * @return Associated origin source.
	 */
	public E getOriginSource() {
		return originSource;
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
				return originEquals(cmpOrigin);
			}
		}
		
		return false;
	}
	
	/**
	 * Returns Hash code for this origin.
	 * 
	 * @return Hash code for this origin.
	 */
	protected abstract int originHashCode();
	
	/**
	 * Tests whether given origin equals to this origin.
	 * 
	 * @param origin Origin that should be tested.
	 * @return True if two origins equals, otherwise false.
	 */
	protected abstract boolean originEquals(Origin<?> origin);
}

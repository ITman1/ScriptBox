/**
 * OriginContainer.java
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

public class OriginContainer<E extends Origin<?>> {
	private E _origin;
	private E _effectiveScriptOrigin;
	
	public OriginContainer(E origin, E effectiveScriptOrigin) {
		_origin = origin;
		_effectiveScriptOrigin = effectiveScriptOrigin;
	}

	public E getOrigin() {
		return _origin;
	}

	public E getEffectiveScriptOrigin() {
		return _effectiveScriptOrigin;
	}
}

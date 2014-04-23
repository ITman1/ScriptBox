/**
 * ApplicationCache.java
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

package org.fit.cssbox.scriptbox.deprecied;

import org.fit.cssbox.scriptbox.script.java.ClassMember;
import org.mozilla.javascript.Wrapper;

public class ScriptableOriginWrapper implements Wrapper {
	
	protected Object wrapped;
	protected ClassMember<?> origin;
	
	public ScriptableOriginWrapper(Object wrapped, ClassMember<?> origin) {		
		this.wrapped = wrapped;
		this.origin = origin;
	}

	public ClassMember<?> getOrigin() {
		return origin;
	}

	@Override
	public Object unwrap() {
		return wrapped;
	}
}

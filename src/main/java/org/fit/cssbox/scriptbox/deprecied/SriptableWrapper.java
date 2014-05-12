/**
 * SriptableWrapper.java
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

import java.io.Serializable;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

public abstract class SriptableWrapper implements Scriptable, Serializable, Wrapper {
	private static final long serialVersionUID = -1287330352153930680L;
	
	protected Scriptable wrappedScriptable;

	public SriptableWrapper(Scriptable wrappedScriptable) {
		this.wrappedScriptable = wrappedScriptable;	
	}

	@Override
	public Object get(String name, Scriptable scope) {
		return wrappedScriptable.get(name, scope);
	}

	@Override
	public Object unwrap() {
		if (wrappedScriptable instanceof Wrapper) {
			return ((Wrapper) wrappedScriptable).unwrap();
		}
		
		return wrappedScriptable;
	}

	@Override
	public Object get(int index, Scriptable start) {
		return wrappedScriptable.get(index, start);
	}

	@Override
	public boolean has(String name, Scriptable start) {
		return wrappedScriptable.has(name, start);
	}

	@Override
	public boolean has(int index, Scriptable start) {
		return wrappedScriptable.has(index, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		wrappedScriptable.put(name, start, value);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		wrappedScriptable.put(index, start, value);
	}

	@Override
	public void delete(String name) {
		wrappedScriptable.delete(name);
	}

	@Override
	public void delete(int index) {
		wrappedScriptable.delete(index);
	}

	@Override
	public Scriptable getPrototype() {
		return wrappedScriptable.getPrototype();
	}

	@Override
	public void setPrototype(Scriptable prototype) {
		wrappedScriptable.setPrototype(prototype);
	}

	@Override
	public Scriptable getParentScope() {
		return wrappedScriptable.getParentScope();
	}

	@Override
	public void setParentScope(Scriptable parent) {
		wrappedScriptable.setParentScope(parent);
	}

	@Override
	public Object[] getIds() {
		return wrappedScriptable.getIds();
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		return wrappedScriptable.getDefaultValue(hint);
	}

	@Override
	public boolean hasInstance(Scriptable instance) {
		return wrappedScriptable.hasInstance(instance);
	}
}

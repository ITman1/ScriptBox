/**
 * WrapFactoryDecorator.java
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

package org.fit.cssbox.scriptbox.script.javascript.wrap;

import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class WrapFactoryDecorator extends WrapFactory {
	protected WrapFactoryDecorator parentDecorator;
	protected WrapFactoryDecorator childDecorator;
	
	public WrapFactoryDecorator(WrapFactoryDecorator childDecorator) {
		this.childDecorator = childDecorator;
		
		if (childDecorator != null) {
			childDecorator.bindParentDecorator(this);
		}
	}
	
	public WrapFactoryDecorator() {
		this(null);
	}
		
	public WrapFactoryDecorator getParentDecorator() {
		return parentDecorator;
	}
	
	public WrapFactoryDecorator getChildDecorator() {
		return parentDecorator;
	}
	
	public WrapFactoryDecorator getFirstDecorator() {
		WrapFactoryDecorator topLevelDecorator = this;
		
		while (topLevelDecorator.parentDecorator != null) {
			topLevelDecorator = topLevelDecorator.parentDecorator;
		}
		
		return topLevelDecorator;
	}
	
	public WrapFactoryDecorator getLastDecorator() {
		WrapFactoryDecorator decorator = this;
		
		while (decorator.childDecorator != null) {
			decorator = decorator.childDecorator;
		}
		
		return decorator;
	}
	
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return childWrap(cx, scope, obj, staticType);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return childWrapAsJavaObject(cx, scope, javaObject, staticType);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
		return childWrapJavaClass(cx, scope, javaClass);
	}
	
	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		return childWrapNewObject(cx, scope, obj);
	}
	
	public Object childWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return (childDecorator != null)? childDecorator.wrap(cx, scope, obj, staticType) : null;
	}
	
	public Scriptable childWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return (childDecorator != null)? childDecorator.wrapAsJavaObject(cx, scope, javaObject, staticType) : null;
	}
	
	public Scriptable childWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
		return (childDecorator != null)? childDecorator.wrapJavaClass(cx, scope, javaClass) : null;
	}
	
	public Scriptable childWrapNewObject(Context cx, Scriptable scope, Object obj) {
		return (childDecorator != null)? childDecorator.wrapNewObject(cx, scope, obj) : null;
	}
	
	public Object topWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return getFirstDecorator().wrap(cx, scope, obj, staticType);
	}
	
	public Scriptable topWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return getFirstDecorator().wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
	
	public Scriptable topWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
		return getFirstDecorator().wrapJavaClass(cx, scope, javaClass);
	}
	
	public Scriptable topWrapNewObject(Context cx, Scriptable scope, Object obj) {
		return getFirstDecorator().wrapNewObject(cx, scope, obj);
	}
	
	protected void bindParentDecorator(WrapFactoryDecorator decorator) {
		if (parentDecorator != null) {
			throw new InternalException("Wrap Factory Decorator should have always only one parent decorator!");
		}
		
		parentDecorator = decorator;
	}
}

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

import org.fit.cssbox.scriptbox.script.exceptions.InternalException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * Base class for classes that represent decoratable wrap factories.
 * Wrap factory decorators can be chained and this chain acts then as it was single wrap factory.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class WrapFactoryDecorator extends WrapFactory {
	protected WrapFactoryDecorator parentDecorator;
	protected WrapFactoryDecorator childDecorator;
	
	/**
	 * Constructs new wrap decorator and adds it into chain.
	 * 
	 * @param childDecorator Decorator where it should be chained.
	 */
	public WrapFactoryDecorator(WrapFactoryDecorator childDecorator) {
		this.childDecorator = childDecorator;
		
		if (childDecorator != null) {
			childDecorator.bindParentDecorator(this);
		}
	}
	
	/**
	 * Constructs leaf wrap factory decorator, this decorator does not have any child decorator.
	 */
	public WrapFactoryDecorator() {
		this(null);
	}
		
	/**
	 * Returns parent decorator of the chain.
	 * 
	 * @return Parent decorator of the chain.
	 */
	public WrapFactoryDecorator getParentDecorator() {
		return parentDecorator;
	}
	
	/**
	 * Returns child decorator of the chain.
	 * 
	 * @return Child decorator of the chain.
	 */
	public WrapFactoryDecorator getChildDecorator() {
		return parentDecorator;
	}
	
	/**
	 * Returns first decorator of the chain.
	 * 
	 * @return First decorator of the chain.
	 */
	public WrapFactoryDecorator getFirstDecorator() {
		WrapFactoryDecorator topLevelDecorator = this;
		
		while (topLevelDecorator.parentDecorator != null) {
			topLevelDecorator = topLevelDecorator.parentDecorator;
		}
		
		return topLevelDecorator;
	}
	
	/**
	 * Returns the last decorator of the chain.
	 * 
	 * @return The last decorator of the chain.
	 */
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
	
	/**
	 * Uses child decorator wrap.
	 * 
	 * @see #wrap(Context, Scriptable, Object, Class)
	 */
	public Object childWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return (childDecorator != null)? childDecorator.wrap(cx, scope, obj, staticType) : null;
	}
	
	/**
	 * Uses child decorator wrap.
	 * 
	 * @see #wrapAsJavaObject(Context, Scriptable, Object, Class)
	 */
	public Scriptable childWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return (childDecorator != null)? childDecorator.wrapAsJavaObject(cx, scope, javaObject, staticType) : null;
	}
	
	/**
	 * Uses child decorator wrap.
	 * 
	 * @see #wrapJavaClass(Context, Scriptable, Class)
	 */
	public Scriptable childWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
		return (childDecorator != null)? childDecorator.wrapJavaClass(cx, scope, javaClass) : null;
	}
	
	/**
	 * Uses child decorator wrap.
	 * 
	 * @see #wrapNewObject(Context, Scriptable, Object)
	 */
	public Scriptable childWrapNewObject(Context cx, Scriptable scope, Object obj) {
		return (childDecorator != null)? childDecorator.wrapNewObject(cx, scope, obj) : null;
	}
	
	/**
	 * Uses first decorator of the chain.
	 * 
	 * @see #wrap(Context, Scriptable, Object, Class)
	 */
	public Object topWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return getFirstDecorator().wrap(cx, scope, obj, staticType);
	}
	
	/**
	 * Uses first decorator of the chain.
	 * 
	 * @see #wrapAsJavaObject(Context, Scriptable, Object, Class)
	 */
	public Scriptable topWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		return getFirstDecorator().wrapAsJavaObject(cx, scope, javaObject, staticType);
	}
	
	/**
	 * Uses first decorator of the chain.
	 * 
	 * @see #wrapJavaClass(Context, Scriptable, Class)
	 */
	public Scriptable topWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
		return getFirstDecorator().wrapJavaClass(cx, scope, javaClass);
	}
	
	/**
	 * Uses first decorator of the chain.
	 * 
	 * @see #wrapNewObject(Context, Scriptable, Object)
	 */
	public Scriptable topWrapNewObject(Context cx, Scriptable scope, Object obj) {
		return getFirstDecorator().wrapNewObject(cx, scope, obj);
	}
	
	/**
	 * Adds parent decorator to this decorator.
	 * 
	 * @param decorator Decorator to be added as a parent decorator.
	 */
	protected void bindParentDecorator(WrapFactoryDecorator decorator) {
		if (parentDecorator != null) {
			throw new InternalException("Wrap Factory Decorator should have always only one parent decorator!");
		}
		
		parentDecorator = decorator;
	}
}

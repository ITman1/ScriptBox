/**
 * DefaultWrapFactoryDecorator.java
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

import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaObject;

import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultObjectMembers;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

/**
 * Default wrap factory decorator which wraps Java object using the {@link HostedJavaObject}
 * and class members resolver.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class DefaultWrapFactoryDecorator extends WrapFactoryDecorator {
	private class DecoratedWrapFactory extends WrapFactory {
		@Override
		public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
			return topWrap(cx, scope, obj, staticType);
		}
		
		@Override
		public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
			return topWrapAsJavaObject(cx, scope, javaObject, staticType);
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
			return topWrapJavaClass(cx, scope, javaClass);
		}
		
		@Override
		public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
			return topWrapNewObject(cx, scope, obj);
		}
		
		public Object decoratorWrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
			return super.wrap(cx, scope, obj, staticType);
		}
		
		public Scriptable decoratorWrapJavaClass(Context cx, Scriptable scope, Class<?> javaClass) {
			return super.wrapJavaClass(cx, scope, javaClass);
		}
		
		public Scriptable decoratorWrapNewObject(Context cx, Scriptable scope, Object obj) {
			return super.wrapNewObject(cx, scope, obj);
		}
	}
	
	protected DecoratedWrapFactory factory;
	protected ClassMembersResolverFactory membersResolverFactory;
	
	/**
	 * Constructs leaf wrap factory decorator using the default class members resolver. 
	 */
	public DefaultWrapFactoryDecorator() {
		this(null, null);
	}
	
	/**
	 * Constructs new wrap factory decorator and chains the passed decorator.
	 * 
	 * @param decorator Decorator the be added as a child decorator and chained.
	 */
	public DefaultWrapFactoryDecorator(WrapFactoryDecorator decorator) {
		this(decorator, null);
	}
	
	/**
	 * Constructs leaf wrap factory decorator.
	 * 
	 * @param membersResolverFactory Members resolver factory used for wrapping of the Java object.
	 */
	public DefaultWrapFactoryDecorator(ClassMembersResolverFactory membersResolverFactory) {
		this(null, membersResolverFactory);
	}
	
	/**
	 * Constructs new wrap factory decorator and chains the passed decorator.
	 * 
	 * @param decorator Decorator the be added as a child decorator and chained.
	 * @param membersResolverFactory Members resolver factory used for wrapping of the Java object.
	 */
	public DefaultWrapFactoryDecorator(WrapFactoryDecorator decorator, ClassMembersResolverFactory membersResolverFactory) {
		super(decorator);
		
		this.membersResolverFactory = (membersResolverFactory == null)? new DefaultClassMembersResolverFactory() : membersResolverFactory;
		
		factory = new DecoratedWrapFactory();
		factory.setJavaPrimitiveWrap(false);
	}
	
	@Override
	public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
		return factory.decoratorWrap(cx, scope, obj, staticType);
	}
	
	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		DefaultObjectMembers objectMembers = DefaultObjectMembers.getObjectMembers(javaObject, membersResolverFactory);
		return new HostedJavaObject(scope, objectMembers);
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Scriptable wrapJavaClass(Context cx, Scriptable scope, Class javaClass) {
		return factory.decoratorWrapJavaClass(cx, scope, javaClass);
	}
	
	@Override
	public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
		return factory.decoratorWrapNewObject(cx, scope, obj);
	}
}

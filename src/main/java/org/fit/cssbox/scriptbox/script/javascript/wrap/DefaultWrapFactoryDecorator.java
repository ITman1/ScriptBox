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

import org.fit.cssbox.scriptbox.script.java.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.DefaultClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.java.ObjectMembers;
import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

public class DefaultWrapFactoryDecorator extends WrapFactoryDecorator {
	protected class DecoratedWrapFactory extends WrapFactory {
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
		
		public Scriptable decoratorWrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
			return super.wrapAsJavaObject(cx, scope, javaObject, staticType);
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
	
	public DefaultWrapFactoryDecorator() {
		this(null, null);
	}
	
	public DefaultWrapFactoryDecorator(WrapFactoryDecorator decorator) {
		this(decorator, null);
	}
	
	public DefaultWrapFactoryDecorator(ClassMembersResolverFactory membersResolverFactory) {
		this(null, membersResolverFactory);
	}
	
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
		ObjectMembers objectMembers = ObjectMembers.getObjectMembers(javaObject, membersResolverFactory);
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

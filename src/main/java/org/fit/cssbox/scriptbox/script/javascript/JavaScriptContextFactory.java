/**
 * JavaScriptContextFactory.java
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

package org.fit.cssbox.scriptbox.script.javascript;

import org.fit.cssbox.scriptbox.dom.events.adapters.EventAdapter;
import org.fit.cssbox.scriptbox.dom.events.adapters.MouseEventAdapter;
import org.fit.cssbox.scriptbox.dom.events.adapters.UIEventAdapter;
import org.fit.cssbox.scriptbox.script.adapter.AdapterRegistry;
import org.fit.cssbox.scriptbox.script.javascript.wrap.AdapterWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.ErrorAdapter;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;

/**
 * Context factory that is used for all JavaScript engines.
 * This context should be associated with the JavaScriptEngine 
 * that is unique for one thread, because there should be one context per one thread.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptContextFactory extends ContextFactory {
	
	protected WindowJavaScriptEngine scriptEngine;
	protected ClassMembersResolverFactory membersResolverFactory;
	protected AdapterRegistry adapterRegistry;
	
	/*public JavaScriptContextFactory() {
		this(new WindowJavaScriptEngine(null, null));
	}*/
	
	/**
	 * Constructs new JavaScript context with the associated script engine.
	 * 
	 * @param scriptEngine Script engine to be associated with this context factory.
	 */
	public JavaScriptContextFactory(WindowJavaScriptEngine scriptEngine) {
		this.scriptEngine = scriptEngine;
		this.membersResolverFactory = scriptEngine.getClassMembersResolverFactory();
		this.adapterRegistry = new AdapterRegistry();

		this.adapterRegistry.registerAdapter(EventAdapter.class);
		this.adapterRegistry.registerAdapter(UIEventAdapter.class);
		this.adapterRegistry.registerAdapter(MouseEventAdapter.class);
		this.adapterRegistry.registerAdapter(ErrorAdapter.class);
	}
	
	@Override
	public boolean hasFeature(Context cx, int feature) {
		if (feature == Context.FEATURE_E4X) {
			return false;
		} else {
			return super.hasFeature(cx, feature);
		}
	}
	
	@Override
	protected Context makeContext() {
		Context cx = super.makeContext();
		
		WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator(membersResolverFactory);
		wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);
		wrapFactoryDecorator = new AdapterWrapFactoryDecorator(adapterRegistry, wrapFactoryDecorator);

		cx.setWrapFactory(wrapFactoryDecorator);
		
		return cx;
	}
}

/**
 * CollectionsWrapFactoryTests.java
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

package tests.script.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultClassMembersResolverFactory;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.TestUtils.AbstractGlobalObjectScriptEngineFactory;
import tests.script.TestUtils.GlobalObjectScriptEngineTester;

/**
 * Tests wrapper that wraps native Java collections.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class CollectionsWrapFactoryTests {	
	
	public static class CollectionsContextFactory extends ContextFactory {
		
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
			wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}
	
	public static class CollectionsWrapFactoryEngineFactory extends AbstractGlobalObjectScriptEngineFactory {

		@Override
		public BrowserScriptEngine getScriptEngine(final Object object) {
			return new WindowJavaScriptEngine(null, null, new CollectionsContextFactory()) {
				@Override
				protected TopLevel initializeTopLevel() {
					return new ObjectTopLevel(object, this);
				}
				
				@Override
				protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
					return new DefaultClassMembersResolverFactory();
				}
			};
		}

		
	};
	
	public static class CollectionsContainer {
		public Map<String, String> foobarMap = new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put("foo", "bar");
			}
		};
		
		public List<String> foobarList = new ArrayList<String>() {
			private static final long serialVersionUID = 1L;
			{
				add("foobar");
			}
		};
	}
	
	private static GlobalObjectScriptEngineTester<CollectionsContainer> tester;
	private static CollectionsContainer globalObject;

	static {
		CollectionsWrapFactoryEngineFactory engineFactory = new CollectionsWrapFactoryEngineFactory();
		
		globalObject = new CollectionsContainer();
		tester = new GlobalObjectScriptEngineTester<CollectionsContainer>(engineFactory, globalObject);
	}
		
	@Test
	public void TestNativeJavaObjectCollections() {
		tester.assertEquals("foobarMap['foo']", "bar");
		tester.assertEquals("foobarList[0]", "foobar");
		tester.assertUndefined("foobarList['str']");
		tester.assertUndefined("foobarList[1]");
	}
	
}

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

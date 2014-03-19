package tests.script.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestUtils.AbstractGlobalObjectScriptEngineFactory;
import tests.script.engine.TestUtils.GlobalObjectScriptEngineTester;

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
			return new GlobalObjectJavaScriptEngine(null, null, new CollectionsContextFactory()) {
				@Override
				protected TopLevel initializeTopLevel() {
					return new ObjectTopLevel(object, this);
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
	public void TestNativeJavaObjectCollections() throws ScriptException {
		tester.assertEquals("foobarMap['foo']", "bar");
		tester.assertEquals("foobarList[0]", "foobar");
		tester.assertNull("foobarList['str']");
		tester.assertNull("foobarList[1]");
	}
	
}

package tests.script.engine;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.wrap.CollectionsWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestClasses.NestedObjectWithGetter;
import tests.script.engine.TestClasses.CollectionsContainer;

public class CollectionsWrapFactoryTests {
	public class CollectionsContextFactory extends ContextFactory {
				
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
			wrapFactoryDecorator = new CollectionsWrapFactoryDecorator(wrapFactoryDecorator);

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}
	
	@Test
	public void TestNativeJavaObjectGetter() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getCollectionsScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('getFoo', publicNestedObject['foo']);");
		engine.eval("retValues.put('get0', publicNestedObject[0]);");
		
		assertEquals("bar", retValues.get("getFoo"));
		assertEquals((Integer)retValues.get("get0"), new Integer(1));
	}
	
	@Test
	public void TestNativeJavaObjectCollections() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		CollectionsContainer globalObject = new CollectionsContainer();
		JavaScriptEngine engine = getCollectionsScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('getFoo', foobarMap['foo']);");
		engine.eval("retValues.put('get0', foobarList[0]);");
		engine.eval("retValues.put('getStr0', foobarList['str']);");
		engine.eval("retValues.put('get1', foobarList[1]);");
		
		assertEquals("bar", retValues.get("getFoo"));
		assertEquals("foobar", retValues.get("get0"));
		assertEquals("undefined", retValues.get("getStr0"));
		assertEquals("undefined", retValues.get("get1"));
	}

	protected JavaScriptEngine getCollectionsScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null, new CollectionsContextFactory()) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ObjectTopLevel(object, this);
			}
		};
	}
	
}

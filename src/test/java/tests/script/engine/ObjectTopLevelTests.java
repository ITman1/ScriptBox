package tests.script.engine;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectTopLevel;
import org.junit.Test;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

public class ObjectTopLevelTests {
	
	public class TestGlobalObject implements ObjectGetter {
		public String publicProperty = "public property";
		protected String protectedProperty = "protected property";
		private String privateProperty = "private property";
		
		public TestGlobalObject publicNestedObject;
		public TestGlobalObject publicNestedObjectWithGetter;
		private TestGlobalObject privateNestedObjectWithGetter;
		
		public TestGlobalObject(int nestLevel) {
			if (nestLevel > 0) {
				publicNestedObject = new TestGlobalObject(nestLevel - 1);
				publicNestedObjectWithGetter = new TestGlobalObject(nestLevel - 1);
				privateNestedObjectWithGetter = new TestGlobalObject(nestLevel - 1);
			}
		}
		
		public TestGlobalObject getPublicNestedObjectWithGetter() {
			return privateNestedObjectWithGetter;
		}
		
		public TestGlobalObject getPrivateNestedObjectWithGetter() {
			return privateNestedObjectWithGetter;
		}
		
		public String getConcat() {
			return "";
		}
		
		public String getConcat(String arg1) {
			return arg1;
		}
		
		public String getConcat(String arg1, String arg2) {
			return arg1 + arg2;
		}

		@Override
		public Object get(Object arg) {
			if (arg instanceof String) {
				if (((String)arg).equals("foo")) {
					return "bar";
				}
			}
			return ObjectGetter.UNDEFINED_VALUE;
		}
	}
	
	@Test
	public void TestEnumerableProperties() throws ScriptException {
		Map<String, Object> properties = new HashMap<String, Object>();
		TestGlobalObject globalObject = new TestGlobalObject(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("properties", properties);
    	engine.eval("for(var propName in this) {properties.put(propName, this[propName]);}");
    	
    	// Test for number of enumerable properties
    	// There are 5 from from top level, "propName" defined in JavaScript and properties binded via JSR 223
    	assertEquals(properties.size(), 9);
    	
    	/* Test for equality of all properties which have instances in Java */
    	
    	// Top level properties
    	assertEquals(globalObject.publicProperty, properties.get("publicProperty"));
    	assertEquals(globalObject.publicNestedObject, properties.get("publicNestedObject"));
    	assertEquals(globalObject.getPublicNestedObjectWithGetter(), properties.get("publicNestedObjectWithGetter"));
    	assertEquals(globalObject.getPrivateNestedObjectWithGetter(), properties.get("privateNestedObjectWithGetter"));
    	assertEquals("", properties.get("concat"));
    	assertEquals(globalObject.getClass(), properties.get("class"));
    	
    	// Binded property via Scripting API
    	assertEquals(properties, properties.get("properties"));
	}
	
	@Test
	public void TestFunctionProperties() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		TestGlobalObject globalObject = new TestGlobalObject(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("globalObject", globalObject);
		engine.put("retValues", retValues);
    	engine.eval("retValues.put('getConcat1', getConcat('foo'));");
    	engine.eval("retValues.put('getConcat2', getConcat('foo', 'bar'));");
    	engine.eval("retValues.put('equals', equals(globalObject));");
    	engine.eval("retValues.put('hashCode', hashCode());");
    	
    	assertEquals("foo", retValues.get("getConcat1"));
    	assertEquals("foobar", retValues.get("getConcat2"));
    	assertEquals(true, retValues.get("equals"));
    	assertEquals(Math.abs(globalObject.hashCode() - (Double)retValues.get("hashCode")) < 1.0, true);
	}
	
	@Test
	public void TestObjectGetter() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		TestGlobalObject globalObject = new TestGlobalObject(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("retValues", retValues);
		engine.eval("retValues.put('get', this['foo']);");
		
		assertEquals("bar", retValues.get("get"));
	}
	
	protected JavaScriptEngine getObjectTopLevelScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null, new ContextFactory()) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ObjectTopLevel(object, this);
			}
		};
	}
}

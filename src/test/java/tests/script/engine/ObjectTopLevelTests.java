package tests.script.engine;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.junit.Test;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestClasses.NestedObjectWithGetter;

public class ObjectTopLevelTests {
	
	@Test
	public void TestEnumerableProperties() throws ScriptException {
		Map<String, Object> properties = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("properties", properties);
    	engine.eval("for(var propName in this) {properties.put(propName, this[propName]);}");
    	
    	// Test for number of enumerable properties
    	// There are 5 from from top level, "propName" defined in JavaScript and properties binded via JSR 223
    	assertEquals(properties.size(), 11);
    	
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
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
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
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("retValues", retValues);
		engine.eval("retValues.put('getFoo', this['foo']);");
		engine.eval("retValues.put('get0', this[0]);");
		
		assertEquals("bar", retValues.get("getFoo"));
		assertEquals(((Double)retValues.get("get0")) - 1 < 1e-1, true);
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

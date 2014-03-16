package tests.script.engine;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.junit.Test;
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;

import tests.script.engine.TestClasses.NestedObjectWithGetter;

@RunWith(JUnit4.class)
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
    	assertEquals(properties.size(), 23);
    	
    	/* Test for equality of all properties which have instances in Java */
    	
    	// Test some enumerated top level properties
    	assertEquals(globalObject.publicProperty, properties.get("publicProperty"));
    	assertEquals(globalObject.publicNestedObject, properties.get("publicNestedObject"));
    	assertEquals(globalObject.getPublicNestedObjectWithGetter(), properties.get("publicNestedObjectWithGetter"));
    	assertEquals(globalObject.getPrivateNestedObjectWithGetter(), properties.get("privateNestedObjectWithGetter"));
    	assertEquals("", properties.get("concat"));
    	assertEquals(globalObject.getClass(), properties.get("class"));
    	
    	// Binded property via Scripting API
    	assertEquals(properties, engine.get("properties"));
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
    	assertEquals(globalObject.hashCode(), retValues.get("hashCode"));
	}
	
	@Test
	public void TestObjectProperties() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('publicNestedObject', publicNestedObject);");
		engine.eval("retValues.put('publicProperty', publicProperty);");
		engine.eval("retValues.put('publicNestedObjectWithGetter', publicNestedObjectWithGetter);");
		
		assertEquals(globalObject.publicNestedObject, retValues.get("publicNestedObject"));
		assertEquals(globalObject.publicProperty, retValues.get("publicProperty"));
		assertEquals(globalObject.publicNestedObjectWithGetter, retValues.get("publicNestedObjectWithGetter"));
		
		NestedObjectWithGetter newNestedObject = new NestedObjectWithGetter(1);
		String newPublicProperty = "new property";
		engine.put("newNestedObject", newNestedObject);
		engine.put("newPublicProperty", newPublicProperty);
		engine.eval("publicNestedObject = newNestedObject;");
		engine.eval("publicProperty = newPublicProperty;");
		engine.eval("publicNestedObjectWithGetter = newNestedObject;");
		
		assertEquals(globalObject.publicNestedObject, newNestedObject);
		assertEquals(globalObject.publicProperty, newPublicProperty);
		assertEquals(globalObject.publicNestedObjectWithGetter, newNestedObject);
		
		engine.eval("retValues.put('staticProperty', this['METHOD_NAME']);");
		assertEquals(Undefined.instance, retValues.get("staticProperty"));
	}
	
	@Test
	public void TestObjectGetter() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('getFoo', this['foo']);");
		engine.eval("retValues.put('get0', this[0]);");
		engine.eval("retValues.put('getStr0', this['str']);");
		engine.eval("retValues.put('get1', this[1]);");
		
		assertEquals("bar", retValues.get("getFoo"));
		assertEquals(retValues.get("get0"), 1);
		assertEquals(Undefined.instance, retValues.get("getStr0"));
		assertEquals(Undefined.instance, retValues.get("get1"));
	}
	
	@Test
	public void TestPropertyGetter() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('class', class);");
		engine.eval("retValues.put('concat', concat);");
		engine.eval("retValues.put('publicNestedObjectWithGetter', publicNestedObjectWithGetter);");
		engine.eval("retValues.put('privateNestedObjectWithGetter', privateNestedObjectWithGetter);");
		
		assertEquals(globalObject.getClass(), retValues.get("class"));
		assertEquals(globalObject.getConcat(), retValues.get("concat"));
    	assertEquals(globalObject.getPublicNestedObjectWithGetter(), retValues.get("publicNestedObjectWithGetter"));
    	assertEquals(globalObject.getPrivateNestedObjectWithGetter(), retValues.get("privateNestedObjectWithGetter"));
    	
    	// Getter classes should be defined also...
   		engine.eval("getClass();");
   		engine.eval("getConcat();");
   		engine.eval("getPublicNestedObjectWithGetter();");
   		engine.eval("getPrivateNestedObjectWithGetter();");

    	//test that getter is has no priority before direct get
    	assertTrue(globalObject.duplicatedPublicStringProperty != null);
		engine.eval("publicStringProperty;");
		assertTrue(globalObject.duplicatedPublicStringProperty != null);
	}
	
	@Test
	public void TestPropertySetter() throws ScriptException {
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		NestedObjectWithGetter newNestedObject = new NestedObjectWithGetter(1);

		engine.put("newNestedObject", newNestedObject);
		engine.eval("privateNestedObjectWithGetter = newNestedObject;");

		assertEquals(globalObject.getPrivateNestedObjectWithGetter(), newNestedObject);
		
    	//test that setter is has no priority before direct set
    	assertFalse(globalObject.duplicatedPublicStringProperty.equals("new property"));
		engine.eval("publicStringProperty = 'new property';");
		assertFalse(globalObject.duplicatedPublicStringProperty.equals("new property"));
	}
	
	protected JavaScriptEngine getObjectTopLevelScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null, new TestContextFactories.DefaultContextFactory()) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ObjectTopLevel(object, this);
			}
		};
	}
}

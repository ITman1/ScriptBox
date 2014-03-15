
package tests.script.engine;
import static org.junit.Assert.*;

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
import org.junit.runners.JUnit4;
import org.junit.runner.RunWith;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestClasses.NestedObjectWithGetter;

/*
 * This should test that created scriptable acts the same way as a object top level, so tests are similar or the same.
 */
@RunWith(JUnit4.class)
public class DefaultWrapFactoryTests {

	public class DefaultContextFactory extends ContextFactory {
		
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();

			cx.setWrapFactory(wrapFactoryDecorator);
			
			return cx;
		}
	}

	@Test
	public void TestEnumerableProperties() throws ScriptException {
		Map<String, Object> properties = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		
		engine.put("properties", properties);
    	engine.eval("for(var propName in publicNestedObject) {properties.put(propName, this[propName]);}");
    	
    	// Test for number of enumerable properties
    	// There are 5 from from top level, "propName" defined in JavaScript and properties binded via JSR 223
    	assertEquals(properties.size(), 21);
    	
    	/* Test for equality of all properties which have instances in Java */
    	
    	// Top level properties
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

		engine.put("eqObject", globalObject.publicNestedObject);
		engine.put("retValues", retValues);
    	engine.eval("retValues.put('getConcat1', publicNestedObject.getConcat('foo'));");
    	engine.eval("retValues.put('getConcat2', publicNestedObject.getConcat('foo', 'bar'));");
    	engine.eval("retValues.put('equals', publicNestedObject.equals(eqObject));");
    	engine.eval("retValues.put('hashCode', publicNestedObject.hashCode());");
    	
    	assertEquals("foo", retValues.get("getConcat1"));
    	assertEquals("foobar", retValues.get("getConcat2"));
    	assertEquals(true, retValues.get("equals"));
    	assertEquals(Math.abs(globalObject.publicNestedObject.hashCode() - (Double)retValues.get("hashCode")) < 1.0, true);
	}
	
	@Test
	public void TestObjectProperties() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('publicNestedObject', publicNestedObject.publicNestedObject);");
		engine.eval("retValues.put('publicProperty', publicNestedObject.publicProperty);");
		engine.eval("retValues.put('publicNestedObjectWithGetter', publicNestedObject.publicNestedObjectWithGetter);");
		
		assertEquals(globalObject.publicNestedObject.publicNestedObject, retValues.get("publicNestedObject"));
		assertEquals(globalObject.publicNestedObject.publicProperty, retValues.get("publicProperty"));
		assertEquals(globalObject.publicNestedObject.publicNestedObjectWithGetter, retValues.get("publicNestedObjectWithGetter"));
		
		NestedObjectWithGetter newNestedObject = new NestedObjectWithGetter(1);
		String newPublicProperty = "new property";
		engine.put("newNestedObject", newNestedObject);
		engine.put("newPublicProperty", newPublicProperty);
		engine.eval("publicNestedObject.publicNestedObject = newNestedObject;");
		engine.eval("publicNestedObject.publicProperty = newPublicProperty;");
		engine.eval("publicNestedObject.publicNestedObjectWithGetter = newNestedObject;");
		
		assertEquals(globalObject.publicNestedObject.publicNestedObject, newNestedObject);
		assertEquals(globalObject.publicNestedObject.publicProperty, newPublicProperty);
		assertEquals(globalObject.publicNestedObject.publicNestedObjectWithGetter, newNestedObject);
	}
	
	@Test
	public void TestPropertyGetter() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);

		engine.put("retValues", retValues);
		engine.eval("retValues.put('class', publicNestedObject.class);");
		engine.eval("retValues.put('concat', publicNestedObject.concat);");
		engine.eval("retValues.put('publicNestedObjectWithGetter', publicNestedObject.publicNestedObjectWithGetter);");
		engine.eval("retValues.put('privateNestedObjectWithGetter', publicNestedObject.privateNestedObjectWithGetter);");
		
		assertEquals(globalObject.publicNestedObject.getClass(), retValues.get("class"));
		assertEquals(globalObject.publicNestedObject.getConcat(), retValues.get("concat"));
    	assertEquals(globalObject.publicNestedObject.getPublicNestedObjectWithGetter(), retValues.get("publicNestedObjectWithGetter"));
    	assertEquals(globalObject.publicNestedObject.getPrivateNestedObjectWithGetter(), retValues.get("privateNestedObjectWithGetter"));
    	
    	//test that getter is has no priority before direct get
    	assertTrue(globalObject.publicNestedObject.duplicatedPublicStringProperty != null);
		engine.eval("publicNestedObject.publicStringProperty;");
		assertTrue(globalObject.publicNestedObject.duplicatedPublicStringProperty != null);
	}
	
	@Test
	public void TestPropertySetter() throws ScriptException {
		NestedObjectWithGetter globalObject = new NestedObjectWithGetter(2);
		JavaScriptEngine engine = getObjectTopLevelScriptEngine(globalObject);
		NestedObjectWithGetter newNestedObject = new NestedObjectWithGetter(1);

		engine.put("newNestedObject", newNestedObject);
		engine.eval("publicNestedObject.privateNestedObjectWithGetter = newNestedObject;");

		assertEquals(globalObject.publicNestedObject.getPrivateNestedObjectWithGetter(), newNestedObject);
		
    	//test that setter is has no priority before direct set
    	assertFalse(globalObject.publicNestedObject.duplicatedPublicStringProperty.equals("new property"));
		engine.eval("publicStringProperty = 'new property';");
		assertFalse(globalObject.publicNestedObject.duplicatedPublicStringProperty.equals("new property"));
	}
	
	protected JavaScriptEngine getObjectTopLevelScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null, new DefaultContextFactory()) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ObjectTopLevel(object, this);
			}
		};
	}
}

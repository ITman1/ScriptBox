/**
 * JavaScriptAnnotationsTests.java
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.annotation.InvisibleField;
import org.fit.cssbox.scriptbox.script.annotation.InvisibleFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptClass;
import org.fit.cssbox.scriptbox.script.annotation.ScriptField;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.annotation.ScriptGetter;
import org.fit.cssbox.scriptbox.script.annotation.ScriptSetter;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.reflect.ObjectGetter;
import org.junit.Test;
import org.mozilla.javascript.TopLevel;

import tests.script.TestUtils.AbstractGlobalObjectScriptEngineFactory;
import tests.script.TestUtils.AssertCallback;
import tests.script.TestUtils.GlobalObjectScriptEngineTester;
import tests.script.TestUtils.Resetable;

/**
 * Tests hosted Java object, its defined restricted properties, 
 * functions according to specified script annotations.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class JavaScriptAnnotationsTests {
	
	public static class JavaScriptAnnotationsEngineFactory extends AbstractGlobalObjectScriptEngineFactory {

		@Override
		public BrowserScriptEngine getScriptEngineProtected(final Object object) {
			return new WindowJavaScriptEngine(this, null) {
				@Override
				protected TopLevel initializeTopLevel() {
					return new ObjectTopLevel(object, this);
				}
			};
		}
	};
	
	public static class ScriptClassAnnotatedObject implements Resetable {
		public String property;
		@InvisibleField
		public String invisibleProperty;
		@ScriptField
		public String visibleProperty;
		
		public void function() {}
		@InvisibleFunction
		public void invisibleFunction() {}
		@ScriptFunction
		public void visibleFunction() {}
		
		@Override
		public void reset() {
			invisibleProperty = "invisibleProperty";
			visibleProperty = "visibleProperty";
		}
	}
	
	@ScriptClass(options = {ScriptClass.ALL_METHODS})
	public static class AllMethodsAnnotatedObject extends ScriptClassAnnotatedObject {
	}
	
	@ScriptClass(options = {ScriptClass.ALL_FIELDS})
	public static class AllFieldsAnnotatedObject extends ScriptClassAnnotatedObject {
	}
	
	@ScriptClass(options = {ScriptClass.ALL_FIELDS, ScriptClass.ALL_METHODS})
	public static class AllFieldsMethodsAnnotatedObject extends ScriptClassAnnotatedObject {
	}
	
	public static class ScriptAnnotatedObject implements ObjectGetter, Resetable {
		@ScriptField(options = {})
		public String recentlyCalledGetter;
		@ScriptField(options = {})
		public String recentlyCalledSetter;
		
		public String property;
		public String propertyWithGetter;
		public String propertyWithSetter;
		public String propertyWithSetterAndGetter;
		
		@ScriptField(engines = {"unsupported"})
		public String unsupportedVisibleProperty;
		
		@ScriptField
		public String visibleProperty;
		@ScriptField
		public String visiblePropertyWithGetter;
		@ScriptField
		public String visiblePropertyWithSetter;
		@ScriptField
		public String visiblePropertyWithSetterAndGetter;
		
		@ScriptField
		public String visiblePropertyWithNoGetterOverride;
		@ScriptField
		public String visiblePropertyWithNoSetterOverride;
		
		public String propertyWithCallableGetter;
		public String propertyWithCallableSetter;

		public String propertyWithEnumerableGetter;
		public String propertyWithEnumerableSetter;
		
		public String propertyWithExplicitGetter;
		public String propertyWithExplicitSetter;
		
		@ScriptGetter(engines = {"javascript"})
		public String getPropertyWithGetter() {
			recentlyCalledGetter = "getPropertyWithGetter";
			return propertyWithGetter;
		}
		
		@ScriptSetter(engines = {"javascript"})
		public void setPropertyWithSetter(String value) {
			recentlyCalledSetter = "setPropertyWithSetter";
			propertyWithSetter = value;
		}
		
		@ScriptGetter
		public String getPropertyWithSetterAndGetter() {
			recentlyCalledGetter = "getPropertyWithSetterAndGetter";
			return propertyWithSetterAndGetter;
		}
		
		@ScriptSetter
		public void setPropertyWithSetterAndGetter(String value) {
			recentlyCalledSetter = "setPropertyWithSetterAndGetter";
			propertyWithSetterAndGetter = value;
		}
		
		@ScriptGetter(engines = {"unsupported"})
		public String getUnsupportedVisibleProperty() {
			recentlyCalledGetter = "getUnsupportedVisibleProperty";
			return propertyWithGetter;
		}
		
		@ScriptSetter(engines = {"unsupported"})
		public void setUnsupportedVisibleProperty(String value) {
			recentlyCalledSetter = "setUnsupportedVisibleProperty";
			propertyWithSetter = value;
		}
		
		@ScriptGetter
		public String getVisiblePropertyWithGetter() {
			recentlyCalledGetter = "getVisiblePropertyWithGetter";
			return visiblePropertyWithGetter;
		}
		
		@ScriptSetter
		public void setVisiblePropertyWithSetter(String value) {
			recentlyCalledSetter = "setVisiblePropertyWithSetter";
			visiblePropertyWithSetter = value;
		}
		
		@ScriptGetter
		public String getVisiblePropertyWithSetterAndGetter() {
			recentlyCalledGetter = "getVisiblePropertyWithSetterAndGetter";
			return visiblePropertyWithSetterAndGetter;
		}
		
		@ScriptSetter
		public void setVisiblePropertyWithSetterAndGetter(String value) {
			recentlyCalledSetter = "setVisiblePropertyWithSetterAndGetter";
			visiblePropertyWithSetterAndGetter = value;
		}
		
		@ScriptGetter(options = {})
		public String getVisiblePropertyWithNoGetterOverride() {
			recentlyCalledGetter = "getVisiblePropertyWithNoGetterOverride";
			return visiblePropertyWithNoGetterOverride;
		}
		
		@ScriptSetter(options = {})
		public void setVisiblePropertyWithNoSetterOverride(String value) {
			recentlyCalledSetter = "setVisiblePropertyWithNoSetterOverride";
			visiblePropertyWithNoSetterOverride = value;
		}
		
		@ScriptGetter(options = {ScriptGetter.CALLABLE_GETTER})
		public String getPropertyWithCallableGetter() {
			recentlyCalledGetter = "getPropertyWithCallableGetter";
			return propertyWithCallableGetter;
		}
		
		@ScriptSetter(options = {ScriptSetter.CALLABLE_SETTER})
		public void setPropertyWithCallableSetter(String value) {
			recentlyCalledSetter = "setPropertyWithCallableSetter";
			propertyWithCallableSetter = value;
		}
		
		@ScriptGetter(options = {ScriptGetter.CALLABLE_ENUMERABLE_GETTER})
		public String getPropertyWithEnumerableGetter() {
			recentlyCalledGetter = "getPropertyWithEnumerableGetter";
			return propertyWithEnumerableGetter;
		}
		
		@ScriptSetter(options = {ScriptSetter.CALLABLE_ENUMERABLE_SETTER})
		public void setPropertyWithEnumerableSetter(String value) {
			recentlyCalledSetter = "setPropertyWithEnumerableSetter";
			propertyWithEnumerableSetter = value;
		}
		
		@ScriptGetter(field = "propertyWithExplicitGetter")
		public String getterWithExplicitFieldName() {
			recentlyCalledGetter = "getterWithExplicitFieldName";
			return propertyWithExplicitGetter;
		}
		
		@ScriptSetter(field = "propertyWithExplicitSetter")
		public void setterWithExplicitFieldName(String value) {
			recentlyCalledSetter = "setterWithExplicitFieldName";
			propertyWithExplicitSetter = value;
		}
		
		/* Functions */
		
		public void invisibleFunction() {
		}
		
		@ScriptFunction(engines = {"unsupported"})
		public void unsupportedFunction() {
			return;
		}
		
		@ScriptFunction(options = {ScriptFunction.ENUMERABLE})
		public void enumerableFunction() {
			return;
		}
		
		@ScriptFunction
		public void nonEnumerableFunction() {
			return;
		}
		
		@ScriptFunction
		public String overloadedFunction(String arg1) {
			return arg1;
		}
		
		@ScriptFunction
		public String overloadedFunction(String arg1, String arg2) {
			return arg1 + arg2;
		}
		
		@ScriptFunction
		@Override
		public Object get(Object arg) {
			if (arg instanceof String) {
				if (((String)arg).equals("string_key")) {
					return "string_value";
				}
			}
			
			return ObjectGetter.UNDEFINED_VALUE;
		}

		@Override
		public void reset() {
			recentlyCalledGetter = null;
			recentlyCalledSetter = null;
			property = "property";
			propertyWithGetter = "property with getter";
			propertyWithSetter = "property with setter";
			propertyWithSetterAndGetter = "property with setter and getter";
			unsupportedVisibleProperty = "unsupported visible property";
			visibleProperty = "visible property";
			visiblePropertyWithGetter = "visible property with getter";
			visiblePropertyWithSetter = "visible property with setter";
			visiblePropertyWithSetterAndGetter = "visible property with setter and getter";
			visiblePropertyWithNoGetterOverride = "visible property with getter override";
			visiblePropertyWithNoSetterOverride = "visible property with setter override";
			propertyWithCallableGetter = "property with callable getter";
			propertyWithCallableSetter = "property with callable setter";
			propertyWithEnumerableGetter = "property with enumerable getter";
			propertyWithEnumerableSetter = "property with enumerable setter";
			propertyWithExplicitGetter = "visible property with explicit getter";
			propertyWithExplicitSetter = "visible property with excplicit setter";
		}

		@Override
		public Collection<Object> getKeys() {
			return null;
		}
	}
	
	private static GlobalObjectScriptEngineTester<ScriptAnnotatedObject> tester;
	private static GlobalObjectScriptEngineTester<AllMethodsAnnotatedObject> allMethodsTester;
	private static GlobalObjectScriptEngineTester<AllFieldsAnnotatedObject> allFieldsTester;
	private static GlobalObjectScriptEngineTester<AllFieldsMethodsAnnotatedObject> allFieldsMethodsTester;
	
	private static ScriptAnnotatedObject globalObject;
	private static AllMethodsAnnotatedObject allMethodsGlobalObject;
	private static AllFieldsAnnotatedObject allFieldsGlobalObject;
	private static AllFieldsMethodsAnnotatedObject allFieldsMethodsGlobalObject;
	static {
		JavaScriptAnnotationsEngineFactory engineFactory = new JavaScriptAnnotationsEngineFactory();
		
		globalObject = new ScriptAnnotatedObject();
		tester = new GlobalObjectScriptEngineTester<ScriptAnnotatedObject>(engineFactory, globalObject);
		
		allMethodsGlobalObject = new AllMethodsAnnotatedObject();
		allMethodsTester = new GlobalObjectScriptEngineTester<AllMethodsAnnotatedObject>(engineFactory, allMethodsGlobalObject);
		
		allFieldsGlobalObject = new AllFieldsAnnotatedObject();
		allFieldsTester = new GlobalObjectScriptEngineTester<AllFieldsAnnotatedObject>(engineFactory, allFieldsGlobalObject);
		
		allFieldsMethodsGlobalObject = new AllFieldsMethodsAnnotatedObject();
		allFieldsMethodsTester = new GlobalObjectScriptEngineTester<AllFieldsMethodsAnnotatedObject>(engineFactory, allFieldsMethodsGlobalObject);
	}
	
	private static final String countOfEnumerablePropertiesScript = 
			"var count = 0;" +
			"for(var propName in this) {" +
				"count = count + 1;" + 
			"}";
	private static final String enumerablePropertiesScript = 
			"var propertyContainer = new Object();" +
			"for(var propName in this) {" +
				"propertyContainer[propName] = 'enumerable';" + 
			"}";
		
	@Test
	public void TestEnumerableProperties() {		
		
		tester.assertEquals(countOfEnumerablePropertiesScript, "count", 21);
		
		String s = enumerablePropertiesScript;
		tester.assertFalse(s, "typeof propertyContainer['propertyWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithSetterAndGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visibleProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visiblePropertyWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visiblePropertyWithSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visiblePropertyWithSetterAndGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visiblePropertyWithNoGetterOverride'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['visiblePropertyWithNoSetterOverride'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithCallableGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithCallableSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithEnumerableGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithEnumerableSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithExplicitGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['propertyWithExplicitSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['getPropertyWithEnumerableGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['setPropertyWithEnumerableSetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['enumerableFunction'] === 'undefined'");
	}
	
	@Test
	public void TestNonEnumerableProperties() {	
		tester.assertFalse("typeof this['recentlyCalledGetter'] === 'undefined'");
		tester.assertFalse("typeof this['recentlyCalledSetter'] === 'undefined'");
		tester.assertFalse("typeof this['getPropertyWithCallableGetter'] === 'undefined'");
		tester.assertFalse("typeof this['setPropertyWithCallableSetter'] === 'undefined'");
		tester.assertFalse("typeof this['nonEnumerableFunction'] === 'undefined'");
		tester.assertFalse("typeof this['overloadedFunction'] === 'undefined'");
	}
	
	@Test
	public void TestUnsupportedOrInvisibleProperties() {		
		tester.assertTrue("typeof this['property'] === 'undefined'");
		tester.assertTrue("typeof this['unsupportedVisibleProperty'] === 'undefined'");
		
		tester.assertTrue("typeof this['invisibleFunction'] === 'undefined'");
		tester.assertTrue("typeof this['unsupportedFunction'] === 'undefined'");
		tester.assertTrue("typeof this['get'] === 'undefined'");
	}
	
	@Test
	public void TestDirectAccessToVisibleFields() {	
		/* No getters */
		tester.assertCallback("visibleProperty", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledGetter);
				assertEquals(globalObject.visibleProperty, resultValue);
			};
		});
		
		tester.assertCallback("visiblePropertyWithSetter", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledGetter);
				assertEquals(globalObject.visiblePropertyWithSetter, resultValue);
			};
		});
		
		/* No setters */
		
		tester.assertCallback("visibleProperty = 'new visibleProperty'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledSetter);
				assertEquals("new visibleProperty", resultValue);
			};
		});
		
		tester.assertCallback("visiblePropertyWithGetter = 'new visiblePropertyWithGetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledSetter);
				assertEquals("new visiblePropertyWithGetter", resultValue);
			};
		});

		/* Without getter/setter override */
		
		tester.assertCallback("visiblePropertyWithNoGetterOverride", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledGetter);
				assertEquals(globalObject.visiblePropertyWithNoGetterOverride, resultValue);
			};
		});
		
		tester.assertCallback("visiblePropertyWithNoSetterOverride = 'new visiblePropertyWithNoSetterOverride'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertNull(globalObject.recentlyCalledSetter);
				assertEquals("new visiblePropertyWithNoSetterOverride", resultValue);
			};
		});
	}
	
	@Test
	public void TestRedirectedAccessToVisibleFields() {	
		/* Getters with implicit getter override */
		
		tester.assertCallback("visiblePropertyWithGetter", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals(globalObject.recentlyCalledGetter, "getVisiblePropertyWithGetter");
				assertEquals(globalObject.getVisiblePropertyWithGetter(), resultValue);
			};
		});
		
		tester.assertCallback("visiblePropertyWithSetterAndGetter", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals(globalObject.recentlyCalledGetter, "getVisiblePropertyWithSetterAndGetter");
				assertEquals(globalObject.getVisiblePropertyWithSetterAndGetter(), resultValue);
			};
		});
		
		/* Setters with implicit setter override */
		
		tester.assertCallback("visiblePropertyWithSetter = 'new visiblePropertyWithSetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals(globalObject.recentlyCalledSetter, "setVisiblePropertyWithSetter");
				assertEquals("new visiblePropertyWithSetter", globalObject.visiblePropertyWithSetter);
			};
		});
		
		tester.assertCallback("visiblePropertyWithSetterAndGetter = 'new visiblePropertyWithSetterAndGetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals(globalObject.recentlyCalledSetter, "setVisiblePropertyWithSetterAndGetter");
				assertEquals("new visiblePropertyWithSetterAndGetter", globalObject.visiblePropertyWithSetterAndGetter);
			};
		});
	}
	
	@Test
	public void TestAccessToInvisibleFields() {	
		tester.assertEquals("propertyWithGetter", globalObject.getPropertyWithGetter());
		tester.assertEquals("propertyWithSetterAndGetter", globalObject.getPropertyWithSetterAndGetter());
		tester.assertEquals("propertyWithCallableGetter", globalObject.getPropertyWithCallableGetter());
		tester.assertEquals("propertyWithEnumerableGetter", globalObject.getPropertyWithEnumerableGetter());
		tester.assertEquals("propertyWithExplicitGetter", globalObject.getterWithExplicitFieldName());
		
		
		tester.assertCallback("propertyWithSetter = 'new propertyWithSetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithSetter", globalObject.propertyWithSetter);
			};
		});
		
		tester.assertCallback("propertyWithSetterAndGetter = 'new propertyWithSetterAndGetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithSetterAndGetter", globalObject.propertyWithSetterAndGetter);
			};
		});
		
		tester.assertCallback("propertyWithCallableSetter = 'new propertyWithCallableSetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithCallableSetter", globalObject.propertyWithCallableSetter);
			};
		});
		
		tester.assertCallback("propertyWithEnumerableSetter = 'new propertyWithEnumerableSetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithEnumerableSetter", globalObject.propertyWithEnumerableSetter);
			};
		});
		
		tester.assertCallback("propertyWithExplicitSetter = 'new propertyWithExplicitSetter'", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithExplicitSetter", globalObject.propertyWithExplicitSetter);
			};
		});
	}
	
	@Test
	public void TestCallableProperties() {	
		tester.evalScript("enumerableFunction()");
		tester.evalScript("nonEnumerableFunction()");
		
		tester.assertEquals("overloadedFunction('foo')", "foo");
		tester.assertEquals("overloadedFunction('foo', 'bar')", "foobar");
		
		tester.assertEquals("getPropertyWithEnumerableGetter()", globalObject.getPropertyWithEnumerableGetter());		
		tester.assertCallback("setPropertyWithEnumerableSetter('new propertyWithEnumerableSetter')", new AssertCallback<ScriptAnnotatedObject>() {
			@Override
			public void assertResult(ScriptAnnotatedObject globalObject, Object resultValue) {
				assertEquals("new propertyWithEnumerableSetter", globalObject.propertyWithEnumerableSetter);
			};
		});
	}
	
	@Test
	public void TestScriptClassAutomaticProperties() {	
		allFieldsTester.assertEquals(countOfEnumerablePropertiesScript, "count", 5);
		allMethodsTester.assertEquals(countOfEnumerablePropertiesScript, "count", 4);
		allFieldsMethodsTester.assertEquals(countOfEnumerablePropertiesScript, "count", 5);
		
		allFields(allFieldsGlobalObject, allFieldsTester);
		visibleFunctions(allFieldsGlobalObject, allFieldsTester);
		
		allFunctions(allMethodsGlobalObject, allMethodsTester);
		visibleFields(allMethodsGlobalObject, allMethodsTester);
		
		allFunctions(allFieldsMethodsGlobalObject, allFieldsMethodsTester);
		allFields(allFieldsMethodsGlobalObject, allFieldsMethodsTester);
	}
	
	protected void allFields(ScriptClassAnnotatedObject object, GlobalObjectScriptEngineTester<?> tester) {
		tester.assertTrue("typeof this['invisibleProperty'] === 'undefined'");
		
		tester.assertFalse("typeof this['property'] === 'undefined'");
		tester.assertFalse("typeof this['property'] === 'undefined'");
		tester.assertFalse("typeof this['class'] === 'undefined'");
	}
	
	protected void visibleFields(ScriptClassAnnotatedObject object, GlobalObjectScriptEngineTester<?> tester) {
		tester.assertFalse("typeof this['visibleProperty'] === 'undefined'");
		
		tester.assertTrue("typeof this['invisibleProperty'] === 'undefined'");
		tester.assertTrue("typeof this['property'] === 'undefined'");
		tester.assertTrue("typeof this['class'] === 'undefined'");
	}
	
	protected void allFunctions(ScriptClassAnnotatedObject object, GlobalObjectScriptEngineTester<?> tester) {
		tester.assertTrue("typeof this['invisibleFunction'] === 'undefined'");
		
		tester.assertFalse("typeof this['equals'] === 'undefined'");
		tester.assertFalse("typeof this['getClass'] === 'undefined'");
		tester.assertFalse("typeof this['hashCode'] === 'undefined'");
		tester.assertFalse("typeof this['reset'] === 'undefined'");
		tester.assertFalse("typeof this['function'] === 'undefined'");
		tester.assertFalse("typeof this['visibleFunction'] === 'undefined'");
	}
	
	protected void visibleFunctions(ScriptClassAnnotatedObject object, GlobalObjectScriptEngineTester<?> tester) {
		tester.assertFalse("typeof this['visibleFunction'] === 'undefined'");
		
		tester.assertTrue("typeof this['invisibleFunction'] === 'undefined'");
		tester.assertTrue("typeof this['equals'] === 'undefined'");
		tester.assertTrue("typeof this['getClass'] === 'undefined'");
		tester.assertTrue("typeof this['hashCode'] === 'undefined'");
		tester.assertTrue("typeof this['reset'] === 'undefined'");
		tester.assertTrue("typeof this['function'] === 'undefined'");

	}
}

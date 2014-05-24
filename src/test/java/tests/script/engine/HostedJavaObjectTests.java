/**
 * HostedJavaObjectTests.java
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.wrap.DefaultWrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.DefaultClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.reflect.ObjectGetter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.TestUtils.AbstractGlobalObjectScriptEngineFactory;
import tests.script.TestUtils.GlobalObjectScriptEngineTester;

/**
 * Tests hosted Java object and top level, its defined properties and functions.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
@RunWith(JUnit4.class)
public class HostedJavaObjectTests {
	public static class DefaultContextFactory extends ContextFactory {
		
		@Override
		protected Context makeContext() {
			Context cx = super.makeContext();
			WrapFactoryDecorator wrapFactoryDecorator = new DefaultWrapFactoryDecorator();
			cx.setWrapFactory(wrapFactoryDecorator);
			return cx;
		}
	}

	public static class HostedJavaObjectEngineFactory extends AbstractGlobalObjectScriptEngineFactory {
		
		@Override
		public BrowserScriptEngine getScriptEngineProtected(final Object object) {
			return new WindowJavaScriptEngine(null, null, new DefaultContextFactory()) {
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
	
	public static class NestedObjectWithGetter implements ObjectGetter {
		public String publicProperty;
		protected String protectedProperty;
		
		public NestedObjectWithGetter publicNestedObject;
		public NestedObjectWithGetter publicNestedObjectWithGetter;
		private NestedObjectWithGetter privateNestedObjectWithGetter;
		
		public String publicStringProperty;
		public String duplicatedPublicStringProperty;
		
		private int nestLevel;
		
		public NestedObjectWithGetter(int nestLevel) {
			this.nestLevel = nestLevel;
			reset();
		}
		
		public NestedObjectWithGetter getPublicNestedObjectWithGetter() {
			return publicNestedObjectWithGetter;
		}
		
		public NestedObjectWithGetter getPrivateNestedObjectWithGetter() {
			return privateNestedObjectWithGetter;
		}
		
		public void setPrivateNestedObjectWithGetter(NestedObjectWithGetter object) {
			privateNestedObjectWithGetter = object;
		}
		
		public String getPublicStringProperty() {
			duplicatedPublicStringProperty = null;
			return publicStringProperty;
		}
		
		public void setPublicStringProperty(String value) {
			publicStringProperty = value;
			duplicatedPublicStringProperty = value;
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
			
			if (arg instanceof Integer) {
				if (((Integer)arg).equals(0)) {
					return 1;
				}
			}
			
			return ObjectGetter.UNDEFINED_VALUE;
		}

		public void reset() {
			if (nestLevel > 0) {
				publicNestedObject = new NestedObjectWithGetter(nestLevel - 1);
				publicNestedObjectWithGetter = new NestedObjectWithGetter(nestLevel - 1);
				privateNestedObjectWithGetter = new NestedObjectWithGetter(nestLevel - 1);
			}
			
			publicProperty = "public property";
			protectedProperty = "protected property";
			publicStringProperty = "publicStringProperty";
			duplicatedPublicStringProperty = "publicStringProperty";
		}

		@Override
		public Collection<Object> getKeys() {
			return null;
		}
	}
	
	private static GlobalObjectScriptEngineTester<NestedObjectWithGetter> tester;
	private static NestedObjectWithGetter globalObject;

	static {
		HostedJavaObjectEngineFactory engineFactory = new HostedJavaObjectEngineFactory();
		
		globalObject = new NestedObjectWithGetter(2);
		tester = new GlobalObjectScriptEngineTester<NestedObjectWithGetter>(engineFactory, globalObject);
	}
	
	private static final String topLevelCountOfEnumerablePropertiesScript = 
			"var count = 0;" +
			"for(var propName in this) {" +
				"count = count + 1;" + 
			"}";
	private static final String hostedCountOfEnumerablePropertiesScript = 
		"var count = 0;" +
		"for(var propName in publicNestedObject) {" +
			"count = count + 1;" + 
		"}";
	
	public void TestEnumerableProperties(String scopeObject) {
		String s = "var propertyContainer = new Object();" +
			"for(var propName in "+ scopeObject +") {" +
				"propertyContainer[propName] = 'enumerable';" + 
			"}";
		
		tester.assertFalse(s, "typeof propertyContainer['getClass'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['getPublicNestedObjectWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['equals'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['duplicatedPublicStringProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['publicNestedObjectWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['hashCode'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['class'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['wait'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['publicProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['setPublicStringProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['concat'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['publicNestedObject'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['privateNestedObjectWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['publicStringProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['setPrivateNestedObjectWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['notify'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['getConcat'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['reset'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['getPublicStringProperty'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['toString'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['getPrivateNestedObjectWithGetter'] === 'undefined'");
		tester.assertFalse(s, "typeof propertyContainer['notifyAll'] === 'undefined'");
	}
	
	public void TestFunctionProperties(String scopeObject, NestedObjectWithGetter object) {
		tester.assertEquals(scopeObject + ".getConcat('foo')", "foo");
		tester.assertEquals(scopeObject + ".getConcat('foo', 'bar')", "foobar");
		tester.assertEquals(scopeObject + ".hashCode()", object.hashCode());
	}
	
	public void TestObjectProperties(String scopeObject, NestedObjectWithGetter object) {
		tester.assertEquals(scopeObject + ".publicNestedObject", object.publicNestedObject);
		tester.assertEquals(scopeObject + ".publicProperty", object.publicProperty);
		tester.assertEquals(scopeObject + ".publicNestedObjectWithGetter", object.publicNestedObjectWithGetter);
				
		tester.assertEquals(
				scopeObject + ".publicNestedObject = " + scopeObject + ".publicNestedObjectWithGetter", 
				scopeObject + ".publicNestedObject",
				object.publicNestedObjectWithGetter);
		tester.assertEquals(
				scopeObject + ".publicNestedObjectWithGetter = " + scopeObject + ".publicNestedObject", 
				scopeObject + ".publicNestedObjectWithGetter",
				object.publicNestedObject);
		tester.assertEquals(scopeObject + ".publicProperty = 'new property'", "new property");

		tester.assertUndefined(scopeObject + "['METHOD_NAME']");
		
		object.reset();
	}
	
	public void TestObjectGetter(String scopeObject, NestedObjectWithGetter object) {
		tester.assertEquals(scopeObject + "['foo']", "bar");
		tester.assertEquals(scopeObject + "[0]", 1);
		tester.assertUndefined(scopeObject + "['str']");
		tester.assertUndefined(scopeObject + "['1']");
	}
	
	public void TestPropertyGetter(String scopeObject, NestedObjectWithGetter object) {
		tester.assertEquals(scopeObject + ".class", object.getClass());
		tester.assertEquals(scopeObject + ".concat", object.getConcat());
		tester.assertEquals(scopeObject + ".publicNestedObjectWithGetter", object.getPublicNestedObjectWithGetter());
		tester.assertEquals(scopeObject + ".privateNestedObjectWithGetter", object.getPrivateNestedObjectWithGetter());
		    	
    	// Getter classes should be defined also...
		tester.evalScript(scopeObject + ".getClass();");
		tester.evalScript(scopeObject + ".getConcat();");
		tester.evalScript(scopeObject + ".getPublicNestedObjectWithGetter();");
		tester.evalScript(scopeObject + ".getPrivateNestedObjectWithGetter();");

    	//test that getter is has no priority before direct get
		object.reset();
    	assertTrue(object.duplicatedPublicStringProperty != null);
    	tester.evalScript(scopeObject + ".publicStringProperty;");
		assertTrue(object.duplicatedPublicStringProperty != null);
	}
	
	public void TestPropertySetter(String scopeObject, NestedObjectWithGetter object) {
		object.reset();
		tester.assertEquals(
				scopeObject + ".privateNestedObjectWithGetter = " + scopeObject + ".publicNestedObject", 
				scopeObject + ".privateNestedObjectWithGetter",
				object.publicNestedObject);
		
    	//test that setter is has no priority before direct set
		object.reset();
    	assertFalse(object.duplicatedPublicStringProperty.equals("new property"));
    	tester.evalScript(scopeObject + ".publicStringProperty = 'new property';");
		assertFalse(object.duplicatedPublicStringProperty.equals("new property"));
	}
	
	@Test
	public void TestEnumerableProperties() {
		tester.assertEquals(topLevelCountOfEnumerablePropertiesScript, "count", 27);
		tester.assertEquals(hostedCountOfEnumerablePropertiesScript, "count", 24);
		
		TestEnumerableProperties("this");
		TestEnumerableProperties("publicNestedObject");
	}
	
	@Test
	public void TestFunctionProperties() {
		TestFunctionProperties("this", globalObject);
		TestFunctionProperties("publicNestedObject", globalObject.publicNestedObject);
	}
	
	@Test
	public void TestObjectProperties() {
		TestObjectProperties("this", globalObject);
		TestObjectProperties("publicNestedObject", globalObject.publicNestedObject);
	}
	
	@Test
	public void TestObjectGetter() {
		TestObjectGetter("this", globalObject);
		TestObjectGetter("publicNestedObject", globalObject.publicNestedObject);
	}
	
	@Test
	public void TestPropertyGetter() {
		TestPropertyGetter("this", globalObject);
		TestPropertyGetter("publicNestedObject", globalObject.publicNestedObject);
	}
	
	@Test
	public void TestPropertySetter() {
		TestPropertySetter("this", globalObject);
		TestPropertySetter("publicNestedObject", globalObject.publicNestedObject);
	}
	
}

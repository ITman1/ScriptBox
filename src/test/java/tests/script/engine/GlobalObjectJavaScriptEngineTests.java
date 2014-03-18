package tests.script.engine;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.annotation.ScriptAnnotationTopLevel;
import org.junit.Test;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestClasses.AnnotatedNestedObjectWithGetter;

public class GlobalObjectJavaScriptEngineTests {

	@Test
	public void TestObjectEnumeratedProperties() throws ScriptException {
		Map<String, Object> properties = new HashMap<String, Object>();
		AnnotatedNestedObjectWithGetter globalObject = new AnnotatedNestedObjectWithGetter(2);
		BrowserScriptEngine engine = new AnnotatedScriptEngineFactory().getScriptEngine(globalObject);
		
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
	
	public class AnnotatedScriptEngineFactory extends BrowserScriptEngineFactory {
		
		private static final String ENGINE_SHORTNAME = "javascript";
		
		public BrowserScriptEngine getScriptEngine(final Object object) {
			return new GlobalObjectJavaScriptEngine(this, null) {
				@Override
				protected TopLevel initializeTopLevel() {
					return new ScriptAnnotationTopLevel(object, this);
				}
			};
		}
				
		@Override
		public List<String> getExplicitlySupportedMimeTypes() {
			return null;
		}

		@Override
		public BrowserScriptEngine getBrowserScriptEngine(ScriptSettings<?> scriptSettings) {
			return null;
		}

		@Override
		public String getEngineName() {
			return null;
		}

		@Override
		public String getEngineVersion() {
			return null;
		}

		@Override
		public String getLanguageName() {
			return null;
		}

		@Override
		public String getLanguageVersion() {
			return null;
		}

		@Override
		public String getEngineShortName() {
			return ENGINE_SHORTNAME;
		}
	}
}

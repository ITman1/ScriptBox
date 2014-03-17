package tests.script.engine;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.script.javascript.GlobalObjectJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.annotation.ScriptAnnotationTopLevel;
import org.junit.Test;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

import tests.script.engine.TestClasses.AnnotatedNestedObjectWithGetter;

public class GlobalObjectJavaScriptEngineTests {

	@Test
	public void TestObjectEnumeratedProperties() throws ScriptException {
		Map<String, Object> retValues = new HashMap<String, Object>();
		AnnotatedNestedObjectWithGetter globalObject = new AnnotatedNestedObjectWithGetter(2);
		JavaScriptEngine engine = getScriptAnnotationScriptEngine(globalObject);
		
		engine.put("retValues", retValues);
		engine.eval("retValues.put('getFoo', this['foo']);");
		engine.eval("retValues.put('get0', this[0]);");
		engine.eval("retValues.put('getStr0', this['str']);");
		engine.eval("retValues.put('get1', this[1]);");
		
		assertEquals("bar", retValues.get("getFoo"));
		assertEquals(((Double)retValues.get("get0")) - 1 < 1e-1, true);
		assertEquals("undefined", retValues.get("getStr0"));
		assertEquals("undefined", retValues.get("get1"));
	}
	
	protected JavaScriptEngine getScriptAnnotationScriptEngine(final Object object) {
		return new GlobalObjectJavaScriptEngine(null, null) {
			@Override
			protected TopLevel initializeTopLevel() {
				return new ScriptAnnotationTopLevel(object, this);
			}
		};
	}
}

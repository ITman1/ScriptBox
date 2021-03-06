/**
 * TestUtils.java
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

package tests.script;

import java.net.MalformedURLException;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptContextInject;
import org.fit.cssbox.scriptbox.script.ScriptSettings;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.window.Window;
import org.mozilla.javascript.Undefined;

/**
 * Utilities used for simpler testing of the scripts.
 * 
 * @author Radim Loskot
 * @version 0.9
 * @since 0.9 - 21.4.2014
 */
public class TestUtils {
	public static interface Resetable {
		public void reset();
	}
	
	public static interface AssertCallback<GlobalObjectType> {
		public void assertResult(GlobalObjectType globalObject, Object resultValue);
	}
	
	public static class DebugInject extends Object {
		@ScriptFunction
		public void println(String arg) {
			System.out.println(arg);
		}
	}
	
	public static abstract class AbstractGlobalObjectScriptEngineFactory extends BrowserScriptEngineFactory {
		
		private static final String ENGINE_SHORTNAME = "javascript";
		
		public BrowserScriptEngine getScriptEngine(final Object object) {
			ScriptContextInject inject = new ScriptContextInject() {
				
				@Override
				public boolean inject(ScriptContext context) {
					Bindings b = context.getBindings(ScriptContext.ENGINE_SCOPE);
					b = context.getBindings(ScriptContext.ENGINE_SCOPE);
					
					b.put("debug", new DebugInject());
					
					return true;
				}
			};
			
			BrowserScriptEngine engine = getScriptEngineProtected(object);
			
			ScriptContext cx = engine.getContext();
			inject.inject(cx);
			
			return engine;
		}
		
		public abstract BrowserScriptEngine getScriptEngineProtected(final Object object);
		
		@Override
		public List<String> getExplicitlySupportedMimeTypes() {
			return null;
		}

		@Override
		protected BrowserScriptEngine getBrowserScriptEngineProtected(ScriptSettings<?> scriptSettings) {
			if (scriptSettings == null) {
				return null;
			} else {
				return getScriptEngine(scriptSettings.getGlobalObject());
			}
			
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
	
	public static class GlobalObjectScriptEngineTester<GlobalObjectType> {
		private AbstractGlobalObjectScriptEngineFactory engineFactory;
		private GlobalObjectType globalObject;
		
		public GlobalObjectScriptEngineTester(AbstractGlobalObjectScriptEngineFactory engineFactory, GlobalObjectType globalObject) {
			this.engineFactory = engineFactory;
			this.globalObject = globalObject;
		}
		
		public void assertTrue(String scriptValue) {
			assertTrue(null, scriptValue);
		}
		
		public void assertTrue(String executionScript, String scriptValue) {
			assertEquals(executionScript, scriptValue, true);
		}
		
		public void assertFalse(String scriptValue) {
			assertFalse(null, scriptValue);
		}
		
		public void assertFalse(String executionScript, String scriptValue) {
			assertEquals(executionScript, scriptValue, false);
		}
		
		public void assertNull(String scriptValue) {
			assertNull(null, scriptValue);
		}
		
		public void assertNull(String executionScript, String scriptValue) {
			Object resultValue = evalScript(executionScript, scriptValue);	
			org.junit.Assert.assertNull(resultValue);
		}
		
		public void assertNotNull(String scriptValue) {
			assertNotNull(null, scriptValue);
		}
		
		public void assertNotNull(String executionScript, String scriptValue) {
			Object resultValue = evalScript(executionScript, scriptValue);	
			org.junit.Assert.assertNotNull(resultValue);
		}
		
		public void assertUndefined(String scriptValue) {
			assertEquals(null, scriptValue, Undefined.instance);
		}
		
		public void assertEquals(String scriptValue, Object expectedValue) {
			assertEquals(null, scriptValue, expectedValue);
		}
		
		public void assertEquals(String executionScript, String scriptValue, Object expectedValue) {
			Object resultValue = evalScript(executionScript, scriptValue);
			
			if (expectedValue instanceof Integer && resultValue instanceof Double) {
				resultValue = ((Double)resultValue).intValue();
			}
			
			org.junit.Assert.assertEquals(resultValue, expectedValue);
		}
		
		public void assertNotEquals(String scriptValue, Object expectedValue) {
			assertNotEquals(null, scriptValue, expectedValue);
		}
		
		public void assertNotEquals(String executionScript, String scriptValue, Object expectedValue) {
			Object resultValue = evalScript(executionScript, scriptValue);		
			org.junit.Assert.assertFalse(expectedValue.equals(resultValue));
		}
		
		public void assertCallback(String scriptValue, AssertCallback<GlobalObjectType> assertCallback) {
			assertCallback(null, scriptValue, assertCallback);
		}
		
		public void assertCallback(String executionScript, String scriptValue, AssertCallback<GlobalObjectType> assertCallback) {
			Object resultValue = evalScript(executionScript, scriptValue);		
			assertCallback.assertResult(globalObject, resultValue);
		}	
		
		public Object evalScript(String executionScript, String scriptValue) {
			beforeEval();
			ScriptEngine engine = getScriptEngine();
			
			Object resultValue = null;
			try {
				if (executionScript != null) {
					engine.eval(executionScript);
				}
				resultValue = engine.eval(scriptValue);
			} catch (ScriptException e) {
				e.printStackTrace();
				org.junit.Assert.fail("Unexpected exception: " + e.toString());
			}
			
			return resultValue;
		}
		
		public void evalScript(String executionScript) {
			beforeEval();
			ScriptEngine engine = getScriptEngine();
			
			try {
			engine.eval(executionScript);
			} catch (ScriptException e) {
				org.junit.Assert.fail("Unexpected exception: " + e.toString());
			}
		}
		
		protected void beforeEval() {
			if (globalObject instanceof Resetable) {
				((Resetable)globalObject).reset();
			}
		}
		
		protected ScriptEngine getScriptEngine() {
			return engineFactory.getScriptEngine(globalObject);
		}
	}
	
	public static class UserAgentWindowTester {
		protected UserAgent userAgent;
		
		public UserAgentWindowTester() {
			userAgent = new UserAgent();
		}
		
		public Window navigate(String url) {
			BrowsingUnit browsingUnit = userAgent.openBrowsingUnit();
			try {
				browsingUnit.navigate(url);
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			
			synchronized (this) {
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			Html5DocumentImpl doc = browsingUnit.getWindowBrowsingContext().getActiveDocument();
			return doc.getWindow();
		}
	}
}

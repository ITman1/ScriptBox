package tests.script;

import java.net.MalformedURLException;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.fit.cssbox.scriptbox.browser.BrowsingUnit;
import org.fit.cssbox.scriptbox.browser.UserAgent;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.ScriptSettings;

public class TestUtils {
	public static interface Resetable {
		public void reset();
	}
	
	public static interface AssertCallback<GlobalObjectType> {
		public void assertResult(GlobalObjectType globalObject, Object resultValue);
	}
	
	public static abstract class AbstractGlobalObjectScriptEngineFactory extends BrowserScriptEngineFactory {
		
		private static final String ENGINE_SHORTNAME = "javascript";
		
		public abstract BrowserScriptEngine getScriptEngine(final Object object);
				
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
				// TODO Auto-generated catch block
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

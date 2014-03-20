package tests.script.window;

import org.fit.cssbox.scriptbox.browser.Window;
import org.junit.Test;

import tests.script.TestUtils.UserAgentWindowTester;

public class WindowPropertiesTests {
	
	private static UserAgentWindowTester tester;

	static {
		tester = new UserAgentWindowTester();
		
	}
		
	@Test
	public void TestWindowNames() {
		Window window = tester.navigate("http://www.stud.fit.vutbr.cz/~xlosko01/CSSBox/tests/window_properties.html");
		try {
			synchronized (this) {
				Thread.sleep(1000000000);
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
}

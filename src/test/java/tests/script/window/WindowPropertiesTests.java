package tests.script.window;

import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.events.EventImpl;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.dom.Html5DocumentImpl;
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
		EventImpl event = new EventImpl();
		event.initEvent("onclick", true, true);
		Html5DocumentImpl document = window.getDocumentImpl();
		ElementImpl element = (ElementImpl)document.getElementById("event_target");
		window.dispatchEvent(event, element);
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

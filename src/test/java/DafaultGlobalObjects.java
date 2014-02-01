import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.*;
import static org.junit.Assert.*;

public class DafaultGlobalObjects {
	private static final ScriptEngine engine;

	static {
		engine = new ScriptEngineManager().getEngineByName("js");
	}

	@Test
	public void StringConcat() {
		try {
			engine.eval(
					"var propValue;" + "for(var propName in this.context) {"
					+ "    print(propName + ';');" + "}");
			assertEquals(15, 15);
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

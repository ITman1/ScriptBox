package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.browser.Window;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class WindowTopLevel extends TopLevel {
	private static final long serialVersionUID = -824471943182669084L;

	private Window window;
	
	public WindowTopLevel(Window window) {
		this.window = window;
		
		Context cx = WindowScriptEngine.enterContext();
		try {
			cx.initStandardObjects(this, true);
			String builtinFunctions[] = {"debug", "nldebug"};
			defineFunctionProperties(builtinFunctions, WindowTopLevel.class, ScriptableObject.DONTENUM);
			sealObject();
		} finally {
			Context.exit();
		}
	}
	
	public static Object debug(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		printArgs(args, true);
		return Context.getUndefinedValue();
	}
	
	public static Object nldebug(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		printArgs(args, false);
		return Context.getUndefinedValue();
	}
	
	private static void printArgs(Object[] args, boolean newLine) {
		List<String> argsList = new ArrayList<String>();
		if (args != null) {
			for (Object arg : args) {
				String printableString = printableString(arg);
				argsList.add(printableString);
			}
		}
		String joinedList = StringUtils.join(argsList, ", ");
		if (newLine) {
			System.out.println(joinedList);
		} else {
			System.out.print(joinedList);
		}
	}
	
	private static String printableString(Object arg) {
		if (arg instanceof Wrapper) {
			arg = ((Wrapper)arg).unwrap();
		}
			
		String printString;
		if (arg == null) {
			printString = "null";
		} else if (arg instanceof Undefined) {
			printString = "undefined";
		} else {
			printString = arg.toString();
		}

		return printString;
	}
}

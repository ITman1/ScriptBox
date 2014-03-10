package org.fit.cssbox.scriptbox.script.javascript;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngine;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class ObjectTopLevel extends TopLevel {	
	private static final long serialVersionUID = -824471943182669084L;

	private Object globalObject;
	private BrowserScriptEngine browserScriptEngine;
	
	public ObjectTopLevel(Object globalObject, BrowserScriptEngine browserScriptEngine) {
		this.globalObject = globalObject;
		this.browserScriptEngine = browserScriptEngine;
		
		Context cx = Context.enter();
		try {
			cx.initStandardObjects(this, true);
			
			implementGlobalObject();
			defineBuiltinFunctions();
			defineBuiltinProperties();
			deleteRhinoUnsafeProperties();
			sealObject();
		} finally {
			Context.exit();
		}
	}
	
	public Object getGlobalObject() {
		return globalObject;
	}
	
	public BrowserScriptEngine getBrowserScriptEngine() {
		return browserScriptEngine;
	}
	
	protected ObjectImplementor implementGlobalObject() {
		return new ObjectImplementor(globalObject, this, browserScriptEngine);
	}
	
	protected void defineBuiltinProperties() {
		
	}
	
	protected void defineBuiltinFunctions() {
		String builtinFunctions[] = {"debug", "nldebug"};
		defineFunctionProperties(builtinFunctions, ObjectTopLevel.class, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
	}
	
	
	
    private void deleteRhinoUnsafeProperties() {
        delete("JavaAdapter");
        delete("org");
        delete("java");
        delete("JavaImporter");
        delete("Script");
        delete("edu");
        delete("uneval");
        delete("javax");
        delete("getClass");
        delete("com");
        delete("net");
        delete("Packages");
        delete("importClass");
        delete("importPackage");
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

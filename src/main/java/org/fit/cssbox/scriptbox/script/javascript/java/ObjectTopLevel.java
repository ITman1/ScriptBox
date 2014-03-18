package org.fit.cssbox.scriptbox.script.javascript.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassFunction;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class ObjectTopLevel extends TopLevel {	
	private static final long serialVersionUID = -824471943182669084L;

	protected Object globalObject;
	protected JavaScriptEngine scriptEngine;
	protected ObjectImplementor implementor;
	
	public ObjectTopLevel(Object globalObject, JavaScriptEngine scriptEngine, ObjectImplementor implementor) {
		this.globalObject = globalObject;
		this.scriptEngine = scriptEngine;
		this.implementor = implementor;
		
		Context cx = scriptEngine.enterContext();
		try {
			cx.initStandardObjects(this, true);
			deleteRhinoUnsafeProperties();
			
			implementGlobalObject();
			defineBuiltinFunctions();
			defineBuiltinProperties();
			sealObject();
		} finally {
			scriptEngine.exitContext();
		}
	}
	
	public ObjectTopLevel(Object globalObject, JavaScriptEngine browserScriptEngine) {
		this(globalObject, browserScriptEngine, null);
	}
	
	public Object getGlobalObject() {
		return globalObject;
	}
	
	public JavaScriptEngine getBrowserScriptEngine() {
		return scriptEngine;
	}
	
	@Override
	public Object get(int index, Scriptable start) {
		Object object = super.get(index, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(index) : object;
		return object;
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(name) : object;
		return object;
	}
	
	@Override
	public Object[] getIds() {
		String[] ids = {};
		if (implementor != null) {
			Set<String> properties = implementor.getEnumerableProperties();
			ids = properties.toArray(new String[properties.size()]);;
		}

		return ids;
	}
	
	protected Object objectGetterGet(Object arg) {
		ClassFunction objectGetter = (implementor != null)? implementor.getObjectMembers().getObjectGetter() : null;
		
		if (objectGetter != null) {
			Object value = objectGetter.invoke(globalObject, arg);
			
			if (value != ObjectGetter.UNDEFINED_VALUE) {
				return value;
			}
		}
		
		return Scriptable.NOT_FOUND;
	}
	
	protected void implementGlobalObject() {
		if (implementor == null) {
			implementor = new ObjectImplementor(globalObject, scriptEngine);
		}
		
		implementor.implementObject(this);
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

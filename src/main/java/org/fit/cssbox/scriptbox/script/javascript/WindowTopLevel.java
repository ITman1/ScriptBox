package org.fit.cssbox.scriptbox.script.javascript;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptException;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.browser.Window;
import org.fit.cssbox.scriptbox.script.ScriptAnnotation;
import org.fit.cssbox.scriptbox.script.ScriptFunction;
import org.fit.cssbox.scriptbox.script.ScriptGetter;
import org.fit.cssbox.scriptbox.script.ScriptSetter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.TopLevel;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.Wrapper;

public class WindowTopLevel extends TopLevel {	
	private static final long serialVersionUID = -824471943182669084L;

	private WindowScriptEngine windowScriptEngine;
	
	public WindowTopLevel(WindowScriptEngine windowScriptEngine) {
		this.windowScriptEngine = windowScriptEngine;
		
		Context cx = WindowScriptEngine.enterContext();
		try {
			cx.initStandardObjects(this, true);
			
			defineBuiltinFunctions();
			defineBuiltinProperties();
			defineWindowFunctions();
			defineWindowProperties();
			deleteRhinoUnsafeProperties();
			sealObject();
		} finally {
			WindowScriptEngine.exitContext();
		}
	}
	
	private void defineBuiltinProperties() {
		
	}
	
	private void defineBuiltinFunctions() {
		String builtinFunctions[] = {"debug", "nldebug"};
		defineFunctionProperties(builtinFunctions, WindowTopLevel.class, ScriptableObject.DONTENUM | ScriptableObject.PERMANENT);
	}
	
	private void defineWindowFunctions() {
		Window window = windowScriptEngine.getWindow();
 		Class<?> windowClass = window.getClass();
 		
		for (Method method : windowClass.getMethods()) {	
			Annotation functionAnnotation = method.getAnnotation(ScriptFunction.class);   
			
			if (functionAnnotation != null) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(functionAnnotation, windowScriptEngine);
				if (!isSupported) {
					continue;
				}
				
				String methodName = method.getName();
				ObjectFunctionWrapper.defineWrappedObjectFunction(this,  methodName, window, method);
			}
		}
	}
	
	private void defineWindowProperties() {
		Window window = windowScriptEngine.getWindow();
 		Class<?> windowClass = window.getClass();
		Map<String, Method> getters = new HashMap<String, Method>();
		Map<String, Method> setters = new HashMap<String, Method>();
		for (Method method : windowClass.getMethods()){		
			Annotation annotation = null;
			if ((annotation = method.getAnnotation(ScriptGetter.class)) != null) {
			} else if ((annotation = method.getAnnotation(ScriptSetter.class)) != null) {
			}
			
			if (annotation != null) {
				boolean isSupported = ScriptAnnotation.isEngineSupported(annotation, windowScriptEngine);
				if (!isSupported) {
					continue;
				}
				String methodName = method.getName();
				if (annotation instanceof ScriptGetter) {
					String fieldName = ScriptAnnotation.getFieldFromGetterName(methodName);
					getters.put(fieldName, method);
				} else if (annotation instanceof ScriptSetter) {
					String fieldName = ScriptAnnotation.getFieldFromSetterName(methodName);
					setters.put(fieldName, method);
				}
			}
		}
		
		for (Map.Entry<String, Method> getterEntry : getters.entrySet()) {
			String fieldName = getterEntry.getKey();
			Method windowFieldGetter = getterEntry.getValue();
			Method windowFieldSetter = setters.get(fieldName);
			
			setters.remove(fieldName);
			ObjectFieldGetterSetterWrapper.defineWrappedObjectFieldGetterSetter(this, fieldName, window, windowFieldGetter, windowFieldSetter);
		}
	}
	
    private void deleteRhinoUnsafeProperties() {
    	Object[] objects = getAllIds();
        /*delete("JavaAdapter");
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
        delete("importPackage");*/
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

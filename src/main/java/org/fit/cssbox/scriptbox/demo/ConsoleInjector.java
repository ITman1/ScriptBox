package org.fit.cssbox.scriptbox.demo;

import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;

import org.apache.commons.lang3.StringUtils;
import org.fit.cssbox.scriptbox.script.annotation.ScriptFunction;
import org.fit.cssbox.scriptbox.script.javascript.JavaScriptInjector;
import org.mozilla.javascript.Undefined;

public class ConsoleInjector extends JavaScriptInjector {

	public static class Console {
		
		@ScriptFunction
		public void debug(Object ...args) {
			printArgs(args, true);
		}
		
		private static void printArgs(Object[] args, boolean newLine) {
			List<String> argsList = new ArrayList<String>();
			if (args != null) {
				for (Object arg : args) {
					String printableString = printableString(arg);
					argsList.add(printableString);
				}
			}
			String joinedList = StringUtils.join(argsList, " ");
			if (newLine) {
				System.out.println(joinedList);
			} else {
				System.out.print(joinedList);
			}
		}
		
		private static String printableString(Object arg) {			
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
	
	@Override
	public boolean inject(ScriptContext context) {
		Console console = new Console();
		
		Bindings bindings = context.getBindings(ScriptContext.ENGINE_SCOPE);
		bindings.put("console", console);
		
		return true;
	}

}

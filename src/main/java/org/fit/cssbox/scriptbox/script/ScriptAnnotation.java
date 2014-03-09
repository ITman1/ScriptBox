package org.fit.cssbox.scriptbox.script;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public class ScriptAnnotation {
	public static boolean isEngineSupported(Annotation annotation, BrowserScriptEngine scriptEngine) {
		String engineName = scriptEngine.getBrowserFactory().getEngineShortName();
		String engines[] = null;		
		if (annotation instanceof ScriptGetter) {
			ScriptGetter scriptGetter = (ScriptGetter)annotation;
			engines = scriptGetter.engines();
		} else if (annotation instanceof ScriptSetter) {
			ScriptSetter scriptSetter = (ScriptSetter)annotation;
			engines = scriptSetter.engines();
		} else if (annotation instanceof ScriptFunction) {
			ScriptFunction scriptFunction = (ScriptFunction)annotation;
			engines = scriptFunction.engines();
		}
		
		if (engines == null || engines.length == 0) {
			return true;
		}
		
		List<String> enginesList = Arrays.asList(engines);
		
		return engineName != null && !engineName.isEmpty() && enginesList.contains(engineName);
	}
	
	public static String getFieldFromGetterName(String getterName) {
		if (getterName.startsWith("get") && getterName.length() > 3) {
			return (getterName.charAt(3) + "").toLowerCase() + getterName.substring(4);
		} else {
			return getterName;
		}
	}
	
	public static String getFieldFromSetterName(String getterName) {
		if (getterName.startsWith("set") && getterName.length() > 3) {
			return (getterName.charAt(3) + "").toLowerCase() + getterName.substring(4);
		} else {
			return getterName;
		}
	}
}

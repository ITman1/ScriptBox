/**
 * ObjectTopLevel.java
 * (c) Radim Loskot and Radek Burget, 2013-2014
 *
 * ScriptBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * ScriptBox is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *  
 * You should have received a copy of the GNU Lesser General Public License
 * along with ScriptBox. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.fit.cssbox.scriptbox.script.javascript.java;

import org.fit.cssbox.scriptbox.script.java.ClassFunction;
import org.fit.cssbox.scriptbox.script.java.ObjectGetter;
import org.fit.cssbox.scriptbox.script.javascript.WindowJavaScriptEngine;
import org.fit.cssbox.scriptbox.script.javascript.js.HostedJavaObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.TopLevel;

public class ObjectTopLevel extends TopLevel {	
	private static final long serialVersionUID = -824471943182669084L;

	protected Object globalObject;
	protected WindowJavaScriptEngine scriptEngine;
	protected ObjectImplementor implementor;
	
	public ObjectTopLevel(Object globalObject, WindowJavaScriptEngine scriptEngine, ObjectImplementor implementor) {
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
	
	public ObjectTopLevel(Object globalObject, WindowJavaScriptEngine browserScriptEngine) {
		this(globalObject, browserScriptEngine, null);
	}
	
	public Object getGlobalObject() {
		return globalObject;
	}
	
	public WindowJavaScriptEngine getBrowserScriptEngine() {
		return scriptEngine;
	}
	
	@Override
	public Object get(int index, Scriptable start) {
		Object object = super.get(index, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(index) : object;
		
		if (object != Scriptable.NOT_FOUND) {
			object = WindowJavaScriptEngine.javaToJS(object, this);
		}
		
		return object;
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? objectGetterGet(name) : object;
		
		if (object != Scriptable.NOT_FOUND) {
			object = WindowJavaScriptEngine.javaToJS(object, this);
		}
		
		return object;
	}
	
	@Override
	public Object[] getIds() {
		Object[] superIds = {};
		
		if (implementor != null) {
			return HostedJavaObject.getIds(implementor.getObjectMembers(), superIds);
		}

		return superIds;
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
}

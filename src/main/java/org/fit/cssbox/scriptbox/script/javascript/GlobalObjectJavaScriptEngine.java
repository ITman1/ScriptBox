/**
 * GlobalObjectJavaScriptEngine.java
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

package org.fit.cssbox.scriptbox.script.javascript;

import org.apache.xerces.dom.ElementImpl;
import org.fit.cssbox.scriptbox.browser.WindowScriptSettings;
import org.fit.cssbox.scriptbox.script.BrowserScriptEngineFactory;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectTopLevel;
import org.fit.cssbox.scriptbox.script.javascript.java.reflect.ClassMembersResolverFactory;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.DefaultShutter;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.TopLevel;

public class GlobalObjectJavaScriptEngine extends JavaScriptEngine {

	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings, ContextFactory contextFactory) {
		super(factory, scriptSettings, contextFactory);
	}
	
	public GlobalObjectJavaScriptEngine(BrowserScriptEngineFactory factory, WindowScriptSettings scriptSettings) {
		super(factory, scriptSettings);
	}

	@Override
	protected TopLevel initializeTopLevel() {
		Object object = scriptSettings.getGlobalObject();
		return new ObjectTopLevel(object, this);
	}
	
	@Override
	protected ClassMembersResolverFactory initializeClassMembersResolverFactory() {
		DefaultShutter explicitGrantShutter = new DefaultShutter();
		explicitGrantShutter.addVisibleClass(ElementImpl.class, true, false);
		ClassMembersResolverFactory factory = new ScriptAnnotationClassMembersResolverFactory(this, explicitGrantShutter);
		return factory;
	}
}

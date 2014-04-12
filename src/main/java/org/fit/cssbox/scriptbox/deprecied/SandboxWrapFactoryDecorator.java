/**
 * SandboxWrapFactoryDecorator.java
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

package org.fit.cssbox.scriptbox.deprecied;

import org.fit.cssbox.scriptbox.script.javascript.wrap.WrapFactoryDecorator;
import org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox.Shutter;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/** 
 * @deprecated
 */
public class SandboxWrapFactoryDecorator extends WrapFactoryDecorator {

	protected Shutter shutter;
	
	public SandboxWrapFactoryDecorator(Shutter shutter) {
		this(shutter, null);
	}
	
	public SandboxWrapFactoryDecorator(Shutter shutter, WrapFactoryDecorator decorator) {
		super(decorator);
		
		this.shutter = shutter;
	}

	@Override
	public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object javaObject, Class<?> staticType) {
		Scriptable returnedObject = super.wrapAsJavaObject(cx, scope, javaObject, staticType);
		Scriptable sandboxedObject = new SanboxedJavaObject(returnedObject, shutter);
		
		return sandboxedObject;
	}
}

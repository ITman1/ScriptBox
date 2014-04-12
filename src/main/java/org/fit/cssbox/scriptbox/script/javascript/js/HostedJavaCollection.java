/**
 * HostedJavaCollection.java
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

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.mozilla.javascript.Scriptable;

public class HostedJavaCollection extends HostedJavaObject {
	private static final long serialVersionUID = -369637419233477403L;
	
	protected Class<?> javaObjectType;
	
	public HostedJavaCollection(Scriptable scope, Object javaObject) {
		super(scope, javaObject);
		
		javaObjectType = javaObject.getClass();
	}
	
	@Override
	public Object get(int index, Scriptable start) {
		Object object;
		
		object = super.get(index, start);
		object = (object == Scriptable.NOT_FOUND)? collectionGet(index) : object;
		
		return object;
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object;
		
		object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? collectionGet(name) : object;
		
		return object;
	}
	
	protected Object collectionGet(Object key) {
		Method method = null;
		Object result = Scriptable.NOT_FOUND;
		
		if (javaObject instanceof List<?> && key instanceof Integer) {
			try {
				method = ClassUtils.getPublicMethod(javaObjectType, "get", int.class);
			} catch (Exception e) {
				throw new InternalException(e);
			}
			List<?> list = (List<?>)javaObject;
			int index = (Integer)key;
			result = (list.size() > index)? list.get((Integer)key) : Scriptable.NOT_FOUND;
		} else if (javaObject instanceof Map<?,?>) {
			try {
				method = ClassUtils.getPublicMethod(javaObjectType, "get", Object.class);
			} catch (Exception e) {
				throw new InternalException(e);
			}
			result = ((Map<?,?>)javaObject).get(key);
		}
		
		if (result != Scriptable.NOT_FOUND && method != null) {
			//result = new JavaMethodRedirectedWrapper(result, javaObject, method);
		}
		
		return result;
	}
}

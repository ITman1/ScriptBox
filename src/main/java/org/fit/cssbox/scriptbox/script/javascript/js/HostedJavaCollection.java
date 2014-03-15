package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.mozilla.javascript.Scriptable;

public class HostedJavaCollection extends HostedJavaObject {
	private static final long serialVersionUID = -369637419233477403L;
	
	protected Class<?> javaObjectType;
	
	public HostedJavaCollection(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		
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
			result = new JavaMethodRedirectedWrapper(result, javaObject, method);
		}
		
		return result;
	}
}

package org.fit.cssbox.scriptbox.script.javascript.js;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.java.ObjectGetter;
import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

public class HostedJavaObject extends NativeJavaObject {

	private static final long serialVersionUID = 6761328943903362404L;

	protected boolean hasNonObjectGetterGet;
	
	protected Class<?> javaObjectType;
	
	protected Object[] cachedIds;
	
	public HostedJavaObject(Scriptable scope, Object javaObject, Class<?> staticType) {
		super(scope, javaObject, staticType);
		
		this.javaObjectType = javaObject.getClass();
		
		if (javaObject instanceof ObjectGetter) {
			Class<?>[] getterArgs = ObjectGetter.METHOD_ARG_TYPES;
			String getterName = ObjectGetter.METHOD_NAME;
			for (Method method : javaObjectType.getMethods()) {
				String methodName = method.getName();
				Class<?>[] methodParams = method.getParameterTypes();
				if (methodName.equals(getterName) && !Arrays.equals(methodParams, getterArgs)) {
					hasNonObjectGetterGet = true;
					break;
				}
			}
		}
	}
	
	@Override
	public Object get(int index, Scriptable start) {		
		return getterGet(index);
	}
	
	@Override
	public Object get(String name, Scriptable start) {
		Object object;
		
		object = super.get(name, start);
		object = (object == Scriptable.NOT_FOUND)? getterGet(name) : object;
		
		return object;
	}
	
	protected Object getterGet(Object key) {
		Method method = null;
		Object result = Scriptable.NOT_FOUND;
		
		if (javaObject instanceof ObjectGetter) {
			Object value = ((ObjectGetter)javaObject).get(key);
			
			try {
				method = ClassUtils.getPublicMethod(javaObjectType, ObjectGetter.METHOD_NAME, ObjectGetter.METHOD_ARG_TYPES);
			} catch (Exception e) {
				throw new InternalException(e);
			}
			
			if (value != ObjectGetter.UNDEFINED_VALUE) {
				result = value;
			}
		}
		
		if (result != Scriptable.NOT_FOUND && method != null) {
			result = new JavaMethodRedirectedWrapper(result, javaObject, method);
		}
		
		return result;
	}
	
	@Override
	public boolean has(String name, Scriptable start) {
		boolean hasProperty = super.has(name, start);
				
		if (hasProperty && name.equals(ObjectGetter.METHOD_NAME)) {
			return hasNonObjectGetterGet;
		}
		
		return hasProperty;
	}
	
	@Override
	public Object[] getIds() {
		if (cachedIds == null) {
			refreshIdsCache();
		}
		
		return cachedIds;
	}
	
	protected void refreshIdsCache() {
		List<Object> list = new ArrayList<Object>(Arrays.asList(super.getIds()));
		
		if (!hasNonObjectGetterGet) {
			list.remove(ObjectGetter.METHOD_NAME);
		}
		
		cachedIds = list.toArray(new Object[list.size()]);
	}
	
}

package org.fit.cssbox.scriptbox.script.javascript.wrap.sandbox;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ClassUtils;
import org.fit.cssbox.scriptbox.script.javascript.exceptions.InternalException;
import org.fit.cssbox.scriptbox.script.javascript.object.ObjectScriptable;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Wrapper;

public class SanboxedJavaObject implements Scriptable, Serializable, Wrapper {

	private static final long serialVersionUID = 6120524771501878068L;

	protected Scriptable wrappedScriptable;
	protected Object wrappedObject;
	protected Class<?> wrappedObjectType;
	protected Shutter shutter;
	protected final Map<Method, Boolean> visitedMethods;
	protected final Map<String, Boolean> visitedFields;

	public SanboxedJavaObject(Scriptable wrappedScriptable, Shutter shutter) {
		this.wrappedScriptable = wrappedScriptable;
		this.shutter = shutter;
		this.visitedMethods = new HashMap<Method, Boolean>();
		this.visitedFields = new HashMap<String, Boolean>();
		
		if (wrappedScriptable instanceof Wrapper) {
			this.wrappedObject = ((Wrapper)wrappedScriptable).unwrap();
		} else {
			this.wrappedObject = wrappedScriptable;
		}
		
		this.wrappedObjectType = wrappedObject.getClass();
	}

	@Override
	public String getClassName() {
		return "SanboxedJavaObject";
	}

	@Override
	public Object get(String name, Scriptable scope) {

		Object propertyObject = wrappedScriptable.get(name, scope);
		
		if (propertyObject != NOT_FOUND) {
			propertyObject = ObjectScriptable.jsToJava(propertyObject);
		}

		if (propertyObject instanceof BaseFunction) {
			boolean isMethodVisible = isFunctionVisible((BaseFunction)propertyObject);
			  
			if (!isMethodVisible) {
				return NOT_FOUND;
			}
		} else if (propertyObject != NOT_FOUND) {
			boolean isFieldVisible = isFieldVisible(name);
			  
			if (!isFieldVisible) {
				return NOT_FOUND;
			}
		}

		return propertyObject;
	}

	@Override
	public Object unwrap() {
		if (wrappedScriptable instanceof Wrapper) {
			return ((Wrapper) wrappedScriptable).unwrap();
		}
		
		return wrappedScriptable;
	}

	public static Class<?>[] parseMethodSignature(String signature) {
		List<String> parsedArgList = new ArrayList<String>();
		int leftParenPos = signature.indexOf("(");
		int rightParenPos = signature.lastIndexOf(")");

		if (leftParenPos > 0 && rightParenPos > leftParenPos) {
			String typeAndMethodName = signature.substring(0, signature.indexOf("("));
			String typeAndMethodNameArr[] = typeAndMethodName.split("\\s");
			if (typeAndMethodNameArr.length != 2 || typeAndMethodNameArr[0].isEmpty()) {
				return null;
			}
			//parsedArgList.add(typeAndMethodNameArr[0]);
		} else {
			return null;
		}

		String callParams = signature.substring(leftParenPos + 1, rightParenPos);
		callParams = callParams.replaceAll("\\s+", "");
		if (callParams != null && !callParams.isEmpty()) {
			String[] params = callParams.split("\\,");
			for (String param : params) {
				if (param.isEmpty()) {
					return null;
				}
				parsedArgList.add(param);
			}
		}
		
		Class<?>[] types = new Class<?>[parsedArgList.size()];
		
		for (int i = 0; i < parsedArgList.size(); i++) {
			String argTypeName = parsedArgList.get(i);
			
			if (i == 0 && argTypeName.equals("void")) {
				types[i] = null;
				continue;
			}
			
			try {
				types[i] = ClassUtils.getClass(argTypeName);
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		
		return types;
	}

	@Override
	public Object get(int index, Scriptable start) {
		return wrappedScriptable.get(index, start);
	}

	@Override
	public boolean has(String name, Scriptable start) {
		return wrappedScriptable.has(name, start);
	}

	@Override
	public boolean has(int index, Scriptable start) {
		return wrappedScriptable.has(index, start);
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		wrappedScriptable.put(name, start, value);
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		wrappedScriptable.put(index, start, value);
	}

	@Override
	public void delete(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int index) {
		wrappedScriptable.delete(index);
	}

	@Override
	public Scriptable getPrototype() {
		return wrappedScriptable.getPrototype();
	}

	@Override
	public void setPrototype(Scriptable prototype) {
		wrappedScriptable.setPrototype(prototype);
	}

	@Override
	public Scriptable getParentScope() {
		return wrappedScriptable.getParentScope();
	}

	@Override
	public void setParentScope(Scriptable parent) {
		wrappedScriptable.setParentScope(parent);
	}

	@Override
	public Object[] getIds() {
		return wrappedScriptable.getIds();
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		return wrappedScriptable.getDefaultValue(hint);
	}

	@Override
	public boolean hasInstance(Scriptable instance) {
		return wrappedScriptable.hasInstance(instance);
	}
	
	protected boolean isFunctionVisible(BaseFunction function) {
		String name = function.getFunctionName();
		String methodsString = function.toString();
		Class<?>[] parameterTypes = parseMethodSignature(methodsString);
		
		if (parameterTypes == null) {
			throw new InternalException("Unable to parse method parameters for sandoboxing!");
		}
		
		Method method = null;
		try {
			method = wrappedObjectType.getMethod(name, parameterTypes);
		} catch (Exception e) {
			throw new InternalException(e);
		}
		
		Boolean isMethodVisible = visitedMethods.get(method);
		 
		if (isMethodVisible == null) {
			isMethodVisible = shutter.isMethodVisible(wrappedObject, method); 
			visitedMethods.put(method, isMethodVisible);
		}
		
		return isMethodVisible;
	}

	protected boolean isFieldVisible(String name) {
		Boolean isFieldVisible = visitedMethods.get(name);
		 
		if (isFieldVisible == null) {
			isFieldVisible = shutter.isFieldVisible(wrappedObject, name); 
			visitedFields.put(name, isFieldVisible);
		}
		
		return isFieldVisible;
		
	}
}
